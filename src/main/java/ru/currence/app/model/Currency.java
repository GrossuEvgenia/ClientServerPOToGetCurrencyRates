package ru.currence.app.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Date;

@Component
@XmlRootElement(name="currency_")
@XmlAccessorType(XmlAccessType.NONE)
//класс валюты
public class Currency  implements Serializable {


    @XmlAttribute
        private String idCurrencyCbru;
    @XmlElement
    private String parentCode;
    @XmlElement
        private String nameCurrency;
    @XmlElement
        private String charCode;
    @XmlElement
        private String numCode;
    @XmlElement

    private double nominal;
    @XmlElement
        private double currencyValue;
    @XmlElement
    private Date dateRequest;

        @Autowired
        public Currency(){}

        public Currency(String idCurrencyCbru, String parentCode, String nameCurrency, String charCode, String numCode,
                        double nominal, double currencyValue, Date dateRequest)
        {
         this.idCurrencyCbru=idCurrencyCbru;
         this.parentCode=parentCode;
         this.nameCurrency=nameCurrency;
         this.charCode=charCode;
         this.numCode=numCode;
         this.nominal=nominal;
         this.currencyValue=currencyValue;
         this.dateRequest=dateRequest;
        }

        public double getCurrencyValue() {
            return currencyValue;
        }

        public String getCharCode() {
            return charCode;
        }

        public String getidCurrencyCbru() {
            return idCurrencyCbru;
        }

        public String getNameCurrency() {
            return nameCurrency;
        }

        public String getNumCode() {
            return numCode;
        }

        public Date getDateRequest() {
            return dateRequest;
        }

        public double getNominal() {
            return nominal;
        }

        public String getParentCode() {
            return parentCode;
        }

        public void setIdCurrencyCbru(String idCurrencyCbru) {
            this.idCurrencyCbru = idCurrencyCbru;
        }

        public void setCharCode(String charCode) {
            this.charCode = charCode;
        }

        public void setNameCurrency(String nameCurrency) {
            this.nameCurrency = nameCurrency;
        }

        public void setNumCode(String numCode) {
            this.numCode = numCode;
        }

        public void setCurrencyValue(double currencyValue) {
            this.currencyValue = currencyValue;
        }

        public void setDateRequest(Date dateRequest) {
            this.dateRequest = dateRequest;
        }

        public void setNominal(double nominal) {
            this.nominal = nominal;
        }

        public void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

}
