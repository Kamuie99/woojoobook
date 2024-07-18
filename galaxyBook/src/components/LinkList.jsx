import '../styles/LinkList.css'
import LinkItem from './LinkItem';
import { LuBookPlus } from "react-icons/lu";
import { FaBook } from "react-icons/fa";
import { IoLibraryOutline } from "react-icons/io5";
import { GoLaw } from "react-icons/go";

const LinkList = () => {
  return (
    <div className="LinkList">
      <LinkItem icon={<LuBookPlus color='black' size='40' />} 
        text={'내 책 등록하기'} 
        to='/bookregister'
      />
      <LinkItem icon={<FaBook color='black' size='40'/>} 
        text={'대여 중인 책 목록'}
        to='/myactivity'
      />
      <LinkItem icon={<IoLibraryOutline color='black' size='40' />} 
        text={'내 서재로 이동'}
        to='/mylibrary'
      />
      <LinkItem icon={<GoLaw color='black' size='40'/>} 
        text={'이용 안내/정책'} 
        to='/policy'
      />
    </div>
  )
}

export default LinkList;