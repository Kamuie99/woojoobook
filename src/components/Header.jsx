
import '../styles/Header.css';
import Button from '../components/Button';
import LogoSmall from '../assets/LogoSmall.png';
import Sidebar from '../components/Sidebar';

import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { MdKeyboardArrowLeft } from "react-icons/md";
import { IoMenu } from "react-icons/io5";

const Header = () => {
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false); // 사이드바 열림/닫힘 상태 관리
  const sidebarRef = useRef(null); // 사이드바 참조

  const handleLeftClick = () => {
    navigate(-1); // 이전 페이지로 이동
  };

  const handleLogoClick = () => {
    navigate('/'); // 홈으로 이동
  };

  const handleSidebarToggle = () => {
    setSidebarOpen(!sidebarOpen); // 사이드바 열림/닫힘 상태 토글
  };

  const handleClickOutside = (event) => {
    if (sidebarRef.current && !sidebarRef.current.contains(event.target)) {
      setSidebarOpen(false); // 사이드바 닫기
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
        backgroundColor: 'var(--sub-color)', // 원하는 hover 배경색 설정
        color: 'white' // 원하는 hover 텍스트 색상 설정
      },
    },
  };

  return (
    <header className='Header'>
      <div className='header_left' onClick={handleLeftClick}>
        <Button text={<MdKeyboardArrowLeft size="40"/>} color="white"/>
      </div>
      <div className='header_center'>
        <img onClick={handleLogoClick} src={LogoSmall} width="40px"/>
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

export default Header;
