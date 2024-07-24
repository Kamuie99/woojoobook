import { createContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

export const AuthContext = createContext();

// eslint-disable-next-line react/prop-types
export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));
  const [nickname, setNickname] = useState('');
  const [sub, setSub] = useState('');

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
  };

  const logout = () => {
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ token, isLoggedIn, nickname, sub, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
