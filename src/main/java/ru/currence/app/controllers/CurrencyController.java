package ru.currence.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import ru.currence.app.dao.CurrencyDAO;
import ru.currence.app.dom.DomXml;
import ru.currence.app.model.Currency;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/currency")
@CrossOrigin
//класс контроллер
public class CurrencyController {

    private final DomXml domXml;
    private final CurrencyDAO currencyDAO;
    private static SimpleDateFormat formatter;
    private static Date date;                       //дата текущего дня
    private final int countRowsInBd = 150;            //количество строк в архиве


    @Autowired
    public CurrencyController(DomXml domXml, CurrencyDAO currencyDAO) throws Exception {
        this.domXml = domXml;
        this.currencyDAO = currencyDAO;
        formatter = new SimpleDateFormat("dd.MM.yyyy");
        date = new Date(System.currentTimeMillis());

        //провека на наличие в архиве информации о валютах
        if (countRowsInBd < currencyDAO.countRowsInBD()) {
            domXml.parsingInfoAboutCurrency(currencyDAO, "0");
            domXml.parsingInfoAboutCurrency(currencyDAO, "1");
        }

        List<Currency> curList=new ArrayList<>();
        //проверка на наличие текущего дня в базе
        if (currencyDAO.checkExistDate(date) == false) {
            domXml.parsingPageWithCurrency(date, null, null, currencyDAO, 1, curList);
        }
    }


    //получения списка валют
    @GetMapping("/daily-currency-list")
    public MappingJackson2JsonView getCurrencyList(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateCur,
                                                   Model model) throws Exception {
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setPrettyPrint(true);
        List<Currency> currencyList = new ArrayList<>();
        //проверка введенной даты
        int day = checkDate(dateCur, date);

        //если разница между введенной датой и текуще меньше месяца
        if (day >= 0 && day <= 31) {
            model.addAttribute("currency_", currencyDAO.showCurrencyList(date));
        }
        //если больше
        else if (day > 31) {
            model.addAttribute("errormessage", "No data request");

        } else {
            //получение данных из бд
            List<Currency> curList = currencyDAO.showCurrencyList(dateCur);

            //если список пуст
            if (curList.isEmpty()) {
                //парсим запрос
                domXml.parsingPageWithCurrency(dateCur, null, null, currencyDAO, 1, currencyList);
                curList = currencyDAO.showCurrencyList(dateCur);
                if (curList.isEmpty()) {
                    model.addAttribute("errormessage", "No data request");
                }
            }
            model.addAttribute("currency_", curList);
        }
        return view;
    }


    //получение валюты на день
    @GetMapping("/daily-currency")
    public MappingJackson2JsonView getCurrencyConcrete(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateCur,
                                                       @RequestParam("code") String code, Model model) throws Exception {
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setPrettyPrint(true);
        int day = checkDate(dateCur, date);
        List<Currency> currencyList = new ArrayList<>();
        //аналогичные предыдущему методу действия
        if (day >= 0 && day <= 31) {
            model.addAttribute("currency_", currencyDAO.showCurrencyDate(date, code));
        } else if (day > 31) {
            model.addAttribute("errormessage", "No data request");
        } else {
            Currency cur = currencyDAO.showCurrencyDate(dateCur, code);
            if (cur == null) {
                domXml.parsingPageWithCurrency(dateCur, null, null, currencyDAO, 1, currencyList);
                cur = currencyDAO.showCurrencyDate(dateCur, code);
                if (cur == null) {
                    model.addAttribute("errormessage", "No data request");
                }

            }
            model.addAttribute("currency_", cur);
        }
        return view;
    }

