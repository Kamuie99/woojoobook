import React, { useState, useEffect, useRef, useContext } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import axiosInstance from '../../util/axiosConfig';
import Swal from 'sweetalert2';
import ChatButton from './ChatButton';
import ChatModal from './ChatModal';

const Chatting = ({ directMessage = null, onClose }) => {
  const { isLoggedIn, sub: userId } = useContext(AuthContext);
  const [open, setOpen] = useState(false);
  const [isClosing, setIsClosing] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [receiverId, setReceiverId] = useState('');
  const [chatRoom, setChatRoom] = useState('');
  const [messages, setMessages] = useState([]);
  const [chatRooms, setChatRooms] = useState([]);
  const navigate = useNavigate();

  const location = useLocation();
  const excludedPaths = ['/login', '/register'];
  
  useEffect(() => {
    if (!isLoggedIn) {
      setOpen(false);
    }
  }, [isLoggedIn, open])

  useEffect(() => {
    if (directMessage) {
      setOpen(true);
      fetchChatRooms();
      setTimeout(() => {
        setReceiverId(directMessage);
      }, 100);
      return;
    }
    setOpen(false);
  }, [directMessage]);

  const fetchChatRooms = async () => {
    try {
      const response = await axiosInstance.get('chatrooms', {
        params: { userId, page:0, size:10 },
      });
      const data = await response.data;
      setChatRooms(Array.isArray(data.content) ? data.content : []);
    } catch (error) {
      console.error('채팅 룸 목록 조회 중 오류 발생:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const toggleOpen = () => {
    if (!isLoggedIn) {
      Swal.fire({
        title: '채팅을 하시려면 로그인 해주세요',
        confirmButtonText: '확인',
        icon: 'warning'
      }).then((result) => {
        if (result.isConfirmed) {
          navigate('/login');
        }
      })
    }
    if (open) {
      handleClose();
      return;
    } 
    setOpen(true);
    setMessages([]);
    setChatRoom('');
    fetchChatRooms();
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
        onClose();
      }, 500);
      setIsLoading(true);
    }
  };

  return (
    <div>
      {!excludedPaths.includes(location.pathname) &&
        <ChatButton
          toggleOpen = {toggleOpen}
        />
      }
      {open && (
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
      )}
    </div>
  );
};

export default Chatting;
