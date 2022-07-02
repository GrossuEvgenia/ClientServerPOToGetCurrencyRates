package ru.currence.app.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Currency {

    private String idCurrencyCbru;
    private String nameCurrency;
    private String charCode;
    private String numCode;
    private double currencyValue;
    private Date dateRequest;

    private double nominal;

    private String parentCode;

    @Autowired
    public Currency(){}

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

    public int parseDateToInt()
    {
        int day;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateStr = simpleDateFormat.format(dateRequest);
        String date [] = dateStr.split(".");
        day=Integer.parseInt(date[0]);
        return day;
    }
}