    //получение периода
    @GetMapping("/period-currency")
    public MappingJackson2JsonView getCurrencyPeriod(@RequestParam("dateStart") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateCurStart,
                                                     @RequestParam("dateEnd") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateCurEnd,
                                                     @RequestParam("code") String code, Model model) throws Exception {
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setPrettyPrint(true);
        List<Currency> curPeriod = new ArrayList<>();
        //проверка введенных дат
        int day = checkDate(dateCurStart, date);

        //если начальная дата больше чем текущая
        if (day > 0) {
            model.addAttribute("errormessage", "No data request");
        }
        //если конечная дата раньше начальной
        else if (dateCurStart.compareTo(dateCurEnd) >= 0) {
            model.addAttribute("errormessage", "No data request");
        } else {
            //пробуем получить данные в соответсвии с введенными параметрами
            curPeriod.addAll(getPeriodList(dateCurStart,dateCurEnd,code));
            //если список пуст
            if (curPeriod.isEmpty()) {
                String parentCode = currencyDAO.getPanentCodeForId(code);
                curPeriod.clear();

                //пробуем получить данные в соответсвии с парент кодом полученным из бд
                curPeriod.addAll(getPeriodList(dateCurStart,dateCurEnd,parentCode));
                if (curPeriod.isEmpty()) {
                    model.addAttribute("errormessage", "No data request");
                } else {
                    model.addAttribute("currency_", curPeriod);
                }
            } else {
                model.addAttribute("currency_", curPeriod);
            }

        }

        return view;
    }

    //получение инфоормации о валютах
    @GetMapping("/info")
    public MappingJackson2JsonView getCurrencyIdAndName(Model model) {
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setPrettyPrint(true);
        model.addAttribute("info", currencyDAO.getNameCurrency());
        return view;
    }


    //нахождение разницы между датами
    public int checkDate(Date dateCur, Date dateCom) {
        Calendar calendar  = new GregorianCalendar();
        calendar.set(Calendar.YEAR,dateCur.getYear());
        calendar.set(Calendar.MONTH,dateCur.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH,dateCur.getDate());
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        Date first = calendar.getTime();
        calendar.set(Calendar.YEAR,dateCom.getYear());
        calendar.set(Calendar.MONTH,dateCom.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH,dateCom.getDate());
        Date second =calendar.getTime();
        long miliseconds = first.getTime()-second.getTime();
        TimeUnit time = TimeUnit.DAYS;
        int day =(int) time.convert(miliseconds,TimeUnit.MILLISECONDS);
        return day;
    }



//получение периода
    public List<Currency> getPeriodList(Date dateStart, Date dateEnd, String code) throws Exception {
        List<Currency> currencyList = new ArrayList<>();
        String parentCode= currencyDAO.getPanentCodeForId(code);
        Date dateCheck = new Date();
        Date dateMemoryStart = dateStart;
        Date dateMemoryEnd = dateEnd;
        int day=0;
        //если подобный запрос уже был или заданный период лежит внутри другого
        if(currencyDAO.checkRequest(dateStart,dateEnd,code)==false)
        {
            //берем и возвращаем данные из бд
            currencyList.addAll(currencyDAO.showCurrencyDateOnPeriod(dateStart,dateEnd,code,parentCode));
            return currencyList;
        }
        else
        {
            //елси заданный период левее уже существующего в запросе
            dateCheck=currencyDAO.checkPeriodStart(dateStart,dateEnd,code);
            if(dateCheck!=null)
            {
                dateEnd =dateCheck;

            }
            else {
                //если заданный период правее существующего в запросе
                dateCheck=currencyDAO.checkPeriodEnd(dateStart,dateEnd,code);
                if (dateCheck!=null)
                {
                    dateStart=dateCheck;
                }
            }

            //получаем разницу дат
            day=checkDate(dateStart,dateEnd);
            //пробуем получить данные из бд
            currencyList.addAll(currencyDAO.showCurrencyDateOnPeriod(dateStart,dateEnd,code,parentCode));

            //если размер полуенного списка данных отличается от разицы между датами,парсим данный период
            if(currencyList.size()!=Math.abs(day)+1)
                 domXml.parsingPageWithCurrency(dateStart,dateEnd,code,currencyDAO,2, currencyList);
            //если после парсинга дат все так же не хватает
            if(currencyList.size()!=Math.abs(day)+1)
            {
                currencyList.clear();
                //пробуем получить период
                currencyList.addAll(findPeriodDate(dateStart,dateEnd, code,parentCode));
            }

            //елси период пришел не пустой, то запоминаем запрос
            if(!currencyList.isEmpty())
                 currencyDAO.insertInRequestTable(dateMemoryStart,dateMemoryEnd,code);
        }
    currencyList.clear();
    currencyList.addAll(currencyDAO.showCurrencyDateOnPeriod(dateMemoryStart,dateMemoryEnd,code,parentCode));
    return currencyList;
    }

