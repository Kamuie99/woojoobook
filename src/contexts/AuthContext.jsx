import { createContext, useState, useRef, useEffect, useMemo } from 'react';
import { jwtDecode } from 'jwt-decode';
import * as StompJs from '@stomp/stompjs';
import axiosInstance from '../util/axiosConfig';
import Swal from 'sweetalert2';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [sub, setSub] = useState('');
  const [user, setUser] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const client = useRef(null);
  const [wsConnectionStatus, setWsConnectionStatus] = useState('connected');
  const reconnectAttempts = useRef(0);
  const maxReconnectAttempts = 100;
  const reconnectDelay = 5000;

  const brokerURL = import.meta.env.VITE_APP_STOMP_BROKER_URL;

  useEffect(() => {
    const checkAuthStatus = async () => {
      setIsLoading(true);
      const token = localStorage.getItem('token');
      if (token && isTokenValid(token)) {
        setIsLoggedIn(true);
      } else {
        setIsLoggedIn(false);
      }
      setIsLoading(false);
    };

    checkAuthStatus();
  }, []);

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
        setWsConnectionStatus('connected');
        reconnectAttempts.current = 0;
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
        setIsConnected(false);
        handleWebSocketError();
      },
      reconnectDelay,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });
  
    client.current.onWebSocketClose = (event) => {
      console.log('웹소켓 연결 닫힘: ' + event);
      setIsConnected(false);
      handleWebSocketError();
    };
  
    client.current.onWebSocketError = (event) => {
      console.error('WebSocket error: ' + event);
      setIsConnected(false);
      handleWebSocketError();
    };
  
    client.current.activate();
  };

  const handleWebSocketError = () => {
    setWsConnectionStatus('disconnected');
    if (reconnectAttempts.current < maxReconnectAttempts) {
      reconnectAttempts.current += 1;
      setTimeout(() => {
        if (client.current) {
          client.current.deactivate();
          client.current.activate();
        }
      }, reconnectDelay);
    } else {
      console.error('최대 재연결 시도 횟수 초과');
      handleConnectionError();
    }
  };
  
  const handleConnectionError = () => {
    if (isConnected) {
      return;
    }
  
    Swal.fire({
      title: '연결 오류',
      text: '서버와의 연결에 문제가 발생했습니다. 다시 연결을 시도하시겠습니까?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '재연결',
      cancelButtonText: '로그아웃'
    }).then((result) => {
      if (result.isConfirmed) {
        console.log('사용자가 재연결을 선택했습니다.');
        reconnectWebSocket();
      } else {
        console.log('사용자가 로그아웃을 선택했습니다.');
        logout(true);
      }
    });
  };

  const reconnectWebSocket = () => {
    if (client.current) {
      client.current.deactivate();
      client.current.activate();
    } else {
      console.error('WebSocket client is not initialized');
      connectWithToken(token);
    }
  };

  const isTokenValid = (token) => {
    if (!token) return false;
    try {
      const decodedToken = jwtDecode(token);
      return decodedToken.exp * 1000 > Date.now();
    } catch (error) {
      console.error('토큰 검증 오류:', error);
      return false;
    }
  };

  const checkLoginStatus = () => {
    const storedToken = localStorage.getItem('token');
    const lastActiveTime = localStorage.getItem('lastActiveTime');
    const currentTime = Date.now();
  
    if (lastActiveTime && currentTime - parseInt(lastActiveTime) > 30 * 60 * 1000) {
      logout(true);
      return false;
    }
  
    if (storedToken && isTokenValid(storedToken)) {
      return true;
    }
  
    logout(true);
    return false;
  };

  useEffect(() => {
    const checkConnectionStatus = setInterval(() => {
      if (isLoggedIn && wsConnectionStatus === 'disconnected') {
        connectWithToken(token);
      }
    }, 60000);
  
    return () => clearInterval(checkConnectionStatus);
  }, [isLoggedIn, wsConnectionStatus, token]);

  useEffect(() => {
    const initializeAuth = async () => {
      if (checkLoginStatus()) {
        try {
          const storedToken = localStorage.getItem('token');
          setToken(storedToken);
          setIsLoggedIn(true);
          const decodedToken = jwtDecode(storedToken);
          setSub(decodedToken.sub);
          const userDetails = await fetchUserDetails(storedToken);
          if (userDetails) {
            connectWithToken(storedToken);
          } else {
            logout(true);
          }
        } catch (error) {
          console.error('로그인 상태 초기화 중 오류 발생:', error);
          logout(true);
        }
      }
      setIsLoading(false);
    };

    initializeAuth();

    const handleBeforeUnload = () => {
      localStorage.setItem('lastCloseTime', Date.now().toString());
    };
    window.addEventListener('beforeunload', handleBeforeUnload);

    const tokenCheckInterval = setInterval(checkLoginStatus, 5 * 60 * 1000);

    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
      clearInterval(tokenCheckInterval);
    };
  }, []);

  useEffect(() => {
    if (isLoggedIn) {
      const activityEvents = ['mousedown', 'keydown', 'scroll', 'touchstart'];
      const handleUserActivity = () => {
        localStorage.setItem('lastActiveTime', Date.now().toString());
      };
      
      activityEvents.forEach(event => 
        document.addEventListener(event, handleUserActivity)
      );

      return () => {
        activityEvents.forEach(event => 
          document.removeEventListener(event, handleUserActivity)
        );
      };
    }
  }, [isLoggedIn]);

  const fetchUserDetails = async (token) => {
    setIsLoading(true);
    try {
      const response = await axiosInstance.get('/users', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
  
      if (response.status === 200) {
        setUser(response.data);
        setIsLoading(false);
        return response.data;
      } else {
        throw new Error('사용자 정보를 가져오는데 실패했습니다.');
      }
    } catch (error) {
      console.error('사용자 정보를 가져오는데 실패했습니다:', error);
      setIsLoading(false);
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
      connectWithToken(newToken);
    } catch (error) {
      console.error('Login error:', error);
      logout();
    }
  };

  const logout = (silent = false) => {
    setToken(null);
    localStorage.removeItem('token');
    setIsLoggedIn(false);
    setSub('');
    setUser(null);
    if (client.current) {
      client.current.deactivate();
    }
  
    if (!silent) {
      Swal.fire({
        title: '로그아웃 되었습니다.',
        confirmButtonText: '확인',
        icon: 'info'
      });
    }
  };

  const value = useMemo(() => ({
    token, isLoggedIn, user, sub, client, isConnected, isLoading,
    login, logout, updateUser, setUser
  }), [token, isLoggedIn, user, sub, client, isConnected, isLoading]);

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};