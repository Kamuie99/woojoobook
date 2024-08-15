import { useState, useEffect, useContext, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import Swal from 'sweetalert2'
import Header from "../../components/Header"
import Proceed from './Proceed'
import History from './History'
import { BsFillPersonLinesFill } from "react-icons/bs";
import { AuthContext } from "../../contexts/AuthContext"
import styles from './MyActivity.module.css'

const MyActivity = () => {
  const navigate = useNavigate()
  const {isLoggedIn, sub: userId, user} = useContext(AuthContext)
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
    };

    checkAccess();
  }, [isLoggedIn, userId, user, navigate]);

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
        <div className={styles.titleDiv}>
          <BsFillPersonLinesFill /> 나의 활동
        </div>
        <div className={styles.contentDiv}>
          <div className={styles.buttons}>
            <button
              className={`${styles.tab} ${activeContent === 'proceed' ? styles.active : ''}`}
              onClick={() => setActiveContent('proceed')}
            >
              진행 중
            </button>
            <button
              className={`${styles.tab} ${activeContent === 'history' ? styles.active : ''}`}
              onClick={() => setActiveContent('history')}
            >
              히스토리
            </button>
          </div>
            {renderContent()}
        </div>
      </main>
  </>
  )
}

export default MyActivity