package ru.currence.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.currence.app.dao.CurrencyDAO;
import ru.currence.app.dom.DomXml;
import ru.currence.app.model.Currency;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.*;

@Controller
@RequestMapping("/currency")
public class CurrencyController {

    private final DomXml domXml;
    private final CurrencyDAO currencyDAO;
    private static SimpleDateFormat formatter;
    private static Date date;
    @Autowired
    public CurrencyController(DomXml domXml, CurrencyDAO currencyDAO) throws Exception {
        this.domXml = domXml;
        this.currencyDAO = currencyDAO;
        formatter= new SimpleDateFormat("dd.MM.yyyy");
         date = new Date(System.currentTimeMillis());

        if(currencyDAO.checkExistDate(date)==false)
        {
          domXml.parsingPageWithCurrency(date, null, null, currencyDAO, 1, null);
        }
    }


/*    @GetMapping("/daily-currency-list1")
    public String  getCurrencyList1(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateCur,
                                    Model model) throws Exception {

        //domXml.parsingInfoAboutCurrency(currencyDAO,"0");
        //domXml.parsingInfoAboutCurrency(currencyDAO,"1");
       // domXml.parsingPageWithCurrency(dateCur, dateEnd, code, currencyDAO,2);
    *//*for(Currency cur : currencyDAO.showCurrencyList(dateCur))
    {
        System.out.println(cur.getidCurrencyCbru()+" "+cur.getCurrencyValue()+" "+cur.getDateRequest());
    }*//*

        return null;
    }*/



        @GetMapping("/daily-currency-list")
        public String getCurrencyList(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateCur,
                                  Model model) throws Exception {


        int day= checkDate(dateCur, date);
        if(day>=0 && day<=31)
        {
               // model.addAttribute("currency_",currencyDAO.showCurrencyListOnDate(date));
            model.addAttribute("currency_", currencyDAO.showCurrencyList(date));
        }
        else if(day>31)
        {
            return "error";
        }
        else {
           // List<Currency> curList = currencyDAO.showCurrencyListOnDate(dateCur);
            List<Currency> curList = currencyDAO.showCurrencyList(dateCur);
            if (curList.isEmpty()) {
               // domXml.parsingXML(dateCur, 1, currencyDAO);
                //curList = currencyDAO.showCurrencyListOnDate(dateCur);
                domXml.parsingPageWithCurrency(dateCur, null, null, currencyDAO, 1, null);
                curList=currencyDAO.showCurrencyList(dateCur);
                if (curList.isEmpty()) {
                    return "error";
                }
            }
            model.addAttribute("currency_", curList);
        }


            return "daily_currency_list";
        }




    @GetMapping("/daily-currency")
        public String getCurrencyConcrete(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateCur,
                                          @RequestParam("code") String code, Model model) throws Exception
        {

            int day = checkDate(dateCur, date);
            if(day>=0&&day<=31)
            {
              //  model.addAttribute("currency_", currencyDAO.showCurrencyOnDate(date,code));
                model.addAttribute("currency_", currencyDAO.showCurrencyDate(date,code));
            }
            else if(day>31)
            {
                return  "error";
            }
            else{

                //Currency cur =currencyDAO.showCurrencyOnDate(dateCur, code);
                Currency cur = currencyDAO.showCurrencyDate(dateCur, code);
            if(cur==null)
            {
               /* domXml.parsingXML(dateCur,1, currencyDAO);
                cur =currencyDAO.showCurrencyOnDate(dateCur, code);*/
                domXml.parsingPageWithCurrency(dateCur, null,null, currencyDAO,1, null );
                cur=currencyDAO.showCurrencyDate(dateCur, code);
                if(cur==null)
                {
                    return "error";
                }

            }
                model.addAttribute("currency_", cur);
            }

          return "daily_currency";
        }

