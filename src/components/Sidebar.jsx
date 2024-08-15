import Button from '../components/Button';
import Swal from "sweetalert2";
import { Sidebar as ProSidebar, Menu, MenuItem, SubMenu } from 'react-pro-sidebar';
import { Link, useNavigate } from 'react-router-dom';
import { useContext, useEffect, useState } from 'react';
import { IoMenu, IoLibraryOutline } from "react-icons/io5";
import { AuthContext } from '../contexts/AuthContext';
import { FaSpaceAwesome } from "react-icons/fa6";
import { CgPlayListSearch } from "react-icons/cg";
import { BsFillPersonLinesFill } from "react-icons/bs";
import { RiCustomerService2Line } from "react-icons/ri";
import { IoIosLogIn, IoIosLogOut } from "react-icons/io";
import '../styles/Sidebar.css';

const Sidebar = ({ sidebarOpen, handleSidebarToggle, sidebarRef, menuItemStyles }) => {
  const { isLoggedIn, logout, user, sub } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className={`header_sidebar ${sidebarOpen ? 'sidebar_open' : ''}`} ref={sidebarRef}>
      <ProSidebar width="250px" collapsed={!sidebarOpen}>
        <Menu iconShape="circle" menuItemStyles={menuItemStyles}>
          <div className='header_inner_button' onClick={handleSidebarToggle}>
            <Button text={<IoMenu size="30"/>} color="rgb(249, 249, 249, 0.7)"/>
          </div>
          <div className='profile_box'>
            <div className='profile_nickname'>
              <p>{isLoggedIn ? `${user?.nickname}님, 환영합니다` : '로그인이 필요합니다'}</p>
            </div>
            <div className='profile_buttons'>
              {isLoggedIn ? (
                <>
                  <button className='logout_button' onClick={handleLogout}>로그아웃<IoIosLogOut /></button>
                  <Link to={`/mypage`}>
                    <button className='mypage_button'>마이페이지</button>
                  </Link>
                </>
              ) : (
                <>
                  <Link to='/login'>              
                    <button className='login_button'>로그인<IoIosLogIn /></button>
                  </Link>
                  <Link to='/register'>
                    <button className='register_button'>회원가입</button>
                  </Link>
                </>
              )}
            </div>
          </div>
          {isLoggedIn && (
            <> 
              <MenuItem component={<Link to='/booklist' />} icon={<CgPlayListSearch size={'25px'}/>}>우주 도서 검색</MenuItem>
              <SubMenu label="나의 우주도서" icon={<FaSpaceAwesome size={'18px'}/>}>
                <MenuItem component={<Link to='/bookregister' />}>우주도서 등록 </MenuItem>
                <MenuItem component={<Link to={`/${sub}/mybook`} />}>우주도서 관리 </MenuItem>
              </SubMenu>
              <MenuItem 
                component={<Link to={`/${sub}/mylibrary`} />} 
                icon={<IoLibraryOutline size={'21px'}/>}
              > 
                나의 서재 
              </MenuItem>
              <MenuItem component={<Link to={`/${sub}/myactivity`} />} icon={<BsFillPersonLinesFill size={'20px'} /> }> 나의 활동 </MenuItem>
            </>
          )}
          <MenuItem component={<Link to='/policy' />} icon={<RiCustomerService2Line size={'20px'}/>}> 이용안내 및 정책 </MenuItem>
        </Menu>
      </ProSidebar>
    </div>
  );
};

export default Sidebar;