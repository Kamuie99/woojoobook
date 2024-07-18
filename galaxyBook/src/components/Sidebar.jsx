import { Sidebar as ProSidebar, Menu, MenuItem, SubMenu } from 'react-pro-sidebar';
import { Link } from 'react-router-dom';
import Button from '../components/Button';
import { IoMenu } from "react-icons/io5";
import '../styles/Sidebar.css';

// eslint-disable-next-line react/prop-types
const Sidebar = ({ sidebarOpen, handleSidebarToggle, sidebarRef, menuItemStyles }) => {
  return (
    <div className={`header_sidebar ${sidebarOpen ? 'sidebar_open' : ''}`} ref={sidebarRef}>
      <ProSidebar width="250px" collapsed={!sidebarOpen}>
        <Menu iconShape="circle" menuItemStyles={menuItemStyles}>
          <div className='header_inner_button' onClick={handleSidebarToggle}>
            <Button text={<IoMenu size="30"/>} color="rgb(249, 249, 249, 0.7)"/>
          </div>
          <div className='profile_box'>
            프로필 부분
          </div>
          <MenuItem component={<Link to='/bookregister' />}> 내 책 등록하기 </MenuItem>
          <SubMenu label="신청내역">
            <MenuItem component={<Link to='/myactivity' />}> 신청한 내역 </MenuItem>
            <MenuItem component={<Link to='/myactivity' />}> 신청 받은 내역 </MenuItem>
          </SubMenu>
          <MenuItem component={<Link to='/mylibrary' />}> 내 서재로 이동 </MenuItem>
          <MenuItem component={<Link to='/policy' />}> 이용 안내/정책 </MenuItem>
        </Menu>
      </ProSidebar>
    </div>
  );
};

export default Sidebar;
