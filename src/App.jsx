import { useEffect, useState, useContext, useRef } from 'react';
import { Routes, Route, useLocation } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Notfound from './pages/Notfound';
import BookRegister from './pages/BookRegister/BookRegister';
import Policy from './pages/Policy/Policy';
import MyBook from './pages/MyBook/MyBook';
import MyActivity from './pages/MyActivity/MyActivity';
import MyLibrary from './pages/MyLibrary/MyLibrary';
import Register from './pages/Register/Register';
import MyPage from './pages/MyPage/MyPage'
import UserUpdate from './pages/MyPage/UserUpdate';
import PasswordChange from './pages/MyPage/PasswordChange';
import ProtectedRoute from './util/ProtectedRoute';
import Chatting from './components/Chatting/Chatting';
import BookList from './pages/BookList/BookList';
import TestPage from './pages/TestPage';
import debounce from 'lodash.debounce';
import axiosInstance from './util/axiosConfig';
import { jwtDecode } from 'jwt-decode';
import { AuthContext } from './contexts/AuthContext';


// 1. "/": home 페이지
// 2. "/login": login 페이지


function App() {
  const { sub: userId, client, isConnected, token, setUser } = useContext(AuthContext);
  const [directMessage, setDirectMessage] = useState(null);
  const [newMessage, setNewMessage] = useState(() => {
    const storedValue = localStorage.getItem('newMessage')
    return storedValue === 'true';
  });
  const newMessageChatRooms = useRef({});

  const location = useLocation();
  const excludedPaths = ['/login', '/register'];

  const setNewMessageFalse = () => {
    setNewMessage(false);
    localStorage.setItem('newMessage', false);
  }

  useEffect(() => {
    setUser(null);
    const fetchUserDetails = async () => {
      if (token) {
        try {
          const response = await axiosInstance.get('/users', {
            headers: { 'Authorization': `Bearer ${token}` }
          });
    
          if (response.status === 200) {
            setUser(response.data);
            return response.data;
          } else {
            throw new Error('사용자 정보를 가져오는데 실패했습니다.');
          }
        } catch (error) {
          console.error('사용자 정보를 가져오는데 실패했습니다:', error);
          setUser(null);
          return null;
        }
      }
    }
    fetchUserDetails();
  }, [token]);

  useEffect(() => {
    if (client.current && isConnected ) {
      const destination = `/topic/user_${userId}`;
      client.current.subscribe(destination, debounce((message) => {
        console.log('수신된 메시지:', message.body);
        const messageBody = JSON.parse(message.body);
        newMessageChatRooms.current[messageBody.chatRoomId] = true;
        setNewMessage(false)
        setTimeout(() => {
          setNewMessage(true);
          localStorage.setItem('newMessage', true);
        }, 100);
      }))
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isConnected])

  useEffect(() => {
  }, [directMessage]);

  return (
    <>
      <Routes>
        <Route path="/" element={<Home/>} />
        <Route path="/login" element={<Login />} />
        <Route path='/register' element={<Register />} />
        <Route path='/policy' element={<Policy/>} />


        <Route element={<ProtectedRoute />}>        
          <Route path='/bookregister' element={<BookRegister />} />
          <Route path='/:userId/mybook' element={<MyBook/>} />
          <Route path='/:userId/myactivity' element={<MyActivity />} />
          <Route path='/:userId/mylibrary' element={<MyLibrary />} />
          <Route path='/mypage' element={<MyPage />} />
          <Route path='/userupdate' element={<UserUpdate />} />
          <Route path='/passwordchange' element={<PasswordChange />} />
          <Route path='/booklist' element={<BookList setDirectMessage={setDirectMessage}/>} />
          <Route path='/test' element={<TestPage />} />
        </Route>
          
        <Route path="*" element={<Notfound />} />
      </Routes>
      {!excludedPaths.includes(location.pathname) &&
        <Chatting
          onClose={() => {setNewMessageFalse()}}
          newMessageChatRooms={newMessageChatRooms}
          newMessage={newMessage}
          setNewMessage={setNewMessage}
          directMessage={directMessage}
          setDirectMessage={setDirectMessage}
        />
      }
    </>
  )
}

export default App
