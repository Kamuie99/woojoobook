import React, { useState, useEffect, useRef, useContext } from 'react';
import { useLocation } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';
import { Modal, Box, Fab, TextField, List, ListItem, ListItemText, Divider, IconButton, Typography, Button } from '@mui/material';
import { IoChatbox } from "react-icons/io5";
import { IoMdCloseCircleOutline } from "react-icons/io";
import { MdKeyboardArrowLeft } from "react-icons/md";
import { BsArrowReturnLeft } from "react-icons/bs";
import axiosInstance from '../util/axiosConfig';
import * as StompJs from '@stomp/stompjs';
import '../styles/Chatting.css';

const Chatting = () => {
  const { isLoggedIn, sub, token } = useContext(AuthContext);
  const [open, setOpen] = useState(false);
  const [userId, setUserId] = useState('');
  const [receiverId, setReceiverId] = useState('');
  const [chatRooms, setChatRooms] = useState([]);
  const [chatRoomId, setChatRoomId] = useState('');
  const [messages, setMessages] = useState([]);
  const [chat, setChat] = useState('');
  const client = useRef(null);
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
    return () => {
      if (client.current) {
        client.current.deactivate();
      }
    };
  }, []);

  useEffect(() => {
    if (client.current && chatRoomId) {
      const destination = `/queue/chat/${userId}`;
      const subscription = client.current.subscribe(destination, (message) => {
        console.log('수신된 메시지:', message.body);
        const messageBody = JSON.parse(message.body);
        setMessages((prev) => [...prev, { senderId: messageBody.senderId, content: messageBody.content }]);
      });
      return () => subscription.unsubscribe();
    }
  }, [userId, chatRoomId]);

  const handleOpen = () => {
    setOpen(true);
    connect();
    fetchChatRooms();
  };

  const handleClose = () => {
    if (client.current) {
      client.current.deactivate();
    }
    setOpen(false);
    setMessages([]);
    setChatRoomId('');
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };
  
  const brokerURL = import.meta.env.VITE_APP_STOMP_BROKER_URL;
  const connect = () => {
    client.current = new StompJs.Client({
      brokerURL,
      connectHeaders: {
        'Authorization': `Bearer ${token}`
      },
      // debug: function (str) {
      //   console.log(str);
      // },
      onConnect: () => {
        console.log('웹소켓 연결 성공');
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
      },
    });

    client.current.onWebSocketClose = (event) => {
      console.log('WebSocket closed: ', event);
    };

    client.current.onWebSocketError = (event) => {
      console.error('WebSocket error: ', event);
    };

    client.current.activate();
  };

  const fetchChatRooms = async () => {
    try {
      const response = await axiosInstance.get('chatrooms', {
        params: { userId },
      });
      const data = await response.data;
      console.log(data.content);
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
      const response = await axiosInstance.get(`chat/${chatRoomId}`);
      console.log("id: " + chatRoomId);
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
    client.current.publish({
      destination: '/app/chat',
      body: JSON.stringify({ content: chat, chatRoomId, senderId: userId, receiverId }),
    });
    setChat('');
  }

  const handleNewChatSubmit = (e) => {
    e.preventDefault();
    const newReceiverId = receiverIdRef.current.value;
    setReceiverId(newReceiverId);
    fetchOrCreateChatRoom();
  };

  const renderChatList = () => (
    <div class="message-list">
      <List>
        {chatRooms.map((room) => (
          <React.Fragment key={room.id}>
            <ListItem button onClick={() => { 
              let otherUserId = room.receiverId;
              if (otherUserId == userId) {
                otherUserId = room.senderId;
                console.log('other')
              }
              setReceiverId(otherUserId);
              setChatRoomId(room.id);
              fetchChatMessages(room.id);
            }}>
              <ListItemText primary={`${userId == room.receiverId ? room.senderId : room.receiverId } 님과의 채팅방`} />
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
    <div class="message-list">
      <div className="message-container" ref={messagesContainerRef}>
        {messages.map((message, index) => (
          <div key={index} className={`message ${message.senderId == userId ? 'sent' : 'received'}`}>
            <p>{message.content}</p>
          </div>
        ))}
        <div ref={messagesEndRef}/>
      </div>
      <div class="message-input-container">
        <TextField
          value={chat}
          onChange={(e) => setChat(e.target.value)}
          onKeyDown={handleSendMessage}
          placeholder="메시지 입력"
          fullWidth
          className="message-input"
        />
        <div class="message-input-button">
          <Button onClick={handleSendMessageClick} className="input-button">
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
          onClick={handleOpen}
          className="chat-button"
        >
          <IoChatbox size={30} />
        </Fab>
      }
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="chat-modal-title"
        aria-describedby="chat-modal-description"
      >
        <Box className="chat-modal">
          <div class="modal-header">
            <div class="modal-title">
              <p>
                { chatRoomId ? `${chatRoomId} 님과의 채팅` : "채팅 목록" }
              </p>
            </div>
            {
              chatRoomId ?
              <IconButton
                aria-label="back"
                onClick={() => setChatRoomId('')}
              >
                <MdKeyboardArrowLeft size={30} />
              </IconButton> :
              <IconButton
              aria-label="close"
              onClick={handleClose}
              >
                <IoMdCloseCircleOutline size={30} />
              </IconButton>
            }
          </div>
          {chatRoomId ? renderChatRoom() : renderChatList()}
        </Box>
      </Modal>
    </div>
  );
};

export default Chatting;
