package ru.currence.app.dom;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.currence.app.dao.CurrencyDAO;
import ru.currence.app.model.Currency;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
//класс для парсинга
public class DomXml {



    //парсинг информауии о валютах
    public void parsingInfoAboutCurrency(CurrencyDAO currencyDAO, String par) throws Exception
    {
        try{
        // Создается построитель документа
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        // Создается дерево DOM документа из url запроса
        String urlRequest="https://www.cbr.ru/scripts/XML_valFull.asp?d="+par;
        Document document = documentBuilder.parse(urlRequest);

        // Получаем корневой элемент
        Node root = document.getDocumentElement();

        // Просматриваем все подэлементы корневого
        NodeList currencys = root.getChildNodes();
        for (int i = 0; i < currencys.getLength(); i++) {
            Node currency = currencys.item(i);
            NamedNodeMap currencyAttribute = currency.getAttributes();
            Currency concreteCurrency = new Currency();
            if(currencyAttribute!=null) {

                concreteCurrency.setIdCurrencyCbru(currencyAttribute.getNamedItem("ID").getNodeValue());
            }
            // Если нода не текст, то - заходим внутрь
            if (currency.getNodeType() != Node.TEXT_NODE) {
                NodeList currencyTmp = currency.getChildNodes();
                for(int j = 0; j < currencyTmp.getLength(); j++) {
                    Node currencyTmp1 = currencyTmp.item(j);
                    // Если нода не текст, то это один из параметров
                    if (currencyTmp1.getNodeType() != Node.TEXT_NODE) {
                        switch (currencyTmp1.getNodeName())
                        {
                            case "ISO_Num_Code":
                              if( currencyTmp1.getChildNodes().item(0)==null)
                              {
                                  concreteCurrency.setNumCode("not data");
                              }
                              else
                              {
                                  concreteCurrency.setNumCode(currencyTmp1.getChildNodes().item(0).getNodeValue());
                              }

                                break;
                            case "ISO_Char_Code":
                                if( currencyTmp1.getChildNodes().item(0)==null)
                                {
                                    concreteCurrency.setCharCode("not data");
                                }
                                else
                                {
                                    concreteCurrency.setCharCode(currencyTmp1.getChildNodes().item(0).getNodeValue());
                                }
                                break;
                            case "Name":
                                concreteCurrency.setNameCurrency(currencyTmp1.getChildNodes().item(0).getNodeValue());
                                break;
                            case "Nominal":
                                concreteCurrency.setNominal(Double.parseDouble(
                                        currencyTmp1.getChildNodes().item(0).getNodeValue()));
                                break;
                            case "ParentCode":
                                concreteCurrency.setParentCode(currencyTmp1.getChildNodes().item(0).getNodeValue().trim());
                        }

                    }

                }
                //если подобного кода нет то добавляем в бд
                if(currencyDAO.findCodeInBD(concreteCurrency.getidCurrencyCbru())==false) {
                    currencyDAO.insertCurrencyInfo(concreteCurrency);
                }

            }
        }


    } catch (ParserConfigurationException ex) {
        ex.printStackTrace(System.out);
    } catch (SAXException ex) {
        ex.printStackTrace(System.out);
    } catch (IOException ex) {
        ex.printStackTrace(System.out);
    }

        System.out.println("parsing sucsefuly");
    }


