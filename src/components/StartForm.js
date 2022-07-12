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

function StartForm() {
    const [options, setOptions] = useState(null)
    const [cbid, setCbId] = useState('')
    const [isLoading, setIsLoading] = useState(false);
    const [date, setDate] = useState(null)
    const [state, setState] = useState(null)
    const [displayData, setDisplayData] = useState(null)
    const [archive, setArchive] = useState(null)
    useEffect(() => { getArchive() }, [])
    async function getArchive() {
        const response = await axios.get('http://localhost:8080/currency/info')
        console.log(response.data.info)
        const temp = response.data
        setArchive(temp.info)
        setOptions(temp.info[0])
        setCbId(temp.info[0].cbId)
    }
    async function fetchPosts(date, cbid) {
        setDate(null)
        setCbId('')
        try {
          const url = ('http://localhost:8080/currency/single?' + new URLSearchParams({date: date.toLocaleDateString('fr-CH')})
          + '&' + new URLSearchParams({cbid: cbid}))
          console.log(url)
          const response = await axios.get(url)
          const temp = response.data
          setState(1)
          console.log(temp)
          //console.log(Object.entries(temp))
          const tempCurr = Object.entries(temp.currency)
          const tempValues = tempCurr.values()
          console.log(tempCurr)
          setDisplayData(
                <TableRow
                  sx={{'&:last-child td, &:last-child th': {border: 0}}}>
                  <TableCell component="th" scope="row">{tempValues.next().value[1]}</TableCell>
                  <TableCell align="left">{tempValues.next().value[1]}</TableCell>
                  <TableCell align="left">{tempValues.next().value[1]}</TableCell>
                  <TableCell align="left">{tempValues.next().value[1]}</TableCell>
                  <TableCell align="left">{tempValues.next().value[1]}</TableCell>
                  <TableCell align="left">{tempValues.next().value[1]}</TableCell>
                  <TableCell align="left">{new Date(tempValues.next().value[1]).toLocaleDateString('fr-CA')}</TableCell>
                  <TableCell align="left">{tempValues.next().value[1]}</TableCell>
                </TableRow>
          )
          /* setDisplayData(temp.currency.map (
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
          )) */
        } catch(e) {
          setState(0)
          setDisplayData('No data found')
        }
        setIsLoading(false)
      }
    
      const handleClick = (e) => {
        e.preventDefault()
        if(date !== null) {
          setIsLoading(true)
          fetchPosts(date, cbid)
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
          <Autocomplete
            id="combo-box-demo"
            value={options}
            options={archive}
            getOptionLabel={(option) => option.name}
            onChange={(e, value) => {
              setOptions(value)
              setCbId(value.cbId)
            }}
            sx={{width: 260, marginTop: -7, marginLeft: 32.2}}
            renderInput={(params) => <TextField {...params} label="Currency"/>}
            />
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
                      <TableCell align="left">Date</TableCell>
                      <TableCell align="left">Value</TableCell>
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
