import React, { useState, useEffect, useContext } from 'react';
import { Modal, Box, IconButton, CircularProgress, Backdrop } from '@mui/material';
import { IoChatbubblesOutline } from "react-icons/io5";
import { IoMdCloseCircleOutline } from "react-icons/io";
import { FaExchangeAlt } from "react-icons/fa";
import { TbStatusChange } from "react-icons/tb";
import { AuthContext } from '../../contexts/AuthContext';
import axiosInstance from '../../util/axiosConfig';
import styles from './ChatModal.module.css';
import ChatList from './ChatList';
import ChatRoom from './ChatRoom';
import PhoneTopBar from './PhoneTopBar';
import ChatModalHeader from './ChatModalHeader';

const ChatModal = ({ open, handleClose, isClosing, isLoading, handleAnimationEnd, chatRooms, chatRoom, setChatRoom }) => {
  const { isLoggedIn, sub } = useContext(AuthContext);
  const [userId, setUserId] = useState('');
  const [receiverId, setReceiverId] = useState('');

  useEffect(() => {
    if (isLoggedIn) {
      setUserId(sub);
    }
  }, [receiverId]);

  useEffect(() => {
    if (open) {
      setChatRoom('');
    }
  }, [open, setChatRoom]);

  const handleNewChatSubmit = async (e, newReceiverId) => {
    e.preventDefault();
    setReceiverId(newReceiverId);
    if (userId == newReceiverId) {
      console.log('sender와 receiver id가 같습니다.')
      return;
    }
    try {
      const response = await axiosInstance.get('chatrooms/check', {
        params: {
          senderId: userId,
          receiverId: newReceiverId,
        },
      });
      const data = await response.data;
      if (data.isExist) {
        const roomResponse = await axiosInstance.get(`chatrooms/${userId}/${newReceiverId}`);
        const roomData = await roomResponse.data;
        setChatRoom(roomData);
      } else {
        const newRoomResponse = await axiosInstance.post('chatrooms', {
          senderId: userId,
          receiverId: newReceiverId,
        });
        const newRoomData = await newRoomResponse.data;
        setChatRoom(newRoomData);
      }
    } catch (error) {
      console.error('채팅 룸 조회/생성 중 오류 발생:', error);
    }
  };

  const handleSelectRoom = (receiverId, chatRoom) => {
    setReceiverId(receiverId);
    setChatRoom(chatRoom);
  };

  const handleBack = () => {
    setReceiverId('');
    setChatRoom('');
  };

  return (
    <Modal
      open={open}
      onClose={handleClose}
      aria-labelledby={styles.chat_modal_title}
      aria-describedby={styles.chat_modal_description}
      BackdropProps={{
        style: { backgroundColor: 'transparent' },
        invisible: true
      }}
    >
      <Box
        className={`
          ${styles.chat_modal}
          ${open ? styles.chat_modal_open : ''}
          ${isClosing ? styles.chat_modal_close : ''}
        `}
        onAnimationEnd={handleAnimationEnd}
      >
        <PhoneTopBar />
        <ChatModalHeader
          chatRoom={chatRoom}
          handleBack={handleBack}
        />
        {isLoading ? (
          <div className={styles.loading}>
            <CircularProgress />
          </div>
        ) : (
          chatRoom ? (
            <ChatRoom
              userId={userId}
              chatRoom={chatRoom}
              receiverId={receiverId}
            />
          ) : (
            <ChatList
              chatRooms={chatRooms}
              userId={userId}
              onSelectRoom={handleSelectRoom}
              handleNewChatSubmit={handleNewChatSubmit}
            />
          )
        )}
        <div className={styles.modal_footer}>
          {/* TODO: 전체 채팅 보기 */}
          <IconButton onClick={handleBack}>
            <IoChatbubblesOutline size={30} />
          </IconButton>
          {/* TODO: 대여 채팅 필터링 */}
          <IconButton onClick={handleClose}>
            <TbStatusChange size={30} />
          </IconButton>
          {/* TODO: 교환 채팅 필터링 */}
          <IconButton onClick={handleClose}>
            <FaExchangeAlt size={25} />
          </IconButton>
          <IconButton aria-label="close" onClick={handleClose}>
            <IoMdCloseCircleOutline size={30} />
          </IconButton>
        </div>
      </Box>
    </Modal>
  );
};

export default ChatModal;
