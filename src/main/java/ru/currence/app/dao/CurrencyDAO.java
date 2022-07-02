package ru.currence.app.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
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
                    cur.getCharCode(), cur.getNumCode(),dateCur ,cur.getCurrencyValue());
        }


        public void insertCurrencyInfo(Currency cur)
        {
            jdbcTemplate.update("INSERT INTO currency_info(id_currency_cbru,parent_code,num_code," +
                            "char_code,nominal,name_currency) VALUES(?, ?, ?, ?, ?,? )",cur.getidCurrencyCbru(), cur.getParentCode(),
                    cur.getNumCode(), cur.getCharCode(), cur.getNominal(), cur.getNameCurrency());
        }

        public void insertCurrencyValue(Currency cur)
        {
            int pkCurrencyInfo = jdbcTemplate.queryForObject("Select pk_currency_info from currency_info " +
                            "where id_currency_cbru=?",
                    new Object[]{cur.getidCurrencyCbru()}, int.class);
            jdbcTemplate.update("Insert into currency_val(pk_currency_info,currency_value, date_request)" +
                    "values(?,?,?)", pkCurrencyInfo, cur.getCurrencyValue(), cur.getDateRequest());
        }


    public List<Currency> showCurrencyListOnDate( Date dateCur) throws ParseException {
       dateCur=dateConvert(dateCur);
        return (List<Currency>) jdbcTemplate.query("SELECT * FROM currency WHERE date_request=?",
                new Object[]{dateCur},  new BeanPropertyRowMapper<>(Currency.class));
        // stream().findAny().orElse(null);
    }

    public List<Currency> showCurrencyList(Date dateCur) throws Exception
    {

        return jdbcTemplate.query(" select ci.*,cv.currency_value,cv.date_request  from currency_info ci" +
                " join currency_val cv on  ci.pk_currency_info=cv.pk_currency_info and cv.date_request=? ",
                new Object[]{dateCur}, new BeanPropertyRowMapper<>(Currency.class));
    }

    public Currency showCurrencyOnDate( Date dateCur, String code) throws ParseException {
        dateCur=dateConvert(dateCur);
        return (Currency) jdbcTemplate.query("SELECT * FROM currency WHERE date_request=? and id_currency_cbru=?",
                new Object[]{dateCur, code} , new BeanPropertyRowMapper<>(Currency.class)).stream().findAny().orElse(null);
    }


    public Currency showCurrencyDate(Date dateCur, String code)
    {
        return jdbcTemplate.query(" select ci.*,cv.currency_value,cv.date_request  from currency_info ci" +
                        " join currency_val cv on  ci.pk_currency_info=cv.pk_currency_info and cv.date_request=? " +
                        "and ci.id_currency_cbru=?",
                new Object[]{dateCur, code}, new BeanPropertyRowMapper<>(Currency.class)).
                stream().findAny().orElse(null);
    }
    public Date dateConvert(Date dateCur)  throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat= sdf.format(dateCur);
        dateCur = sdf.parse(dateFormat);
        return  dateCur;
    }

    /*public boolean checkExistData( Date dateCur) throws ParseException {
        dateCur = dateConvert(dateCur);
        Currency cur = jdbcTemplate.query("SELECT * FROM currency WHERE date_request=?",
                        new Object[]{dateCur}, new BeanPropertyRowMapper<>(Currency.class)).
                stream().findAny().orElse(null);
        if (cur == null)
            return false;
        return true;
    }*/
        public boolean checkExistDate(Date dateCur)
        {
            Currency cur = jdbcTemplate.query("select date_request from currency_val where date_request=?",
                    new Object[]{dateCur},new BeanPropertyRowMapper<>(Currency.class)).
                    stream().findAny().orElse(null);
            if(cur==null)
                return false;
            return true;
        }



}
