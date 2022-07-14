package ru.currence.app.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.currence.app.model.Currency;
import ru.currence.app.model.NameCurrency;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;


@Component
//класс для работы с бд
public class CurrencyDAO {
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public CurrencyDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //получить количество строк в архиве
    public int countRowsInBD() {
        return jdbcTemplate.queryForObject("select count(id_currency_info) from currency_info", int.class);
    }

    //проверка на наличие определенного кода
    public boolean findCodeInBD(String code) {
        Currency cur = jdbcTemplate.query("select * from curency_info where id_currency_cbru=?", new Object[]{code}, new BeanPropertyRowMapper<>(Currency.class)).
                stream().findAny().orElse(null);
        if (cur == null)
            return false;
        return true;
    }


    //заполнение таблицы с архивом валют
    public void insertCurrencyInfo(Currency cur) {
        jdbcTemplate.update("INSERT INTO currency_info(id_currency_cbru,parent_code,num_code," +
                        "char_code,nominal,name_currency) VALUES(?, ?, ?, ?, ?,? )", cur.getidCurrencyCbru(), cur.getParentCode(),
                cur.getNumCode(), cur.getCharCode(), cur.getNominal(), cur.getNameCurrency());
    }


    //получение первичного ключа в таблице currency_info
    public int getIdCurrencyInfo(String code)
    {
        return jdbcTemplate.queryForObject("select * from find_id(?)", new Object[]{code}, int.class);
    }

        //получение списка валют по дате
        public List<Currency> showCurrencyList(Date dateCur) throws Exception
        {

            return jdbcTemplate.query(" select ci.*,cv.currency_value,cv.date_request  from currency_info ci" +
                " join currency_val_daily cv on  ci.id_currency_info=cv.id_currency_info and cv.date_request=? ",
                new Object[]{dateCur}, new BeanPropertyRowMapper<>(Currency.class));
        }

        //получение периода валют
         public List<Currency> showCurrencyDateOnPeriod( Date date1, Date date2, String code, String parentCode)
        {
            return jdbcTemplate.query("select ci.*, cv.currency_value, cv.date_request from currency_info ci"+
                " join  currency_val_period cv on ci.id_currency_info=cv.id_currency_info and (ci.id_currency_cbru=? " +
                "or ci.parent_code=?) and cv.date_request between ? " +
                "and ?  order by cv.date_request", new Object[]{code,parentCode, date1, date2},
                new BeanPropertyRowMapper<>(Currency.class) );
        }

        //получение одной данной из таблицы с периодом
          public Currency showCurrencyDateOnPeriodOne( Date date, String code)
        {
            return (Currency) jdbcTemplate.query("select ci.*, cv.currency_value, cv.date_request from currency_info ci"+
                " join currency_val_period cv on ci.id_currency_info=cv.id_currency_info and (ci.id_currency_cbru=? or ci.parent_code=?) and cv.date_request=?",
                new Object[]{code, code, date}, new BeanPropertyRowMapper<>(Currency.class) ).
                stream().findAny().orElse(null);
        }



        //получение конкретной валюты по дате и коду
         public Currency showCurrencyDate(Date dateCur, String code)
        {
            return jdbcTemplate.query(" select ci.*,cv.currency_value,cv.date_request  from currency_info ci" +
                        " join currency_val_daily cv on  ci.id_currency_info=cv.id_currency_info and cv.date_request=? " +
                        "and (ci.id_currency_cbru=? or ci.parent_code=?)",
                new Object[]{dateCur, code, code}, new BeanPropertyRowMapper<>(Currency.class)).
                stream().findAny().orElse(null);
        }


    //получение списка с именами и кодами валют
    public List<NameCurrency> getNameCurrency()
    {
        return jdbcTemplate.query("select id_currency_info, id_currency_cbru, " +
                "name_currency from currency_info", new BeanPropertyRowMapper<>(NameCurrency.class));
    }

    //получение родительского кода по ID
    public String getPanentCodeForId(String code)
    {

    return jdbcTemplate.queryForObject("select * from find_parent_code(?)",
            new Object[]{code}, String.class);
    }

    //проверка на наличие определенной даты
        public boolean checkExistDate(Date dateCur)
        {
            Currency cur = jdbcTemplate.query("select date_request from currency_val_daily where date_request=?",
                    new Object[]{dateCur},new BeanPropertyRowMapper<>(Currency.class)).
                    stream().findAny().orElse(null);
            if(cur==null)
                return false;
            return true;
        }

     //заполнение таблицы с периодом
    public int[] batchInsert(List<Currency> curlist) {
        int id=getIdCurrencyInfo(curlist.get(0).getidCurrencyCbru());
        return this.jdbcTemplate.batchUpdate(
                "insert into currency_val_period (id_currency_info,currency_value,date_request) values(?,?,?)",
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1,id );
                        ps.setDouble(2, curlist.get(i).getCurrencyValue());
                        ps.setDate(3,  new java.sql.Date(curlist.get(i).getDateRequest().getTime()));
                    }

                    public int getBatchSize() {
                        return curlist.size();
                    }

                });
    }

    //заполнение таблицы по дням
    public int[] batchInsertDaily(List<Currency> curlist) {

        return this.jdbcTemplate.batchUpdate(
                "insert into currency_val_daily (id_currency_info,currency_value,date_request) values(?,?,?)",
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1,getIdCurrencyInfo(curlist.get(i).getidCurrencyCbru()) );
                        ps.setDouble(2, curlist.get(i).getCurrencyValue());
                        ps.setDate(3,  new java.sql.Date(curlist.get(i).getDateRequest().getTime()));
                    }

                    public int getBatchSize() {
                        return curlist.size();
                    }

                });
    }

    //вставка данных в таблицу currency_request
   public void insertInRequestTable(Date date1, Date date2, String code)
   {
       jdbcTemplate.update("INSERT INTO currency_request(id_currecy,start_date,end_date) VALUES(?, ?, ?)",
               code,date1,date2);
   }
   //проверка на то есть ли запрос в базе
   public boolean checkRequest(Date date1, Date date2, String code)
   {
    return jdbcTemplate.queryForObject("select * from check_request(?,?,?)", new Object[]{date1,date2,code},
            boolean.class);
   }

   //проверка находится ли заданный период левее, тех что есть бд
   public  Date checkPeriodStart(Date date1, Date date2, String code)
   {
       return jdbcTemplate.query("select start_date from currency_request " +
               "where id_currecy=? and start_date>=? and end_date>=?", new  Object[]{code,date1,date2},
               new SingleColumnRowMapper<Date>()).stream().findAny().orElse(null);
   }

    //проверка находится ли заданный период правее, тех что есть бд
    public  Date checkPeriodEnd(Date date1, Date date2, String code)
    {
        return jdbcTemplate.query("select end_date from currency_request " +
                        "where id_currecy=? and start_date<=? and end_date<=?", new  Object[]{code,date1,date2},
                new SingleColumnRowMapper<Date>()).stream().findAny().orElse(null);
    }


}
