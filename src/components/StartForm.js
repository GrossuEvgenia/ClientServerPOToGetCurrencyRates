import React, { useState, useEffect } from 'react';
import { TextField, Button } from '@mui/material';
import axios from 'axios';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import Autocomplete from '@mui/material/Autocomplete';
import LoadingButton from '@mui/lab/LoadingButton';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

//функция для отображения курса валюты за определенную дату
function StartForm() {
    //выбранное значение (название валюты) в выпадающем списке
    const [options, setOptions] = useState(null)
    //id код валюты
    const [cbid, setCbId] = useState('')
    //индикатор загрузки для кнопки
    const [isLoading, setIsLoading] = useState(false);
    //выбранная дата
    const [date, setDate] = useState(null)
    //состояние для отображения данных в случае их обнаружения или сообщения об ошибке
    const [state, setState] = useState(null)
    //отображаемые данные (строки таблицы или сообщение об ошибке)
    const [displayData, setDisplayData] = useState(null)
    //список значений для выпадающего списка
    const [archive, setArchive] = useState(null)
    useEffect(() => { getArchive() }, [])
    //функция для заполнения выпадающего списка значениями (названиями валют)
    async function getArchive() {
        //получение данных с сервера
        const response = await axios.get('http://localhost:8080/currency/info')
        console.log(response.data.info)
        const temp = response.data
        //заполнение списка с установкой значения по умолчанию
        setArchive(temp.info)
        setOptions(temp.info[0])
        setCbId(temp.info[0].idCurrencyCbru)
    }
    //функция для отправки запроса на сервер и получения ответа в виде json файла
    async function fetchPosts(date, cbid) {
        setDate(null)
        setCbId('')
        try {
          const url = ('http://localhost:8080/currency/daily-currency?' + new URLSearchParams({date: date.toLocaleDateString('fr-CH')})
          + '&' + new URLSearchParams({code: cbid}))
          console.log(url)
          //получение данных с сервера
          const response = await axios.get(url)
          const temp = response.data
          setState(1)
          console.log(temp)
          //преобразование полученных данных в массив
          const tempCurr = Object.entries(temp.currency_)
          //итератор по созданному массиву
          const tempValues = tempCurr.values()
          console.log(tempCurr)
          //получение данных из массива с помощью итератора
          setDisplayData(
                <TableRow
                  sx={{'&:last-child td, &:last-child th': {border: 0}}}>
                  <TableCell component="th" scope="row">{tempValues.next().value[1]}</TableCell>
                  <TableCell align="left">{tempValues.next().value[1]}</TableCell>
                  <TableCell align="left">{tempValues.next().value[1]}</TableCell>
                  <TableCell align="left">{tempValues.next().value[1]}</TableCell>
                  <TableCell align="left">{tempValues.next().value[1]}</TableCell>
                  <TableCell align="left">{tempValues.next().value[1]}</TableCell>                
                  <TableCell align="left">{tempValues.next().value[1]}</TableCell>
                  <TableCell align="left">{new Date(tempValues.next().value[1]).toLocaleDateString('fr-CA')}</TableCell>
                </TableRow>
          )
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
          fetchPosts(date, cbid)
          //если нет - вывод сообщения об ошибке
        } else {
          setState(0)
          setDisplayData('Empty request')
        }
      }
    return (
        <div>
          <h1>Currency Viewer</h1>
          <hr/>
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
          {/*выпадающий список с названиями валют*/}
          <Autocomplete
            id="combo-box-demo"
            value={options}
            options={archive}
            getOptionLabel={(option) => option.nameCurrency}
            onChange={(e, value) => {
              setOptions(value)
              setCbId(value.idCurrencyCbru)
            }}
            sx={{width: 260, marginTop: -7, marginLeft: 32.2}}
            renderInput={(params) => <TextField {...params} label="Currency"/>}
            />
            {/*кнопка с индикатором загрузки*/}
          <LoadingButton
            variant="contained"
            onClick={handleClick}
            loading={isLoading}
            style={{backgroundColor: 'darkslateblue',
                    padding: '15px 30px',
                    margin: '0px 10px',
                    top:'-55px',
                    left:'520px'}}>Fetch data
          </LoadingButton>
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

export default StartForm;