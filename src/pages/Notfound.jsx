import React from 'react';
import Logo from '../assets/logoWithLetter.webp'
import { Link } from 'react-router-dom';

const NotFound = () => {
  return (
    <div style={{ textAlign: 'center', marginTop: '50px' }}>
      <img src={Logo} alt="Not Found" style={{marginTop: '50px', width: '400px'}}/>
      <h1>404 - 페이지를 찾을 수 없습니다</h1>
      <p>죄송합니다. 요청하신 페이지를 찾을 수 없습니다.</p>
      <Link to="/">홈으로 돌아가기</Link>
    </div>
  );
};

export default NotFound;