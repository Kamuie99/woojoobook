import React, { useState, useEffect, useContext } from 'react';
import { Modal, Box, IconButton, CircularProgress, Backdrop } from '@mui/material';
import { IoChatbubblesOutline, IoSettingsOutline  } from "react-icons/io5";
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
import Draggable from 'react-draggable';
import ChatManagement from './ChatManagement';

const ChatModal = ({ open, receiverId, setReceiverId, handleClose, isClosing, isLoading, handleAnimationEnd, chatRooms, chatRoom, setChatRoom }) => {
  const { isLoggedIn, sub: userId } = useContext(AuthContext);
  const [isVisible, setIsVisible] = useState(false);
  const [openChatManagement, setOpenChatManagement] = useState(false);

  useEffect(() => {
    if (open) {
      setChatRoom('');
      setTimeout(() => {
        setIsVisible(true);
      }, 800);
    }
  }, [open, setChatRoom]);

  useEffect(() => {
    if (receiverId) {
      fetchOrCreateChatRoom(receiverId);
    }
  }, [receiverId]);

  const fetchOrCreateChatRoom = async (newReceiverId) => {
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
    setOpenChatManagement(false);
    setReceiverId(receiverId);
    setChatRoom(chatRoom);
  };

  const handleBack = () => {
    setReceiverId('');
    setChatRoom('');
    setOpenChatManagement(false);
  };

  const openManagement = () => {
    setReceiverId('');
    setChatRoom('');
    setOpenChatManagement(true);
  }

  return (
    <Draggable>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby={styles.chatModalTitle}
        aria-describedby={styles.chatModalDescription}
        BackdropComponent={null}
      >
        <Box
          className={`
            ${styles.chatModal}
            ${open ? styles.chatModalOpen : ''}
            ${isClosing ? styles.chatModalClose : ''}
          `}
          onAnimationEnd={handleAnimationEnd}
        >
          <PhoneTopBar />
          <ChatModalHeader
            chatRoom={chatRoom}
            openChatManagement={openChatManagement}
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
              openChatManagement ? (
                <ChatManagement
                  chatRooms={chatRooms}
                  userId={userId}
                />
              ) : (
                <ChatList
                  chatRooms={chatRooms}
                  userId={userId}
                  onSelectRoom={handleSelectRoom}
                  fetchOrCreateChatRoom={fetchOrCreateChatRoom}
                />
              )
            )
          )}
          <div className={styles.modalFooter}>
            {/* TODO: 전체 채팅 보기 */}
            <IconButton onClick={handleBack}>
              <IoChatbubblesOutline size={30} />
            </IconButton>
            <IconButton onClick={openManagement}>
              <IoSettingsOutline size={30}/>
            </IconButton>
            {/* TODO: 교환 채팅 필터링 */}
            {/* <IconButton onClick={handleClose}>
              <FaExchangeAlt size={25} />
            </IconButton> */}
            <IconButton aria-label="close" onClick={handleClose}>
              <IoMdCloseCircleOutline size={30} />
            </IconButton>
          </div>
        </Box>
      </Modal>
    </Draggable>
  );
};

export default ChatModal;
