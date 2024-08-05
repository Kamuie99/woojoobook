import React, { useState, useEffect, useContext, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import Swal from 'sweetalert2'
import Header from "../../components/Header"
import Proceed from './Proceed'
import History from './History'
import { MdPendingActions } from "react-icons/md";
import { AuthContext } from "../../contexts/AuthContext"

const MyActivity = () => {
  const navigate = useNavigate()
  const {userId} = useParams()
  const {isLoggedIn, sub: loggedInUserId, user} = useContext(AuthContext)
  const [activeContent, setActiveContent] = useState('proceed');

  const clearLocalStorage = useCallback(() => {
    localStorage.removeItem('myActivityActiveContent');
    localStorage.removeItem('proceedActiveContent');
  }, []);

  useEffect(() => {
    const storedContent = localStorage.getItem('myActivityActiveContent');
    if (storedContent) {
      setActiveContent(storedContent);
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
    };

    checkAccess();
  }, [isLoggedIn, userId, loggedInUserId, user, navigate]);

  useEffect(() => {
    localStorage.setItem('myActivityActiveContent', activeContent);
  }, [activeContent]);

  useEffect(() => {
    return () => {
      if (!location.pathname.includes('/myactivity')) {
        clearLocalStorage();
      }
    };
  }, [location, clearLocalStorage]);

  if (!isLoggedIn || user === null) {
    return <div>Loading...</div>;
  }

  const renderContent = () => {
    switch (activeContent) {
      case 'proceed':
        return <Proceed />;
      case 'history':
        return <History
        userId={userId}
        />;
      default:
        return <Proceed />;
    }
  };

  return (
    <>
      <Header />
      <main>
        <div>
          <MdPendingActions /> 내 활동
        </div>
        <div>
          <button onClick={() => setActiveContent('proceed')}>진행 중</button>
          <button onClick={() => setActiveContent('history')}>히스토리</button>
        </div>

        {renderContent()}
      </main>
  </>
  )
}

export default MyActivity