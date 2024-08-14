import React, { useState, useEffect, useContext } from 'react';
import { Modal, Box, IconButton, CircularProgress } from '@mui/material';
import { IoChatbubblesOutline, IoSettingsOutline, IoCaretBack, IoCaretForward, IoEllipse, IoEllipseOutline } from "react-icons/io5";
import { FaExchangeAlt } from "react-icons/fa";
import { IoMdCloseCircleOutline } from "react-icons/io";
import { AuthContext } from '../../contexts/AuthContext';
import axiosInstance from '../../util/axiosConfig';
import styles from './ChatModal.module.css';
import ChatList from './ChatList';
import ChatRoom from './ChatRoom';
import PhoneTopBar from './PhoneTopBar';
import ChatModalHeader from './ChatModalHeader';
import Draggable from 'react-draggable';
import ChatManagement from './ChatManagement';
import Swal from 'sweetalert2';

const ChatModal = ({
  open, isLoading, isClosing,
  receiverId, chatRooms, chatRoom, newMessage, newMessageChatRooms,
  handleClose, handleAnimationEnd, setReceiverId, setChatRoom, setChatRooms, fetchChatRooms,
  handlePageChange, currentPage, totalPages
}) => {
  const { sub: userId } = useContext(AuthContext);
  const [openChatManagement, setOpenChatManagement] = useState(false);

  useEffect(() => {
    if (open) {
      setChatRoom('');
      fetchChatRooms(0, true);
    }
  }, [open]);

  useEffect(() => {
    if (receiverId) {
      fetchOrCreateChatRoom(receiverId);
    }
  }, [receiverId]);
  
  const fetchOrCreateChatRoom = async (newReceiverId) => {
    setReceiverId(newReceiverId);
    if (userId == newReceiverId) {
      Swal.fire({
        title: '잘못된 요청입니다',
        confirmButtonText: '확인',
        icon: 'error'
      })
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
        newMessageChatRooms.current[roomData.id] = false;
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
    newMessageChatRooms.current[chatRoom.id] = false;
  };

  const handleBack = (roomId) => {
    setReceiverId('');
    setChatRoom('');
    fetchChatRooms(0, true);
    setOpenChatManagement(false);
    if (roomId) {
      newMessageChatRooms.current[roomId] = false;
    }
  };

  // const openManagement = () => {
  //   setReceiverId('');
  //   setChatRoom('');
  //   setOpenChatManagement(true);
  // }

  const renderContent = () => {
    if (isLoading) {
      return (
        <div className={styles.loading}>
          <CircularProgress className={styles.loadingCircularProgress} />
        </div>
      );
    }

    if (chatRoom) {
      return (
        <ChatRoom
          userId={userId}
          chatRoom={chatRoom}
          receiverId={receiverId}
        />
      );
    }

    if (openChatManagement) {
      return (
        <ChatManagement
          chatRooms={chatRooms}
          userId={userId}
          fetchOrCreateChatRoom={fetchOrCreateChatRoom}
        />
      );
    }

    return (
      <>
        <ChatList
          chatRooms={chatRooms}
          userId={userId}
          onSelectRoom={handleSelectRoom}
          setChatRooms={setChatRooms}
          newMessage={newMessage}
          newMessageChatRooms={newMessageChatRooms}
        />
        <div className={styles.pagination}>
          <button
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 0}
          >
            <IoCaretBack size={20} />
          </button>
          {totalPages > 1 && (
            <div className={styles.paginationButton}>
              {Array.from({ length: totalPages }, (_, index) => (
                <button
                  key={index}
                  onClick={() => handlePageChange(index)}
                  disabled={currentPage === index}
                >
                  {currentPage === index
                    ? <IoEllipse size={20} />
                    : <IoEllipseOutline size={20} />}
                </button>
              ))}
            </div>
          )}
          <button
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage >= totalPages -1}
            >
            <IoCaretForward size={20} />
          </button>
        </div>
      </>
    );
  };

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
            ${styles.chatModal} ${open ? styles.chatModalOpen : ''} ${isClosing ? styles.chatModalClose : ''}
          `}
          onAnimationEnd={handleAnimationEnd}
        >
          <PhoneTopBar />
          <ChatModalHeader
            chatRoom={chatRoom}
            openChatManagement={openChatManagement}
            handleBack={handleBack}
          />
          {renderContent()}
          <div className={styles.modalFooter}>
            <IconButton onClick={() => handleBack()}>
              <IoChatbubblesOutline size={30} />
            </IconButton>
            <IconButton aria-label="close" onClick={() => handleClose(chatRoom)}>
              <IoMdCloseCircleOutline size={30} />
            </IconButton>
          </div>
        </Box>
      </Modal>
    </Draggable>
  );
};

export default ChatModal;
