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
        if(currencyDAO.checkExistData(date)==false)
        {
          domXml.parsingXML(date, 1, currencyDAO);
        }
    }


        @GetMapping("/daily-currency-list")
        public String getCurrencyList(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateCur,
                                  Model model) throws Exception {


        int day= checkDate(dateCur);
        if(day>=0 && day<=31)
        {
                model.addAttribute("currency_",currencyDAO.showCurrencyListOnDate(date));
        }
        else if(day>31)
        {
            return "error";
        }
        else {
            List<Currency> curList = currencyDAO.showCurrencyListOnDate(dateCur);
            if (curList.isEmpty()) {
                domXml.parsingXML(dateCur, 1, currencyDAO);
                curList = currencyDAO.showCurrencyListOnDate(dateCur);
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

            int day = checkDate(dateCur);
            if(day>=0&&day<=31)
            {
                model.addAttribute("currency_", currencyDAO.showCurrencyOnDate(date,code));
            }
            else if(day>31)
            {
                return  "error";
            }
            else{

                Currency cur =currencyDAO.showCurrencyOnDate(dateCur, code);
            if(cur==null)
            {
                domXml.parsingXML(dateCur,1, currencyDAO);
                cur =currencyDAO.showCurrencyOnDate(dateCur, code);
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
                                    @RequestParam("code") String code, Model model) throws Exception
    {

      List<Currency> curPeriod = new ArrayList<>();
      Calendar calendar = Calendar.getInstance();
      Currency curTmp;
      int day = checkDate(dateCurStart);
      if(day>0)
      {
          return "error";
      }
      else if(dateCurStart.compareTo(dateCurEnd)>=0)
      {
          return "error";
      }

      else {
          while (dateCurStart.compareTo(dateCurEnd) <= 0) {
              curTmp = currencyDAO.showCurrencyOnDate(dateCurStart, code);
              if (curTmp == null) {
                  domXml.parsingXML(dateCurStart, 1, currencyDAO);
                  curTmp = currencyDAO.showCurrencyOnDate(dateCurStart, code);
              }

              curPeriod.add(curTmp);
              calendar.setTime(dateCurStart);
              calendar.add(Calendar.DAY_OF_MONTH, 1);
              dateCurStart = calendar.getTime();
          }

          model.addAttribute("currency", curPeriod);
      }

        return "period_currency";
    }


    public int checkDate(Date dateCur)
    {
        Calendar cal1 = new GregorianCalendar();
        cal1.setTime(dateCur);
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(date);
        int day= cal1.get(Calendar.DAY_OF_YEAR) - cal2.get(Calendar.DAY_OF_YEAR);
        return day;
    }


}
