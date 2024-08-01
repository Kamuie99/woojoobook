import '../styles/LinkList.css'
import LinkItem from './LinkItem';
import { LuBookPlus } from "react-icons/lu";
import { FaBook } from "react-icons/fa";
import { IoLibraryOutline } from "react-icons/io5";
import { GoLaw } from "react-icons/go";
import { useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext';

const LinkList = () => {
  const { sub } = useContext(AuthContext)

  return (
    <div className="LinkList">
      <LinkItem icon={<LuBookPlus color='black' size='40' />} 
        text={'내 책 등록하기'} 
        to='/bookregister'
      />
      <LinkItem icon={<FaBook color='black' size='40'/>} 
        text={'내 활동으로 이동'}
        to={`/${sub}/myactivity`}
      />
      <LinkItem icon={<IoLibraryOutline color='black' size='40' />} 
        text={'내 서재로 이동'}
        to={`/${sub}/mylibrary`}
      />
      <LinkItem icon={<GoLaw color='black' size='40'/>} 
        text={'내 책 관리하기'} 
        to={`/${sub}/mybook`}
      />
    </div>
  )
}

export default LinkList;