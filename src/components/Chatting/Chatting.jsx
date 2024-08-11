import React, { useState, useEffect, useRef, useContext } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import axiosInstance from '../../util/axiosConfig';
import Swal from 'sweetalert2';
import ChatButton from './ChatButton';
import ChatModal from './ChatModal';

const Chatting = ({ onClose, newMessageChatRooms, setNewMessage, newMessage, directMessage, setDirectMessage }) => {
  const { isLoggedIn, sub: userId } = useContext(AuthContext);
  const [open, setOpen] = useState(false);
  const [isClosing, setIsClosing] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [receiverId, setReceiverId] = useState('');
  const [chatRoom, setChatRoom] = useState('');
  const [messages, setMessages] = useState([]);
  const [chatRooms, setChatRooms] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const chatRoomsEndRef = useRef(null);
  const navigate = useNavigate();

  const location = useLocation();
  const excludedPaths = ['/login', '/register'];
  
  useEffect(() => {
    if (!isLoggedIn) {
      setOpen(false);
    }
  }, [isLoggedIn, open])

  useEffect(() => {
    if (directMessage == null) {
      return;
    }
    toggleOpen();
    fetchChatRooms(0, true);
    setTimeout(() => {
      setReceiverId(directMessage);
    }, 100);
    setNewMessage(false);
  }, [directMessage]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore) {
          fetchMoreChatRooms();
        }
      },
      { threshold: 1.0 }
    );
    if (chatRoomsEndRef.current) {
      observer.observe(chatRoomsEndRef.current);
    }
    return () => {
      if (chatRoomsEndRef.current) {
        observer.unobserve(chatRoomsEndRef.current);
      }
    }
  }, [hasMore]);

  useEffect(() => {
    if (page > 0) {
      fetchChatRooms(page);
    }
  }, [page]);

  useEffect(() => {
    const updateChatRooms = chatRooms.map((room) => {
      return {
        ...room,
        hasNewMessage:!!newMessageChatRooms.current[room.id]
      }
    })
    setChatRooms([...updateChatRooms]);
  }, [newMessage])

  const fetchChatRooms = async (page, init = false) => {
    try {
      const response = await axiosInstance.get('chatrooms', {
        params: { userId, page: init ? 0 : page, size: 15 },
      });
      const data = await response.data;
      const updateChatRooms = (Array.isArray(data.content) ? data.content : []).map((room) => {
        return {
          ...room,
          hasNewMessage: !!newMessageChatRooms.current[room.id]
        };
      });
      if (init) {
        setChatRooms(updateChatRooms);
        setPage(0);
        setHasMore(true);
      } else {
        setChatRooms((prev) => [...prev, ...updateChatRooms]);
      }
      setHasMore(data.content.length === 3);
    } catch (error) {
      console.error('채팅 룸 목록 조회 중 오류 발생:', error);
    }
  };

  const fetchMoreChatRooms = () => {
    setPage((prev) => prev + 1)
  };

  const toggleOpen = (e, directMessage = null) => {
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
    fetchChatRooms(page, true);
    setTimeout(() => {
      setIsLoading(false);
    }, 900);
  }
  
  const handleClose = () => {
    setNewMessage(false);
    setMessages([]);
    setChatRoom('');
    setReceiverId('');
    setIsClosing(true);
    setIsLoading(true);
    setDirectMessage(null);
  };

  const handleAnimationEnd = () => {
    if (isClosing) {
      setTimeout(() => {
        setIsClosing(false);
        setOpen(false);
        onClose();
      }, 500);
    }
  };

  return (
    <div>
      {!excludedPaths.includes(location.pathname) &&
        <ChatButton
          toggleOpen = {toggleOpen}
          newMessage = {newMessage}
        />
      }
      {open && (
        <ChatModal
          open={open}
          isLoading={isLoading}
          isClosing={isClosing}
          chatRoomsEndRef={chatRoomsEndRef}
          hasMore={hasMore}
          fetchMoreChatRooms={fetchMoreChatRooms}
          receiverId={receiverId}
          chatRooms={chatRooms}
          chatRoom={chatRoom}
          newMessage={newMessage}
          newMessageChatRooms={newMessageChatRooms}
          handleClose={handleClose}
          handleAnimationEnd={handleAnimationEnd}
          setReceiverId={setReceiverId}
          setChatRoom={setChatRoom}
          setChatRooms={setChatRooms}
          fetchChatRooms={fetchChatRooms}
        />
      )}
    </div>
  );
};

export default Chatting;
