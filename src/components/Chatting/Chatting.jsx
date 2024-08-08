import React, { useState, useEffect, useRef, useContext } from 'react';
import { useLocation } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import axiosInstance from '../../util/axiosConfig';
import ChatButton from './ChatButton';
import ChatModal from './ChatModal';

const Chatting = ({directMessage = null}) => {
  const { isLoggedIn, sub: userId } = useContext(AuthContext);
  const [open, setOpen] = useState(false);
  const [isClosing, setIsClosing] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [receiverId, setReceiverId] = useState('');
  const [chatRoom, setChatRoom] = useState('');
  const [messages, setMessages] = useState([]);
  const [chatRooms, setChatRooms] = useState([]);

  const location = useLocation();
  const excludedPaths = ['/login', '/register'];

  const fetchChatRooms = async () => {
    try {
      const response = await axiosInstance.get('chatrooms', {
        params: { userId, page:0, size:10 },
      });
      const data = await response.data;
      console.log(data.content)
      setChatRooms(Array.isArray(data.content) ? data.content : []);
    } catch (error) {
      console.error('채팅 룸 목록 조회 중 오류 발생:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (directMessage) {
      console.log(directMessage)
      setOpen(true);
      setTimeout(() => {
        setReceiverId(directMessage);
      }, 100);
    }
  }, [directMessage]);

  const handleOpen = () => {
    setOpen(true);
    setMessages([]);
    setChatRoom('');
    fetchChatRooms();
    document.body.style.overflow = 'hidden';
  }

  const handleClose = () => {
    setMessages([]);
    setChatRoom('');
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
        receiverId={receiverId}
        setReceiverId={setReceiverId}
        handleClose={handleClose}
        isClosing={isClosing}
        isLoading={isLoading}
        handleAnimationEnd={handleAnimationEnd}
        chatRooms={chatRooms}
        chatRoom={chatRoom}
        setChatRoom={setChatRoom}
      />
    </div>
  );
};

export default Chatting;
