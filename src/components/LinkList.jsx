import '../styles/LinkList.css'
import LinkItem from './LinkItem';
import { LuBookPlus } from "react-icons/lu";
import { FaBook } from "react-icons/fa";
import { IoLibraryOutline } from "react-icons/io5";
import { useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import { BsFillPersonLinesFill } from "react-icons/bs";
import { RiCustomerService2Line } from "react-icons/ri";

const LinkList = () => {
  const { sub } = useContext(AuthContext)

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