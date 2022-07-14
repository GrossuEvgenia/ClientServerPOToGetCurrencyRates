package ru.currence.app.model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import java.io.Serializable;


@Component
@XmlRootElement(name="info")
@XmlAccessorType(XmlAccessType.NONE)

//класс для получения названия и ID валюты
public class NameCurrency  implements Serializable {


    @XmlAttribute
    private int id_currency_info;
    @XmlElement
    private String idCurrencyCbru;
    @XmlElement
    private String nameCurrency;

    @Autowired
    NameCurrency(){}

    public int getId_currency_info() {
        return id_currency_info;
    }

    public void setId_currency_info(int id_currency_info) {
        this.id_currency_info = id_currency_info;
    }

    public String getNameCurrency() {
        return nameCurrency;
    }

    public String getIdCurrencyCbru() {
        return idCurrencyCbru;
    }

    public void setNameCurrency(String nameCurrency) {
        this.nameCurrency = nameCurrency;
    }

    public void setIdCurrencyCbru(String idCurrencyCbru) {
        this.idCurrencyCbru = idCurrencyCbru;
    }
}
