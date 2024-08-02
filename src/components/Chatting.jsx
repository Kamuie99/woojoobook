import React, { useState, useEffect, useRef, useContext } from 'react';
import { useLocation } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';
import { Modal, Box, Fab, TextField, List, ListItem, ListItemText, Divider, IconButton, Typography, Button } from '@mui/material';
import { IoChatbox, IoWifi, IoChatbubblesOutline } from "react-icons/io5";
import { IoMdCloseCircleOutline } from "react-icons/io";
import { MdKeyboardArrowLeft, MdBatteryCharging90 } from "react-icons/md";
import { BsArrowReturnLeft } from "react-icons/bs";
import { FaExchangeAlt } from "react-icons/fa";
import { TbStatusChange } from "react-icons/tb";
import axiosInstance from '../util/axiosConfig';
import styles from '../styles/Chatting.module.css';
import LogoSmall from '../assets/LogoSmall.png';

const Chatting = () => {
  const { isLoggedIn, sub, client } = useContext(AuthContext);
  const [open, setOpen] = useState(false);
  const [isClosing, setIsClosing] = useState(false);
  const [userId, setUserId] = useState('');
  const [receiverId, setReceiverId] = useState('');
  const [chatRooms, setChatRooms] = useState([]);
  const [chatRoomId, setChatRoomId] = useState('');
  const [messages, setMessages] = useState([]);
  const [chat, setChat] = useState('');
  const [currentTime, setCurrentTime] = useState(new Date());
  const receiverIdRef = useRef(null);
  const messagesEndRef = useRef(null);
  const messagesContainerRef = useRef(null);

  const location = useLocation();
  const excludedPaths = ['/login', '/register'];

  useEffect(() => {
    if (isLoggedIn) {
      setUserId(sub);
    }
  }, [isLoggedIn, sub]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);
  
  useEffect(() => {
    if (receiverId) {
      fetchOrCreateChatRoom();
    }
  }, [receiverId]);

  useEffect(() => {
    if (client.current && chatRoomId) {
      const destination = `/topic/user_${userId}`;
      const subscription = client.current.subscribe(destination, (message) => {
        console.log('수신된 메시지:', message.body);
        const messageBody = JSON.parse(message.body);
        setMessages((prev) =>
          [{
            senderId: messageBody.senderId,
            content: messageBody.content
          }, ...prev]);
      });
      return () => subscription.unsubscribe();
    }
  }, [userId, chatRoomId]);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);
    return () => clearInterval(interval);
  }, [])

  const toggleOpen = () => {
    setOpen(prev => {
      const newState = !prev;
      if (!newState) {
        setMessages([]);
        setChatRoomId('');
      } else {
        fetchChatRooms();
      }
      if (newState) {
        document.body.style.overflow = 'hidden';
      } else {
        document.body.style.overflow = '';
      }
      return newState;
    });
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
      }, 900);
    }
  };
  
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  const fetchChatRooms = async () => {
    try {
      const response = await axiosInstance.get('chatrooms', {
        params: { userId },
      });
      const data = await response.data;
      setChatRooms(Array.isArray(data.content) ? data.content : []);
    } catch (error) {
      console.error('채팅 룸 목록 조회 중 오류 발생:', error);
    }
  };

  const fetchOrCreateChatRoom = async () => {
    try {
      const response = await axiosInstance.get('chatrooms/check', {
        params: {
          senderId: userId,
          receiverId
        }
      });
      if (userId === receiverId) {
        return;
      }
      const data = await response.data;
      console.log("exist: " + data.isExist);
      if (data.isExist) {
        const roomResponse = await axiosInstance.get(`chatrooms/${userId}/${receiverId}`);
        const roomData = await roomResponse.data;
        setChatRoomId(roomData.id);
        fetchChatMessages(roomData.id);
      } else {
        const newRoomResponse = await axiosInstance.post('chatrooms', {
          senderId: userId,
          receiverId
        });
        const newRoomData = await newRoomResponse.data;
        setChatRoomId(newRoomData.id);
        fetchChatMessages(newRoomData.id);
      }
    } catch (error) {
      console.error('채팅 룸 조회/생성 중 오류 발생:', error);
    }
  };

  const fetchChatMessages = async (chatRoomId) => {
    try {
      console.log(localStorage.getItem('token'));
      const response = await axiosInstance.get(`chat/${chatRoomId}`);  // 오류가 예상되는 부분
      console.log(response) // response 값
      console.log("chatRoomId: " + chatRoomId);
      const data = await response.data; // 응답으로 page를 전달 받는다!
      setMessages(data.content || []); // Page 객체의 content를 추출하여 상태 업데이트
    } catch (error) {
      console.error('채팅 메시지 조회 중 오류 발생:', error);
    }
  };

  const handleSendMessage = (e) => {
    if (e.key === 'Enter' && chat.trim() !== '') {
      sendMessage();
    }
  };

  const handleSendMessageClick = () => {
    if (chat.trim() !== '') {
      sendMessage();
    }
  }

  const sendMessage = () => {
    console.log('Attempting to send message', client.current);
    if (client.current && client.current.connected) {
      console.log('WebSocket is connected');
      const newMessage = {
        content: chat,
        chatRoomId,
        senderId: userId,
        receiverId,
      }
      client.current.publish({
        destination: '/app/chat',
        body: JSON.stringify(newMessage),
      });
      console.log('Message sent', newMessage);
      setChat('');
    } else {
      console.error('WebSocket is not connected');
    }
  };

  const handleNewChatSubmit = (e) => {
    e.preventDefault();
    const newReceiverId = receiverIdRef.current.value;
    setReceiverId(newReceiverId);
    fetchOrCreateChatRoom();
  };

  const renderChatList = () => (
    <div className={styles.chat_list}>
      <List>
        {chatRooms.map((room) => (
          <React.Fragment key={room.id}>
            <ListItem onClick={() => { 
              let otherUserId = room.receiverId;
              if (otherUserId == userId) {
                otherUserId = room.senderId;
                console.log('other')
              }
              setReceiverId(otherUserId);
              setChatRoomId(room.id);
              fetchChatMessages(room.id);
            }}>
              <ListItemText primary=
                {`${userId == room.receiverId ?
                  room.senderId : room.receiverId } 님과의 채팅방`}
              />
            </ListItem>
            <Divider />
          </React.Fragment>
        ))}
      </List>
      <h2>새로운 채팅 시작</h2>
      <form onSubmit={handleNewChatSubmit}>
        <TextField
          type="text"
          inputRef={receiverIdRef}
          placeholder="수신자 ID 입력"
        />
        <Button type="submit">채팅 시작</Button>
      </form>
    </div>
  );

  const renderChatRoom = () => (
    <div className={styles.message_list}>
      <div className={styles.message_container} ref={messagesContainerRef}>
        {[...messages].reverse().map((message, index) => (
          <div key={index} className={`${styles.message} ${message.senderId == userId ? styles.sent : styles.received}`}>
            <p>{message.content}</p>
          </div>
        ))}
        <div ref={messagesEndRef}/>
      </div>
      <div className={styles.message_input_container}>
        <TextField
          value={chat}
          onChange={(e) => setChat(e.target.value)}
          onKeyDown={handleSendMessage}
          placeholder="메시지 입력"
          fullWidth
          className={styles.message_input}
        />
        <div className={styles.message_input_button}>
          <Button onClick={handleSendMessageClick} className={styles.input_button}>
            <BsArrowReturnLeft />
          </Button>
        </div>
      </div>
    </div>
  );

  return (
    <div>
      {!excludedPaths.includes(location.pathname) &&
        <Fab
          color="primary"
          aria-label="chat"
          onClick={toggleOpen}
          className={styles.chat_button}
        >
          <IoChatbox size={30} />
        </Fab>
      }
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby={styles.chat_modal_title}
        aria-describedby={styles.chat_modal_description}
      >
        <Box
          className={`
            ${styles.chat_modal}
            ${open ?styles.chat_modal_open : ''}
            ${isClosing ? styles.chat_modal_close : ''}
          `}
          onAnimationEnd={handleAnimationEnd}
        >
          <div className={styles.phone_top_bar}>
            <div className={styles.current_time}>
            {currentTime.toLocaleTimeString([], {
              hour: 'numeric',
              minute: '2-digit',
              hour12: true,
              hourCycle: 'h12'
            })}
            </div>
            <div className={styles.top_bar_right}>
              <IoWifi />
              <p>75%</p>
              <MdBatteryCharging90 />
            </div>
            <div className={styles.camera}></div>
          </div>
          <div className={styles.modal_header}>
            {chatRoomId ?
              <IconButton
                aria-label="back"
                onClick={() => setChatRoomId('')}
              >
                <MdKeyboardArrowLeft size={30} />
              </IconButton> :
              <img src={LogoSmall} width="40px" />
            }
            <div className={styles.modal_title}>
              <p>{ chatRoomId ? `${receiverId} 님과의 채팅` : "전체 채팅 목록" }</p>
            </div>
          </div>
          {chatRoomId ? renderChatRoom() : renderChatList()}
          <div className={styles.modal_footer}>
            <IconButton
              onClick={(handleClose)}
            >
              <IoChatbubblesOutline size={30} />
            </IconButton>
            <IconButton
              onClick={(handleClose)}
            >
              <TbStatusChange size={30} />
            </IconButton>
            <IconButton
              onClick={(handleClose)}
            >
              <FaExchangeAlt size={25} />
            </IconButton>
            <IconButton
              aria-label="close"
              onClick={handleClose}
            >
              <IoMdCloseCircleOutline size={30} />
            </IconButton>
          </div>
        </Box>
      </Modal>
    </div>
  );
};

export default Chatting;