    @GetMapping("/period-currency")
    public String getCurrencyPeriod(@RequestParam("dateStart") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateCurStart,
                                    @RequestParam("dateEnd") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateCurEnd,
                                    @RequestParam("code") String code, Model model) throws Exception {

        List<Currency> curPeriod = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Currency curTmp;
        int day = checkDate(dateCurStart, date);
        if (day > 0) {
            return "error";
        } else if (dateCurStart.compareTo(dateCurEnd) >= 0) {
            return "error";
        } else {
            Date dateTmp=dateCurStart;
          while (dateTmp.compareTo(dateCurEnd) <= 0) {

             curTmp = currencyDAO.showCurrencyDate(dateTmp, code);
              if (curTmp != null) {

                  curPeriod.add(curTmp);
              }
              calendar.setTime(dateTmp);
              calendar.add(Calendar.DAY_OF_MONTH, 1);
              dateTmp = calendar.getTime();
          }
          day=checkDate(dateCurStart, dateCurEnd);
          if(curPeriod.size()!=Math.abs(day)+1)
          {
              curPeriod=domXml.parsingPageWithCurrency(dateCurStart, dateCurEnd, code, currencyDAO, 2, curPeriod);
          }
          if(curPeriod.size()!=Math.abs(day)+1)
          {
              curPeriod.clear();
              List<Date> dateList = new ArrayList<>();
              dateTmp=dateCurStart;
              while (dateTmp.compareTo(dateCurEnd) <= 0)
              {

                  curTmp = currencyDAO.showCurrencyDate(dateTmp, code);
                  if (curTmp != null) {

                      curPeriod.add(curTmp);
                  }
                  else
                  {
                      dateList.add(dateTmp);
                  }
                  calendar.setTime(dateTmp);
                  calendar.add(Calendar.DAY_OF_MONTH, 1);
                  dateTmp = calendar.getTime();
              }

              if(curPeriod.get(0).getDateRequest().compareTo(dateCurStart)!=0)
              {
                  domXml.parsingPageWithCurrency(dateCurStart, null, null, currencyDAO, 1, null);
                  curPeriod.add(0,currencyDAO.showCurrencyDate(dateCurStart, code));
              }
              if(curPeriod.get(0).getDateRequest().compareTo(dateCurEnd)==0)
              {
                  domXml.parsingPageWithCurrency(dateCurStart,null, null, currencyDAO, 1, null);
                  curPeriod.add(0,currencyDAO.showCurrencyDate(dateCurStart, code));
              }

              for(Date d : dateList)
              {
               for(int i=curPeriod.size()-1; i>=0; i--)
               {
                   if(curPeriod.get(i).getDateRequest().compareTo(d)<0)
                   {
                       Currency c = new Currency();
                       c.setIdCurrencyCbru(curPeriod.get(i).getidCurrencyCbru());
                       c.setCurrencyValue(curPeriod.get(i).getCurrencyValue());
                       c.setParentCode(curPeriod.get(i).getParentCode());
                       c.setNumCode(curPeriod.get(i).getNumCode());
                       c.setCharCode(curPeriod.get(i).getCharCode());
                       c.setNameCurrency(curPeriod.get(i).getNameCurrency());
                       c.setNominal(curPeriod.get(i).getNominal());
                       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                       String sd= simpleDateFormat.format(d);
                       d=simpleDateFormat.parse(sd);
                       c.setDateRequest(d);
                       curPeriod.add(i+1, c);
                       currencyDAO.insertCurrencyValue(c);
                       break;
                   }
               }
              }
          }

          model.addAttribute("currency", curPeriod);
      }



        return "period_currency";
    }


    public int checkDate(Date dateCur, Date dateCom)
    {
       /* Calendar cal1 = new GregorianCalendar();
        cal1.setTime(dateCur);
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(dateCom);

        int day= cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR);
        Period per = Period.between(dateCur.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        day=per.getDays();*/

        long milliseconds = dateCur.getTime() - dateCom.getTime();

       int  day = (int) (milliseconds / (24 * 60 * 60 * 1000));

        return day;
    }


}
