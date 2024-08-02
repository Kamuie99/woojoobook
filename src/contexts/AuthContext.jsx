import { createContext, useState, useRef, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import * as StompJs from '@stomp/stompjs';
import axiosInstance from '../util/axiosConfig';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));
  const [sub, setSub] = useState('');
  const [user, setUser] = useState(null);  // 유저 정보 추가 항목
  const [isConnected, setIsConnected] = useState(false);
  const client = useRef(null);

  const brokerURL = import.meta.env.VITE_APP_STOMP_BROKER_URL;

  const connect = () => {
    if (!token) {
      console.error('연결에 사용할 수 있는 토큰이 없습니다');
      return;
    }

    client.current = new StompJs.Client({
      brokerURL,
      connectHeaders: {
        'Authorization': `Bearer ${token}`
      },
      // debug: function (str) {
      //   console.log(str);
      // },
      onConnect: () => {
        console.log('웹소켓 연결 성공');
        setIsConnected(true);
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
      console.log('WebSocket closed: ' + event);
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

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
      setIsLoggedIn(true);
      const decodedToken = jwtDecode(token); // jwt 토큰을 파싱해서
      setSub(decodedToken.sub);  // 파싱한 값중 sub(유저 식별자) 값을 저장
      fetchUserDetails(token); // Fetch user details when the token is set
      connect(); // 토큰이 설정되면 connect 함수 호출
    } else {
      localStorage.removeItem('token');
      setIsLoggedIn(false);
      setSub('');
      setUser(null);
      if (client.current) {
        client.current.deactivate();
      }
    }
  }, [token]);

  const fetchUserDetails = async (token) => {
    try {
      const response = await axiosInstance.get('/users', {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (response.status === 200) {
        setUser(response.data); // 응답이 성공적일 경우 유저 데이터 설정
      } else {
        throw new Error('사용자 정보를 가져오는데 실패했습니다.');
      }
    } catch (error) {
      console.error('사용자 정보를 가져오는데 실패했습니다:', error);
      setUser(null); // 오류 발생 시 유저를 null로 설정
    }
  };

  const login = (newToken) => {
    setToken(newToken);
    // connect 함수는 useEffect에서 호출되므로 여기서는 제거
  };

  const logout = () => {
    setToken(null);
    // client deactivate는 useEffect에서 처리
  };

  return (
    <AuthContext.Provider value={{ token, isLoggedIn, user, sub, client, isConnected, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};