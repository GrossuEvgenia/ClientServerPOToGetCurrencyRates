package ru.currence.app.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.currence.app.model.Currency;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class CurrencyDAO {
     private final JdbcTemplate jdbcTemplate;


        @Autowired
        public CurrencyDAO(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }


        public void insert(Currency cur, Date dateCur) throws ParseException {
            dateConvert(dateCur);
            jdbcTemplate.update("INSERT INTO currency(id_currency_cbru,name_currency,char_code," +
                            "num_code,date_request,value_currency) VALUES(?, ?, ?, ?, ?,? )",cur.getidCurrencyCbru(), cur.getNameCurrency(),
                    cur.getCharCode(), cur.getNumCode(),dateCur ,cur.getValueCurrency());
        }



    public List<Currency> showCurrencyListOnDate( Date dateCur) throws ParseException {
       dateCur=dateConvert(dateCur);
        return (List<Currency>) jdbcTemplate.query("SELECT * FROM currency WHERE date_request=?",
                new Object[]{dateCur},  new BeanPropertyRowMapper<>(Currency.class));
        // stream().findAny().orElse(null);
    }
    public Currency showCurrencyOnDate( Date dateCur, String code) throws ParseException {
        dateCur=dateConvert(dateCur);
        return (Currency) jdbcTemplate.query("SELECT * FROM currency WHERE date_request=? and id_currency_cbru=?",
                new Object[]{dateCur, code} , new BeanPropertyRowMapper<>(Currency.class)).stream().findAny().orElse(null);
    }


    public Date dateConvert(Date dateCur)  throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat= sdf.format(dateCur);
        dateCur = sdf.parse(dateFormat);
        return  dateCur;
    }
}
