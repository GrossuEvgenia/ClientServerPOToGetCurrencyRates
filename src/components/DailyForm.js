import React, { useState } from 'react';
import { TextField, Button } from '@mui/material';
import axios from 'axios';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import LoadingButton from '@mui/lab/LoadingButton';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

//функция для отображения списка курсов валют по определенной дате
function DailyForm () {
    //индикатор загрузки для кнопки
    const [isLoading, setIsLoading] = useState(false);
    //выбранная дата
    const [date, setDate] = useState(null)
    //состояние для отображения данных в случае их обнаружения или сообщения об ошибке
    const [state, setState] = useState(null)
    //отображаемые данные (строки таблицы или сообщение об ошибке)
    const [displayData, setDisplayData] = useState(null)
    //функция для отправки запроса на сервер и получения ответа в виде json файла
    async function fetchPosts(date) {
        setDate(null)
        try {
          const url = ('http://localhost:8080/currency/daily-currency-list?' + new URLSearchParams({date: date.toLocaleDateString('fr-CH')}))
          console.log(url)
          //получение данных с сервера
          const response = await axios.get(url)
          const temp = response.data
          setState(1)
          console.log(temp)
          //преобразование полученных данных в строки таблицы
          setDisplayData(temp.currency_.map (
            (currency, idCurrencyCbru) => {
              return (
                <TableRow
                  key={idCurrencyCbru}
                  sx={{'&:last-child td, &:last-child th': {border: 0}}}>
                  <TableCell component="th" scope="row">{currency.idCurrencyCbru}</TableCell>
                  <TableCell align="left">{currency.parentCode}</TableCell>
                  <TableCell align="left">{currency.nameCurrency}</TableCell>
                  <TableCell align="left">{currency.charCode}</TableCell>
                  <TableCell align="left">{currency.numCode}</TableCell>
                  <TableCell align="left">{currency.nominal}</TableCell>
                  <TableCell align="left">{currency.currencyValue}</TableCell>
                  <TableCell align="left">{new Date(currency.dateRequest).toLocaleDateString('fr-CA')}</TableCell>
                </TableRow>
              )
            }
          ))
        } catch(e) {
            //обработка возможных ошибок
          setState(0)
          setDisplayData('No data found')
        }
        setIsLoading(false)
      }
      //функция для обработки нажатия на кнопку
      const handleClick = (e) => {
        e.preventDefault()
        //обработка запроса, если дата введена
        if(date !== null) {
          setIsLoading(true)
          fetchPosts(date)
          //если нет - вывод сообщения об ошибке
        } else {
          setState(0)
          setDisplayData('Empty request')
        }
      }
    return (
        <div>
          <h2>Search by date</h2>
          {/*календарь для выбора даты*/}
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              label="Input date"
              value={date}
              onChange={(newDate) => {
                setDate(newDate);
              }}
              renderInput={(params) => <TextField {...params} />}
            />
          </LocalizationProvider>
          {/*кнопка с индикатором загрузки*/}
          <LoadingButton
            variant="contained"
            onClick={handleClick}
            loading={isLoading}
            style={{backgroundColor: 'darkslateblue', padding: '15px 30px', margin: '0px 10px'}}>Fetch data</LoadingButton>
            {/*если данные найдены - вывод таблицы, иначе - вывод сообщения об ошибке*/}
          {state === 1
            ? <TableContainer component={Paper}>
                <Table aria-label="simple table">
                  <TableHead>
                    <TableRow>
                      <TableCell>ID</TableCell>
                      <TableCell align="left">Parent code</TableCell>
                      <TableCell align="center">Name</TableCell>
                      <TableCell align="left">Char Code</TableCell>
                      <TableCell align="left">Num Code</TableCell>
                      <TableCell align="left">Nominal</TableCell>
                      <TableCell align="left">Value</TableCell>
                      <TableCell align="left">Date</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {displayData}
                  </TableBody>
                </Table>
              </TableContainer>
            : <h2>{displayData}</h2>
          }
        </div>
    )
}
export default DailyForm;