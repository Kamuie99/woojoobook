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
  const { isLoggedIn, logout, user, sub } = useContext(AuthContext);
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
              <p>{isLoggedIn ? `${user?.nickname}님, 환영합니다` : '로그인이 필요합니다'}</p>
            </div>
            <div className='profile_buttons'>
              {isLoggedIn ? (
                <>
                  <button className='logout_button' onClick={handleLogout}>로그아웃<IoIosLogOut /></button>
                  <Link to={`/${sub}/mypage`}>
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
          <MenuItem component={<Link to='/booklist' />}>우주 도서</MenuItem>
          <SubMenu label="나의 도서">
            <MenuItem component={<Link to='/bookregister' />}>나의 도서 등록하기 </MenuItem>
            <MenuItem component={<Link to={`/${sub}/mybook`} />}>나의 도서 관리하기 </MenuItem>
          </SubMenu>
          <MenuItem component={<Link to={`/${sub}/myactivity`} />}> 나의 활동 </MenuItem>
          <MenuItem component={<Link to={`/${sub}/mylibrary`} />}> 나의 서재 </MenuItem>
          
          <MenuItem component={<Link to='/policy' />}> 이용 안내/정책 </MenuItem>
        </Menu>

      </ProSidebar>
    </div>
  );
};

export default Sidebar;