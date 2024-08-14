import { createContext, useState, useRef, useEffect, useMemo } from 'react';
import { jwtDecode } from 'jwt-decode';
import * as StompJs from '@stomp/stompjs';
import axiosInstance from '../util/axiosConfig';
import Swal from 'sweetalert2';

export const AuthContext = createContext();

// eslint-disable-next-line react/prop-types
export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [isLoggedIn, setIsLoggedIn] = useState(null);
  const [sub, setSub] = useState('');
  const [user, setUser] = useState(null);  // 유저 정보 추가 항목
  const [isConnected, setIsConnected] = useState(false);
  const client = useRef(null);
  const logoutTimer = useRef(null);

  const brokerURL = import.meta.env.VITE_APP_STOMP_BROKER_URL;

  const connectWithToken = (token) => {
    if (!token) {
      console.error('연결에 사용할 수 있는 토큰이 없습니다');
      return;
    }

    client.current = new StompJs.Client({
      brokerURL,
      connectHeaders: {
        'Authorization': `Bearer ${token}`
      },
      onConnect: () => {
        console.log('웹소켓 연결 성공');
        setIsConnected(true);
        localStorage.setItem('lastCloseTime', Date.now());
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.current.onWebSocketClose = (event) => {
      console.log('웹소켓 연결 닫힘: ' + event);
      setIsConnected(false);
    };

    client.current.onWebSocketError = (event) => {
      console.error('WebSocket error: ' + event);
    };

    client.current.onDisconnect = () => {
      console.log('연결 끊김');
      setIsConnected(false);
    };

    client.current.activate();
  };

  // 30분 동안 아무런 움직임이 없을 때 자동 로그아웃 기능 구현
  const resetLogoutTimer = () => {
    if (logoutTimer.current) {
      clearTimeout(logoutTimer.current);
    }
    logoutTimer.current = setTimeout(logout, 30 * 60 * 1000); // 30분 > 1분으로 수정
  };

  const isTokenValid = (token) => {
    if (!token) return false;
    try {
      const decodedToken = jwtDecode(token);
      return decodedToken.exp * 1000 > Date.now();
    } catch (error) {
      return false;
    }
  };

  useEffect(() => {
    const checkLoginStatus = async () => {
      const storedToken = localStorage.getItem('token');
      if (storedToken) {
        try {
            setToken(storedToken);
            setIsLoggedIn(true);
            const decodedToken = jwtDecode(storedToken);
            setSub(decodedToken.sub);
            await fetchUserDetails(storedToken);
            connectWithToken(storedToken);
            resetLogoutTimer();
  
            // 사용자 활동 감지
            const activityEvents = ['mousedown', 'keydown', 'scroll', 'touchstart'];
            const handleUserActivity = () => resetLogoutTimer();
            activityEvents.forEach(event => 
              document.addEventListener(event, handleUserActivity)
            );
  
            return () => {
              activityEvents.forEach(event => 
                document.removeEventListener(event, handleUserActivity)
              );
            };
        } catch (error) {
          console.error('로그인 상태 확인 중 오류 발생:', error);
          performLogout();
        }
      } else {
        setIsLoggedIn(false);
      }
    };
  
    checkLoginStatus();
  }, []);

  const performLogout = () => {
    setToken(null);
    localStorage.removeItem('token');
    setIsLoggedIn(false);
    setSub('');
    setUser(null);
    if (client.current) {
      client.current.deactivate();
    }
  };

  const fetchUserDetails = async (token) => {
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
      logout();
      return null;
    }
  };

  const updateUser = (updatedUserData) => {
    setUser(prevUser => ({ ...prevUser, ...updatedUserData }));
  };

  const login = async (newToken) => {
    try {
      setToken(newToken);
      localStorage.setItem('token', newToken);
      setIsLoggedIn(true);
      const decodedToken = jwtDecode(newToken);
      setSub(decodedToken.sub);
      await fetchUserDetails(newToken);
      resetLogoutTimer();
      connectWithToken(newToken); // 새로운 함수 사용
    } catch (error) {
      console.error('Login error:', error);
      logout();
    }
  };

  const logout = () => {
    performLogout();
    Swal.fire({
      title: '로그아웃 되었습니다.',
      confirmButtonText: '확인',
      icon: 'info'
    });
  };

  const value = useMemo(() => ({
    token, isLoggedIn, user, sub, client, isConnected,
    login, logout, updateUser, setUser}),
    [token, isLoggedIn, user, sub, client, isConnected]
  );

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};