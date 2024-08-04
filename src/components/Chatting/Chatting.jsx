import React, { useState, useEffect, useRef, useContext } from 'react';
import { useLocation } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import axiosInstance from '../../util/axiosConfig';
import styles from './Chatting.module.css';
import ChatButton from './ChatButton';
import ChatModal from './ChatModal';

const Chatting = () => {
  const { isLoggedIn, sub } = useContext(AuthContext);
  const [open, setOpen] = useState(false);
  const [isClosing, setIsClosing] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [userId, setUserId] = useState('');
  const [receiverId, setReceiverId] = useState('');
  const [chatRoomId, setChatRoomId] = useState('');
  const [messages, setMessages] = useState([]);
  const [chatRooms, setChatRooms] = useState([]);

  const location = useLocation();
  const excludedPaths = ['/login', '/register'];

  const fetchChatRooms = async () => {
    try {
      const response = await axiosInstance.get('chatrooms', {
        params: { userId },
      });
      const data = await response.data;
      setChatRooms(Array.isArray(data.content) ? data.content : []);
    } catch (error) {
      console.error('채팅 룸 목록 조회 중 오류 발생:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (isLoggedIn) {
      setUserId(sub);
    }
  }, [isLoggedIn, sub]);
  
  useEffect(() => {
    if (receiverId) {
      fetchOrCreateChatRoom();
    }
  }, [receiverId]);

  const handleOpen = () => {
    setOpen(true);
    setMessages([]);
    setChatRoomId('');
    fetchChatRooms();
    document.body.style.overflow = 'hidden';
  }

  const handleClose = () => {
    setMessages([]);
    setChatRoomId('');
    setReceiverId('');
    setIsClosing(true);
  };

  const handleAnimationEnd = () => {
    if (isClosing) {
      setTimeout(() => {
        setIsClosing(false);
        setOpen(false);
        document.body.style.overflow = '';
      }, 500);
      setIsLoading(true);
    }
  };

  return (
    <div>
      {!excludedPaths.includes(location.pathname) &&
        <ChatButton
          handleOpen = {handleOpen}
        />
      }
      <ChatModal
        open={open}
        handleClose={handleClose}
        isClosing={isClosing}
        isLoading={isLoading}
        handleAnimationEnd={handleAnimationEnd}
        chatRooms={chatRooms}
        chatRoomId={chatRoomId}
        setChatRoomId={setChatRoomId}
      />
    </div>
  );
};

export default Chatting;
