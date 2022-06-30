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
import java.util.Date;

@Component
public class DomXml {
   // private static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
         //   SpringConfig.class
    //);
  //  private HttpResponse;



public void parsingXML(Date date_cur, int par, CurrencyDAO currencyDAO) throws Exception{

        //public static void main(String[] args) throws Exception {

      //  List<Currency> currencyList = new ArrayList<>();
 /*       DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/currency_db");
        dataSource.setUsername("postgres");
        dataSource.setPassword("1234");
        CurrencyDAO dao= new CurrencyDAO(new JdbcTemplate(dataSource));
*/

        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            String urlRequest="";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            switch(par)
            {
                case 1:

                    urlRequest="http://www.cbr.ru/scripts/XML_daily.asp?date_req="+simpleDateFormat.format(date_cur);
                    break;

            }

           // Document document = documentBuilder.parse("http://www.cbr.ru/scripts/XML_daily.asp?date_req=11/01/2022");
            Document document = documentBuilder.parse(urlRequest);

            // Получаем корневой элемент
            Node root = document.getDocumentElement();

            // Просматриваем все подэлементы корневого - т.е. книги
            NodeList currencys = root.getChildNodes();
            for (int i = 0; i < currencys.getLength(); i++) {
                Node currency = currencys.item(i);
                NamedNodeMap currencyAttribute = currency.getAttributes();
                Currency concreteCurrency = new Currency();
                if(currencyAttribute!=null) {

                    concreteCurrency.setidCurrencyCbru(currencyAttribute.getNamedItem("ID").getNodeValue());
                }
                // Если нода не текст, то это книга - заходим внутрь
                if (currency.getNodeType() != Node.TEXT_NODE) {
                    NodeList currencyTmp = currency.getChildNodes();
                    for(int j = 0; j < currencyTmp.getLength(); j++) {
                        Node currencyTmp1 = currencyTmp.item(j);
                        // Если нода не текст, то это один из параметров книги - печатаем
                        if (currencyTmp1.getNodeType() != Node.TEXT_NODE) {
                            switch (currencyTmp1.getNodeName())
                            {
                                case "NumCode":
                                    concreteCurrency.setNumCode(currencyTmp1.getChildNodes().item(0).getNodeValue());
                                    break;
                                case "CharCode":
                                    concreteCurrency.setCharCode(currencyTmp1.getChildNodes().item(0).getNodeValue());
                                    break;
                                case "Name":
                                    concreteCurrency.setNameCurrency(currencyTmp1.getChildNodes().item(0).getNodeValue());
                                    break;
                                case "Value":
                                    concreteCurrency.setValueCurrency(Double.parseDouble(
                                            currencyTmp1.getChildNodes().item(0).getNodeValue().
                                                    replace(",",".")));
                                    break;
                            }

                        }

                    }

                    currencyDAO.insert(concreteCurrency, date_cur);

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

      //  context.close();
    }
}
