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

function PeriodForm () {
    /* const options = [
        { name: 'USD', cbid: 'R01235' },
        { name: 'EUR', cbid: 'R01239' },
        { name: 'DKK', cbid: 'R01215' }] */
    //const options = ['R01235', 'R01239', 'R01215']
    //const [message, setMessage] = useState("Choose Currency")
    const [options, setOptions] = useState(null)
    const [isLoading, setIsLoading] = useState(false);
    const [date1, setDate1] = useState(null)
    const [date2, setDate2] = useState(null)
    const [cbid, setCbId] = useState('')
    const [state, setState] = useState(null)
    const [displayData, setDisplayData] = useState(null)
    const [archive, setArchive] = useState(null)
    const [dataCurr, setDataCurr] = useState(null)
    const dataItems = []
    useEffect(() => { getArchive() }, [])
    async function getArchive() {
        const response = await axios.get('http://localhost:8080/currency/info')
        console.log(response.data.info)
        const temp = response.data
        setArchive(temp.info)
        setOptions(temp.info[0])
        setCbId(temp.info[0].cbId)
        //setOptions(Object.keys(temp.info).map(key => [key, temp.info[key]]))
    }
    async function fetchPosts(date1, date2, cbid) {
        setDate1(null)
        setDate2(null)
        setCbId('')
        try {
          const url = ('http://localhost:8080/currency/period?' + new URLSearchParams({date1: date1.toLocaleDateString('fr-CH')})
          + '&' + new URLSearchParams({date2: date2.toLocaleDateString('fr-CH')}) + '&' + new URLSearchParams({cbid: cbid}))
          console.log(url)
          const response = await axios.get(url)
          const temp = response.data
          setState(1)
          console.log(temp)
          setDisplayData(temp.currency.map (
            (currency, cbId) => {
              const dataItem = {
                value: currency.value,
                date: new Date(currency.dateRec).toLocaleDateString()
              }
              dataItems.push(dataItem)
              console.log(dataItem)
              console.log(dataItems)
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
          setDataCurr(dataItems)
        } catch(e) {
          setState(0)
          setDisplayData('No data found')
        }
        setIsLoading(false)
      }
      const handleClick = (e) => {
        e.preventDefault()
        if(date1 !== null && date2 !== null) {
          setIsLoading(true)
          fetchPosts(date1, date2, cbid)
        } else {
          setState(0)
          setDisplayData('Empty request')
        }
      }
    return (
        <div>
          <h2>Search by date</h2>
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
          {/* <TextField id="outlined-basic" label="Input cbid" variant="outlined" value={cbid}
            onChange={
              (e) => setCbId(e.target.value)}
          /> */}
          <Autocomplete
            id="combo-box-demo"
            value={options}
            options={archive}
            getOptionLabel={(option) => option.name}
            onChange={(e, value) => {
              setOptions(value)
              setCbId(value.cbId)
            }}
            sx={{width: 260, marginTop: -7, marginLeft: 64.5}}
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
                    left:'775px'}}>Fetch data
          </LoadingButton>
          {/* <Accordion>
                <AccordionSummary
                  expandIcon={<ExpandMoreIcon />}
                  aria-controls="panel1a-content"
                  id="panel1a-header"
                >
                  <Typography>Accordion 1</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <Typography>
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse
                    malesuada lacus ex, sit amet blandit leo lobortis eget.
                  </Typography>
                </AccordionDetails>
              </Accordion> */}
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
                </AccordionDetails>
              </Accordion>
               </div>
            : <h2>{displayData}</h2>
          }
        </div>
    )
}
export default PeriodForm;