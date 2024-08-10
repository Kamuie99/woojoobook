import '../styles/LinkList.css'
import { useNavigate } from 'react-router-dom';
import Swal from 'sweetalert2';
import LinkItem from './LinkItem';
import { LuBookPlus } from "react-icons/lu";
import { FaBook } from "react-icons/fa";
import { IoLibraryOutline } from "react-icons/io5";
// import { GoLaw } from "react-icons/go";
import { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import { BsFillPersonLinesFill } from "react-icons/bs";
import { RiCustomerService2Line } from "react-icons/ri";

const LinkList = () => {
  const { sub } = useContext(AuthContext)
  const navigate = useNavigate();

  const checkAuth = () => {
    if (!sub) {
      Swal.fire({
        title: '권한이 없습니다.',
        text: '로그인이 필요한 기능입니다',
        icon: 'error',
        confirmButtonText: '로그인',
        allowOutsideClick: false,
        allowEscapeKey: false
      }).then((result) => {
        if (result.isConfirmed) {
          navigate('/login');
        }
      });
    }
    return null;
  }

  return (
    <div className="LinkList">
      <LinkItem icon={<LuBookPlus size='40' />} 
        text={'우주도서 등록'} 
        to='/bookregister'
      />
      <LinkItem icon={<FaBook size='40'/>} 
        text={'우주도서 관리'} 
        to={sub ? `/${sub}/mybook` : '/mypage'}
      />
      <LinkItem icon={<IoLibraryOutline size='40' />} 
        text={'나의 서재'}
        to={sub ? `/${sub}/mylibrary` : '/mypage'}
      />
      <LinkItem icon={<BsFillPersonLinesFill size='40'/>} 
        text={'나의 활동'}
        to={sub ? `/${sub}/myactivity` : '/mypage'}
      />
      <LinkItem icon={<RiCustomerService2Line size='40'/>} 
        text={'이용안내 및 정책'}
        to={`/policy`}
      />
    </div>
  )
}

export default LinkList;