    //парсинг валюты
    public List<Currency> parsingPageWithCurrency(Date dateCur, Date dateEnd, String code,
                                        CurrencyDAO currencyDAO, int par, List<Currency> curPeriod) throws Exception
    {
        Date dateTmp;
        List<Currency> currencyListTmp=new ArrayList<>();
        int index=0;
        int sizeList=curPeriod.size();
        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из url запросса
            String urlRequest="";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            //проерка на полученные параметр
            switch(par)
            {
                case 1:

                    urlRequest="http://www.cbr.ru/scripts/XML_daily.asp?date_req="+simpleDateFormat.format(dateCur);
                    break;
                case 2:
                    urlRequest="https://cbr.ru/scripts/XML_dynamic.asp?date_req1="+simpleDateFormat.format(dateCur)+
                            "&date_req2="+simpleDateFormat.format(dateEnd)+"&VAL_NM_RQ="+code;
                    break;

            }

            Document document = documentBuilder.parse(urlRequest);

            // Получаем корневой элемент
            Node root = document.getDocumentElement();
            NamedNodeMap currencyAttribute = root.getAttributes();
            String id="";

            //ели парсим период
            if(par==2)
            {
                id=currencyAttribute.getNamedItem("ID").getNodeValue();
            }

            // Просматриваем все подэлементы корневого
            NodeList currencys = root.getChildNodes();
            for (int i = 0; i < currencys.getLength(); i++) {
                Node currency = currencys.item(i);
                currencyAttribute = currency.getAttributes();
                Currency concreteCurrency = new Currency();
                if(currencyAttribute!=null) {

                    //если парсим период
                    if(par==2)
                    {
                        concreteCurrency.setIdCurrencyCbru(id);
                        concreteCurrency.setDateRequest(simpleDateFormat.parse(
                                currencyAttribute.getNamedItem("Date").getNodeValue()));
                    }
                    else
                    {
                        concreteCurrency.setIdCurrencyCbru(currencyAttribute.getNamedItem("ID").getNodeValue());
                        concreteCurrency.setDateRequest(dateCur);
                    }
                }
                // Если нода не текст, то  заходим внутрь
                if (currency.getNodeType() != Node.TEXT_NODE) {
                    NodeList currencyTmp = currency.getChildNodes();
                    for(int j = 0; j < currencyTmp.getLength(); j++) {
                        Node currencyTmp1 = currencyTmp.item(j);
                        // Если нода не текст, то это один из параметров книги - печатаем
                        if (currencyTmp1.getNodeType() != Node.TEXT_NODE) {
                            switch (currencyTmp1.getNodeName())
                            {

                                case "Value":
                                    concreteCurrency.setCurrencyValue(Double.parseDouble(
                                            currencyTmp1.getChildNodes().item(0).getNodeValue().
                                                    replace(",",".")));
                                    break;
                            }

                        }

                    }
                    //если парсим по дням
                    if(par==1 && dateEnd==null)
                    {
                        //currencyDAO.insertCurrencyValueDaily(concreteCurrency);
                        curPeriod.add(concreteCurrency);
                    }
                    //если парим по дням в период
                    else if (par==1 &&dateEnd!=null && concreteCurrency.getidCurrencyCbru().equals(code) || par==2) {
                       // currencyDAO.insertCurrencyValuePeriod(concreteCurrency);
                   // }
                    //если парсим период
                    //else if(par==2)
                    //{
                        boolean flag=true;
                        //проверям есть ли дата в списке
                     /*   for(Currency c:curPeriod)
                        {
                            if(c.getDateRequest().compareTo(concreteCurrency.getDateRequest())==0)
                            {
                                flag=false;
                                break;
                            }
                        }*/

                        for(int j=index; j<curPeriod.size();j++)
                        {
                            if(curPeriod.get(j).getDateRequest().compareTo(concreteCurrency.getDateRequest())==0)
                            {
                                index=j;
                                flag=false;
                                break;
                            }
                        }


                       // if(curPeriod.contains(concreteCurrency.getDateRequest()))
                         //   flag=false;
                        if(flag)
                        {
                           // currencyDAO.insertCurrencyValuePeriod(concreteCurrency);
                            currencyListTmp.add(concreteCurrency);
                        }

                    }

                }
            }
            if(currencyListTmp.size()>curPeriod.size()&&(par==2||dateEnd!=null))
            {
                currencyDAO.batchInsert(currencyListTmp);
            }
            else
            {
currencyDAO.batchInsertDaily(curPeriod);
            }

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }

        System.out.println("parsing sucsefuly");
        return curPeriod;

    }

}


