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

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/currency")
public class CurrencyController {

    private final DomXml domXml;
    private final CurrencyDAO currencyDAO;
    @Autowired
    public CurrencyController(DomXml domXml, CurrencyDAO currencyDAO) {
        this.domXml = domXml;
        this.currencyDAO = currencyDAO;
    }
        @GetMapping("/hello-world")
        public String sayHello() {
            return "hello_world";
        }

        @GetMapping("/daily-currency-list")
        public String getCurrencyList(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateCur,
                                  Model model) throws Exception {
        List<Currency> curList =currencyDAO.showCurrencyListOnDate(dateCur);
        if(curList.isEmpty())
        {
            domXml.parsingXML(dateCur,1, currencyDAO);
            model.addAttribute("currency_",currencyDAO.showCurrencyListOnDate(dateCur));
        }
        else {
            model.addAttribute("currency_",curList);
        }
            return "daily_currency_list";
        }
        @GetMapping("/daily-currency")
        public String getCurrencyConcrete(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateCur,
                                          @RequestParam("code") String code, Model model) throws Exception
        {
            model.addAttribute("currency_", currencyDAO.showCurrencyOnDate(dateCur, code));
            if(model.getAttribute("currency_")==null)
            {
                domXml.parsingXML(dateCur,1, currencyDAO);
                model.addAttribute("currency_",currencyDAO.showCurrencyOnDate(dateCur, code));
            }
          return "daily_currency";
        }


}
