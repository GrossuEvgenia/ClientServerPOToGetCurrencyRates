import React from 'react';
import { Route, Routes, Navigate } from 'react-router-dom';
import StartForm from './StartForm';
import DailyForm from './DailyForm';
import PeriodForm from './PeriodForm';

const AppRouter = () => {
    return (
        <Routes>
          <Route exact path="/" element={<StartForm/>}/>
          <Route path="/daily" element={<DailyForm/>}/>
          <Route path="/period" element={<PeriodForm/>}/>
        </Routes>
    );
};

export default AppRouter;