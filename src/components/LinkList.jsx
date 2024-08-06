import '../styles/LinkList.css'
import LinkItem from './LinkItem';
import { LuBookPlus } from "react-icons/lu";
import { FaBook } from "react-icons/fa";
import { IoLibraryOutline } from "react-icons/io5";
// import { GoLaw } from "react-icons/go";
import { useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import { BsFillPersonLinesFill } from "react-icons/bs";

const LinkList = () => {
  const { sub } = useContext(AuthContext)

  return (
    <div className="LinkList">
      <LinkItem icon={<LuBookPlus color='black' size='40' />} 
        text={'우주도서 등록'} 
        to='/bookregister'
      />
      <LinkItem icon={<FaBook color='black' size='40'/>} 
        text={'우주도서 관리'} 
        to={`/${sub}/mybook`}
      />
      <LinkItem icon={<IoLibraryOutline color='black' size='40' />} 
        text={'나의 서재'}
        to={`/${sub}/mylibrary`}
      />
      <LinkItem icon={<BsFillPersonLinesFill color='black' size='40'/>} 
        text={'나의 활동'}
        to={`/${sub}/myactivity`}
      />
    </div>
  )
}

export default LinkList;