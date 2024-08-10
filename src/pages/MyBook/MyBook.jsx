import React, { useEffect, useState, useContext, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { AuthContext } from "../../contexts/AuthContext"
import { FaBook } from "react-icons/fa";
import Header from '../../components/Header'
import Registered from './Registered'
import Liked from './Liked'
import Swal from 'sweetalert2'
import styles from './MyBook.module.css';


const MyBook = () => {
  const navigate = useNavigate();
  const {userId} = useParams();
  const {isLoggedIn, sub: loggedInUserId, user} = useContext(AuthContext)

  const initialContent = localStorage.getItem('MyBookContent') || 'registered';
  const [activeContent, setActiveContent] = useState(initialContent);

  useEffect(() => {
    const checkAccess = async () => {
      if (user === null) {
        return;
      }

      if (userId !== loggedInUserId) {
        Swal.fire({
          title: '접근 권한이 없습니다.',
          confirmButtonText: '확인',
          icon: "error"
        });
        navigate(-1);
      }
    }
    
    checkAccess();
  }, [isLoggedIn, userId, loggedInUserId, user, navigate]);
  
  useEffect(() => {
    localStorage.setItem('MyBookContent', activeContent);
    return () => {
      localStorage.removeItem('MyBookContent');
    }
  }, [activeContent]);

  if (!isLoggedIn || user === null) {
    return <div>Loading...</div>
  }

  const renderContent = () => {
    switch (activeContent) {
      case 'registered':
        return <Registered />;
      case 'liked':
        return <Liked />;
      default:
        return <Registered />;
    }
  };

  return (
    <>
      <Header />
      <div className={styles.title}>
        <div className={styles.titleDiv}>
          <FaBook /> 우주도서 관리
        </div>
      </div>
      <div className={styles.selector}>
        <button
          className={`${styles.selectButton} ${activeContent === 'registered' ? styles.activeButton : ''}`}
          onClick={() => setActiveContent('registered')}
        >
          나의 우주도서
        </button>
        <button
          className={`${styles.selectButton} ${activeContent === 'liked' ? styles.activeButton : ''}`}
          onClick={() => setActiveContent('liked')}
        >
          관심 등록된 책
        </button>
      </div>
      <main className={styles.main}>
        {renderContent()}
      </main>
    </>
  )
}

export default MyBook
