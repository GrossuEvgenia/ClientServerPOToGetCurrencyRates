import React, { useEffect, useState, PureComponent } from 'react';
import { TextField } from '@mui/material';
import axios from 'axios';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from "recharts";
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import Typography from '@mui/material/Typography';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import Autocomplete from '@mui/material/Autocomplete';
import LoadingButton from '@mui/lab/LoadingButton';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

//функция для отображения списка курсов валюты за определенный период
function PeriodForm () {
    //выбранное значение (название валюты) в выпадающем списке
    const [options, setOptions] = useState(null)
    //индикатор загрузки для кнопки
    const [isLoading, setIsLoading] = useState(false);
    //первая дата периода
    const [date1, setDate1] = useState(null)
    //вторая дата периода
    const [date2, setDate2] = useState(null)
    //id код валюты
    const [cbid, setCbId] = useState('')
    //состояние для отображения данных в случае их обнаружения или сообщения об ошибке
    const [state, setState] = useState(null)
    //отображаемые данные (строки таблицы или сообщение об ошибке)
    const [displayData, setDisplayData] = useState(null)
    //список значений для выпадающего списка
    const [archive, setArchive] = useState(null)
    //точки для вывода графика в формате (дата, значение курса)
    const [dataCurr, setDataCurr] = useState(null)
    //массив для точек графика
    const dataItems = []
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
    async function fetchPosts(date1, date2, cbid) {
        setDate1(null)
        setDate2(null)
        setCbId('')
        try {
          const url = ('http://localhost:8080/currency/period-currency?' + new URLSearchParams({dateStart: date1.toLocaleDateString('fr-CH')})
          + '&' + new URLSearchParams({dateEnd: date2.toLocaleDateString('fr-CH')}) + '&' + new URLSearchParams({code: cbid}))
          console.log(url)
          //получение данных с сервера
          const response = await axios.get(url)
          const temp = response.data
          setState(1)
          console.log(temp)
          //преобразование полученных данных в строки таблицы
          setDisplayData(temp.currency_.map (
            (currency, idCurrencyCbru) => {
                //точка для графика
              const dataItem = {
                value: currency.currencyValue,
                date: new Date(currency.dateRequest).toLocaleDateString()
              }
              //заполнение массива точками
              dataItems.push(dataItem)
              console.log(dataItem)
              console.log(dataItems)
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
          //присваивание заполненного массива константе для вывода графика
          setDataCurr(dataItems)
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
        //обработка запроса, если даты введены
        if(date1 !== null && date2 !== null) {
          setIsLoading(true)
          fetchPosts(date1, date2, cbid)
          //если нет - вывод сообщения об ошибке
        } else {
          setState(0)
          setDisplayData('Empty request')
        }
      }
    return (
        <div>
          <h2>Search by date</h2>
          {/*календарь для выбора начальной даты*/}
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              label="Input first date"
              value={date1}
              onChange={(newDate1) => {
                setDate1(newDate1);
              }}
              renderInput={(params) => <TextField {...params} />}
            />
          </LocalizationProvider>
          {/*календарь для выбора конечной даты*/}
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              label="Input last date"
              value={date2}
              onChange={(newDate2) => {
                setDate2(newDate2);
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
            sx={{width: 260, marginTop: -7, marginLeft: 64.5}}
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
                    left:'775px'}}>Fetch data
          </LoadingButton>
          {/*если данные найдены - вывод графика и таблицы, иначе - вывод сообщения об ошибке*/}
          {state === 1
            ? <div>
              <Accordion>
                <AccordionSummary
                  expandIcon={<ExpandMoreIcon />}
                  aria-controls="panel1a-content"
                  id="panel1a-header"
                >
                  <Typography>Chart view</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <LineChart
                    width={800}
                    height={400}
                    data={dataCurr}
                    margin={{
                      top: 5,
                      right: 30,
                      left: -10,
                      bottom: 5
                    }}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey='date' />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Line
                      type="monotone"
                      dataKey="value"
                      stroke='darkslateblue'
                      activeDot={{ r: 8 }}
                    />
                  </LineChart>
                </AccordionDetails>
              </Accordion>
              <Accordion>
                <AccordionSummary
                  expandIcon={<ExpandMoreIcon />}
                  aria-controls="panel1a-content"
                  id="panel1a-header"
                >
                  <Typography>Table view</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <TableContainer component={Paper}>
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
                </AccordionDetails>
              </Accordion>
               </div>
            : <h2>{displayData}</h2>
          }
        </div>
    )
}
export default PeriodForm;