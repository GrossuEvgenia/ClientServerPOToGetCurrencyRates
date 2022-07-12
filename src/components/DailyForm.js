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

function DailyForm () {
    const [isLoading, setIsLoading] = useState(false);
    const [date, setDate] = useState(null)
    const [state, setState] = useState(null)
    const [displayData, setDisplayData] = useState(null)
    async function fetchPosts(date) {
        setDate(null)
        try {
          const url = ('http://localhost:8080/currency/all?' + new URLSearchParams({date: date.toLocaleDateString('fr-CH')}))
          console.log(url)
          const response = await axios.get(url)
          const temp = response.data
          setState(1)
          //const tempDate = new Date(response.data.currency.dateRec)
          //const tempDateStr = tempDate.getFullYear() + '-' + tempDate.getMonth() + '-' + tempDate.getDate()
          /* const tempYear = new Intl.DateTimeFormat('en', {year: 'numeric'}).format(tempDate)
          const tempMonth = new Intl.DateTimeFormat('en', {month: 'numeric'}).format(tempDate)
          const tempDay = new Intl.DateTimeFormat('en', {day: 'numeric'}).format(tempDate) */
          console.log(temp)
          setDisplayData(temp.currency.map (
            (currency, cbId) => {
              return (
                <TableRow
                  key={cbId}
                  sx={{'&:last-child td, &:last-child th': {border: 0}}}>
                  <TableCell component="th" scope="row">{currency.cbId}</TableCell>
                  <TableCell align="left">{currency.cbIdP}</TableCell>
                  <TableCell align="left">{currency.name}</TableCell>
                  <TableCell align="left">{currency.numCode}</TableCell>
                  <TableCell align="left">{currency.charCode}</TableCell>
                  <TableCell align="left">{currency.nominal}</TableCell>
                  <TableCell align="left">{currency.value}</TableCell>
                  <TableCell align="left">{new Date(currency.dateRec).toLocaleDateString('fr-CA')}</TableCell>
                </TableRow>
              )
            }
          ))
        } catch(e) {
          setState(0)
          setDisplayData('No data found')
        }
        setIsLoading(false)
        /* console.log(state)
        setState(1)
        console.log(state) */
        /* const currencyList = temp.map(item => (
          <li key={item.cbId}>{item}</li>
        ))
        console.log(currencyList)
        return currencyList */
      }
    
      const handleClick = (e) => {
        e.preventDefault()
        if(date !== null) {
          setIsLoading(true)
          fetchPosts(date)
        } else {
          setState(0)
          setDisplayData('Empty request')
        }
        /* e.preventDefault()
        setIsLoading(true)
        fetchPosts(date) */
      }
      /* const DisplayData=response.data.map(
        (currency)=>{
            return(
                <tr>
                    <td>{currency.cbId}</td>
                    <td>{currency.cbIdP}</td>
                    <td>{currency.name}</td>
                    <td>{currency.numCode}</td>
                    <td>{currency.charCode}</td>
                    <td>{currency.nominal}</td>
                    <td>{currency.datarec}</td>
                    <td>{currency.value}</td>
                </tr>
            )
        }
    ) */
    return (
        <div>
          <h2>Search by date</h2>
          {/* <TextField id="outlined-basic" label="Input date" variant="outlined" value={date}
            onChange={
              (e) => setDate(e.target.value)}
          /> */}
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
          <LoadingButton
            variant="contained"
            onClick={handleClick}
            loading={isLoading}
            style={{backgroundColor: 'darkslateblue', padding: '15px 30px', margin: '0px 10px'}}>Fetch data</LoadingButton>
          {/* <TableContainer component={Paper}>
              <Table aria-label="simple table">
                <TableHead>
                  <TableRow>
                    <TableCell>CbId</TableCell>
                    <TableCell align="left">CbIdP</TableCell>
                    <TableCell align="center">Name</TableCell>
                    <TableCell align="left">NumCode</TableCell>
                    <TableCell align="left">CharCode</TableCell>
                    <TableCell align="left">Nominal</TableCell>
                    <TableCell align="left">Value</TableCell>
                    <TableCell align="left">Date</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {displayData}
                </TableBody>
              </Table>
            </TableContainer> */}
          {state === 1
            ? <TableContainer component={Paper}>
                <Table aria-label="simple table">
                  <TableHead>
                    <TableRow>
                      <TableCell>CbId</TableCell>
                      <TableCell align="left">CbIdP</TableCell>
                      <TableCell align="center">Name</TableCell>
                      <TableCell align="left">NumCode</TableCell>
                      <TableCell align="left">CharCode</TableCell>
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