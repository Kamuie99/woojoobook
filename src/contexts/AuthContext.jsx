import { createContext, useState, useRef, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import * as StompJs from '@stomp/stompjs';

export const AuthContext = createContext();

// eslint-disable-next-line react/prop-types
export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));
  const [nickname, setNickname] = useState('');
  const [sub, setSub] = useState('');
  const client = useRef(null);

  const brokerURL = import.meta.env.VITE_APP_STOMP_BROKER_URL;

  const connect = () => {
    client.current = new StompJs.Client({
      brokerURL,
      connectHeaders: {
        'Authorization': `Bearer ${token}`
      },
      debug: function (str) {
        console.log(str);
      },
      onConnect: () => {
        console.log('웹소켓 연결 성공');
      },
      onStompError: (frame) => {
        console.error('Broker reported error:'+ frame.headers['message']);
        console.error('Additional details:'+ frame.body);
      },
    });

    client.current.onWebSocketColse = (event) => {
      console.log('WebSocket closed:'+ event);
    };

    client.current.onWebSocketError = (event) => {
      console.error('WebSocket error:'+ event);
    };

    client.current.activate();
  };

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
      setIsLoggedIn(true);
      const decodedToken = jwtDecode(token);
      setNickname(decodedToken.nickname);
      setSub(decodedToken.sub);
    } else {
      localStorage.removeItem('token');
      setIsLoggedIn(false);
      setNickname('');
      setSub('');
    }
  }, [token]);

  const login = (newToken) => {
    setToken(newToken);
    connect();
  };

  const logout = () => {
    setToken(null);
    if (client.current) {
      client.current.deactivate();
    }
  };

  return (
    <AuthContext.Provider value={{ token, isLoggedIn, nickname, sub, client, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
