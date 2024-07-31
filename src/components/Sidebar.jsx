import Button from '../components/Button';
import Swal from "sweetalert2";
import { Sidebar as ProSidebar, Menu, MenuItem, SubMenu } from 'react-pro-sidebar';
import { Link, useNavigate } from 'react-router-dom';
import { useContext } from 'react';
import { IoMenu } from "react-icons/io5";
import { AuthContext } from '../contexts/AuthContext';

import { IoIosLogIn, IoIosLogOut } from "react-icons/io";
import '../styles/Sidebar.css';

// eslint-disable-next-line react/prop-types
const Sidebar = ({ sidebarOpen, handleSidebarToggle, sidebarRef, menuItemStyles }) => {
  const { isLoggedIn, logout, nickname, sub } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
    Swal.fire({
      title: '로그아웃 되었습니다.',
      // text: '모든 필드를 입력해주세요.',
      icon: 'info',
      confirmButtonText: '확인'
    })
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
              <p>{isLoggedIn ? `${nickname}님, 환영합니다` : '로그인이 필요합니다'}</p>
            </div>
            <div className='profile_buttons'>
              {isLoggedIn ? (
                <>
                  <button className='logout_button' onClick={handleLogout}>로그아웃<IoIosLogOut /></button>
                  <Link to='/user-update'>
                    <button className='update_button'>내정보 수정</button>
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
          <MenuItem component={<Link to='/bookregister' />}> 내 책 등록하기 </MenuItem>
          <SubMenu label="신청내역">
            <MenuItem component={<Link to='/myactivity' />}> 신청한 내역 </MenuItem>
            <MenuItem component={<Link to='/myactivity' />}> 신청 받은 내역 </MenuItem>
          </SubMenu>
          <MenuItem component={<Link to={`/${sub}/mylibrary`} />}> 내 서재로 이동 </MenuItem>
          <MenuItem component={<Link to='/policy' />}> 이용 안내/정책 </MenuItem>
        </Menu>
      </ProSidebar>
    </div>
  );
};

export default Sidebar;