    //функция заполняющая пробелы после парсинга
    public List<Currency> findPeriodDate(Date dateCurStart, Date dateCurEnd, String code, String parentCode) throws Exception {
          List<Currency> curPeriod = new ArrayList<>();
            List<Date> dateList = new ArrayList<>();
            Currency curTmp;
            Date dateTmp = new Date();
            Calendar calendar = Calendar.getInstance();
            List<Currency> currencyListTmp = new ArrayList<>();
            dateTmp = dateCurStart;

            curPeriod=currencyDAO.showCurrencyDateOnPeriod(dateCurStart,dateCurEnd,code, parentCode);
            //проверяем каких дат нет в бд
            boolean flag1 =true;
            while (dateTmp.compareTo(dateCurEnd) <= 0) {
                flag1=true;
                for(Currency c: curPeriod)
                {
                    if(c.getDateRequest().compareTo(dateTmp)==0)
                    {
                        flag1=false;
                        break;
                    }
                }
                if(flag1)
                    dateList.add(dateTmp);
                calendar.setTime(dateTmp);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                dateTmp = calendar.getTime();


            }

            //если после парсинга и после проверки список все еще пуст
            if (curPeriod.isEmpty()) {
                List<Currency> curTmpList= new ArrayList<>();
                //парсим начальную дату
                domXml.parsingPageWithCurrency(dateCurStart, dateCurEnd, code, currencyDAO, 1, curTmpList);
                curTmp = currencyDAO.showCurrencyDateOnPeriodOne(dateCurStart, code);
                if (curTmp != null) {
                    curPeriod.add(curTmp);
                }
            }
            //если список не пуст
            if (!curPeriod.isEmpty()) {

                //проверяем является ли первая дата в списке начальной
                if (curPeriod.get(0).getDateRequest().compareTo(dateCurStart) != 0) {
                    List<Currency> curTmpList= new ArrayList<>();
                    domXml.parsingPageWithCurrency(dateCurStart, dateCurEnd, code, currencyDAO, 1,curTmpList);
                    curTmp= currencyDAO.showCurrencyDateOnPeriodOne(dateCurStart, code);
                    if(curTmp!=null)
                        curPeriod.add(0,curTmp);
                    else {
                        dateCurStart=curPeriod.get(0).getDateRequest();
                    }
                }
                //проверяем является ли первая дата в списке конечной
                if (curPeriod.get(0).getDateRequest().compareTo(dateCurEnd) == 0) {
                    List<Currency> curTmpList= new ArrayList<>();
                    domXml.parsingPageWithCurrency(dateCurStart, dateCurEnd, code, currencyDAO, 1, curTmpList);
                    curTmp= currencyDAO.showCurrencyDateOnPeriodOne(dateCurStart, code);
                    if(curTmp!=null)
                        curPeriod.add(0,curTmp);
                    else {
                        dateCurStart=curPeriod.get(0).getDateRequest();
                    }
                }

                //с помощью предыдущих дат заполняем пробелы
                for (Date d : dateList) {
                    for (int i = curPeriod.size() - 1; i >= 0; i--) {
                        if (curPeriod.get(i).getDateRequest().compareTo(d) < 0) {
                            Currency c = new Currency(curPeriod.get(i).getidCurrencyCbru(),
                                    curPeriod.get(i).getParentCode(),
                                    curPeriod.get(i).getNameCurrency(),
                                    curPeriod.get(i).getCharCode(),
                                    curPeriod.get(i).getNumCode(),
                                    curPeriod.get(i).getNominal(),
                                    curPeriod.get(i).getCurrencyValue(),
                                    d);
                            curPeriod.add(i + 1, c);
                            currencyListTmp.add(c);
                            break;
                        }
                    }
                }
                if(!currencyListTmp.isEmpty())
                    currencyDAO.batchInsert(currencyListTmp);
            }

        return curPeriod;
    }
}
