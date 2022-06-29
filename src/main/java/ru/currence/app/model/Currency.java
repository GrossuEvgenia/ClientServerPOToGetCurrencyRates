package ru.currence.app.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Currency {

    private String idCurrencyCbru;
    private String nameCurrency;
    private String charCode;
    private String numCode;
    private double valueCurrency;

    @Autowired
    public Currency(){}

    public double getValueCurrency() {
        return valueCurrency;
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

    public void setidCurrencyCbru(String idCurrencyCbru) {
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

    public void setValueCurrency(double valueCurrency) {
        this.valueCurrency = valueCurrency;
    }
}
