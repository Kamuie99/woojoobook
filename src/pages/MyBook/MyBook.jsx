import React, { useEffect, useState, useContext, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { AuthContext } from "../../contexts/AuthContext"
import Header from '../../components/Header'
import Registered from './Registered'
import Liked from './Liked'
import Swal from 'sweetalert2'


const MyBook = () => {
  const navigate = useNavigate();
  const {userId} = useParams();
  const {isLoggedIn, sub: loggedInUserId, user} = useContext(AuthContext)
  const [activeContent, setActiveContent] = useState('registered');

  const clearLocalStorage = useCallback(() => {
    localStorage.removeItem('myActivityActiveContent');
    localStorage.removeItem('proceedActiveContent');
  }, []);

  useEffect(() => {
    const storedCotent = localStorage.getItem('myBookActiveContent');
    if (storedCotent) {
      setActiveContent(storedCotent);
    }

    return () => {
      clearLocalStorage();
    };
  }, [clearLocalStorage]);

  useEffect(() => {
    const checkAccess = async () => {
      if (user === null) {
        return;
      }

      if (userId !== loggedInUserId) {
        Swal.fire({
          title: "접근 권한이 없습니다",
          icon: "error"
        });
        navigate(-1);
      }
    }

    checkAccess();
  }, [isLoggedIn, userId, loggedInUserId, user, navigate]);

  useEffect(() => {
    localStorage.setItem('myBookActiveContent', activeContent);
  }, [activeContent]);

  useEffect(() => {
    return () => {
      if (!location.pathname.includes('/mybook')) {
        clearLocalStorage();
      }
    };
  }, [location, clearLocalStorage]);

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
      <main>
        <div>
          내 책 관리
        </div>
        <div>
          <button onClick={() => setActiveContent('registered')}>내 책 관리</button>
          <button onClick={() => setActiveContent('liked')}>관심 등록된 책</button>
        </div>

        {renderContent()}
      </main>
    </>
  )
}

export default MyBook