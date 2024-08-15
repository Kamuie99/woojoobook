import '../styles/Header.css';
import Button from '../components/Button';
import Sidebar from '../components/Sidebar';

import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { MdKeyboardArrowLeft } from "react-icons/md";
import { IoMenu } from "react-icons/io5";

const Header2 = () => {
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const sidebarRef = useRef(null);

  const handleLeftClick = () => {
    navigate(-1);
  };

  const handleSidebarToggle = () => {
    setSidebarOpen(!sidebarOpen);
  };

  const handleClickOutside = (event) => {
    if (sidebarRef.current && !sidebarRef.current.contains(event.target)) {
      setSidebarOpen(false);
    }
  };

  useEffect(() => {
    if (sidebarOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    } else {
      document.removeEventListener('mousedown', handleClickOutside);
    }
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [sidebarOpen]);

  const menuItemStyles = {
    button: {
      '&:hover': {
        backgroundColor: 'var(--sub-color)',
        color: 'white'
      },
    },
  };

  return (
    <header className='Header'>
      <div className='header_left' onClick={handleLeftClick}>
        <Button text={<MdKeyboardArrowLeft size="40"/>} color="white"/>
      </div>
      <div className='header_center'>
      </div>
      <div className='header_right' onClick={handleSidebarToggle}>
        <Button text={<IoMenu size="30"/>} color="white"/>
      </div>
      <Sidebar 
        sidebarOpen={sidebarOpen} 
        handleSidebarToggle={handleSidebarToggle} 
        sidebarRef={sidebarRef} 
        menuItemStyles={menuItemStyles} 
      />
    </header>
  );
};

export default Header2;
