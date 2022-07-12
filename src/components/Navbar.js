import React from 'react';
import { Link } from 'react-router-dom';

const Navbar = () => {
    return (
        <div className='navbar'>
        <div className='navbar__links'>
          <Link to="/" className='link__text'>Start Page</Link>
          <Link to="/daily" className='link__text'>Daily currencies</Link>
          <Link to="period" className='link__text'>Period for currency</Link>
        </div>
      </div>
    );
};

export default Navbar;