import React, { useState, useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import { Modal, Box, Fab, TextField, List, ListItem, ListItemText, Divider, IconButton, Typography, Button } from '@mui/material';
import { IoChatbox } from "react-icons/io5";
import { IoMdCloseCircleOutline } from "react-icons/io";
import { MdKeyboardArrowLeft } from "react-icons/md";
import axiosInstance from '../util/axiosConfig';
import * as StompJs from '@stomp/stompjs';
import '../styles/Chatting.css';

const Chatting = () => {
  const [open, setOpen] = useState(false);
  const [step, setStep] = useState(1);
  const [userId, setUserId] = useState('');
  const [receiverId, setReceiverId] = useState('');
  const [chatRooms, setChatRooms] = useState([]);
  const [chatRoomId, setChatRoomId] = useState('');
  const [messages, setMessages] = useState([]);
  const [chat, setChat] = useState('');
  const client = useRef(null);
  const messagesEndRef = useRef(null);
  const messagesContainerRef = useRef(null);

  const location = useLocation();
  const excludedPaths = ['/login', '/register'];

  const handleOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    if (client.current) {
      client.current.deactivate();
    }
    setOpen(false);
    setMessages([]);
    setStep(1);
    setUserId('');
  };

  const scrollToBottom = () => {
    messagesContainerRef.current?.scrollTo({ top: messagesContainerRef.current.scrollHeight, behavior: 'smooth' });
  };

  useEffect(() => {
    if (open) {
      scrollToBottom();
    }
  }, [open, messages]);

  useEffect(() => {
    if (step === 2) {
      fetchChatRooms();
    }
  }, [step]);

  useEffect(() => {
    if (receiverId && step === 2) {
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
    if (client.current && step === 3) {
      const destination = `/queue/chat/${userId}`;
      const subscription = client.current.subscribe(destination, (message) => {
        console.log('수신된 메시지:', message.body);
        const messageBody = JSON.parse(message.body);
        setMessages((prev) => [...prev, { senderId: messageBody.senderId, content: messageBody.content }]);
      });
      return () => subscription.unsubscribe();
    }
  }, [userId, step]);

  const connect = () => {
    client.current = new StompJs.Client({
      brokerURL: 'ws://localhost:8080/ws',
      connectHeaders: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      debug: function (str) {
        console.log(str);
      },
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
      console.log(data);
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
      setStep(3);
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
      client.current.publish({
        destination: '/app/chat',
        body: JSON.stringify({ content: chat, chatRoomId, senderId: userId, receiverId }),
      });
      setChat('');
    }
  };

  const renderStep1 = () => (
    <div>
      <h1>사용자 ID 선택</h1>
      <Button onClick={() => { setUserId('1'); connect(); setStep(2); }}>User 1</Button>
      <Button onClick={() => { setUserId('2'); connect(); setStep(2); }}>User 2</Button>
    </div>
  );

  const renderStep2 = () => (
    <div>
      <h1>채팅 룸 목록</h1>
      <List>
        {chatRooms.map((room) => (
          <React.Fragment key={room.id}>
            <ListItem button onClick={() => { setReceiverId(userId === room.receiverId ? room.senderId : room.receiverId); }}>
              <ListItemText primary={`채팅 룸 ID: ${room.id} (상대방 ID: ${userId === room.receiverId ? room.senderId : room.receiverId})`} />
            </ListItem>
            <Divider />
          </React.Fragment>
        ))}
      </List>
      <h2>새로운 채팅 시작</h2>
      <TextField
        type="text"
        value={receiverId}
        onChange={(e) => setReceiverId(e.target.value)}
        placeholder="수신자 ID 입력"
      />
      <Button onClick={fetchOrCreateChatRoom}>채팅 시작</Button>
    </div>
  );

  const renderStep3 = () => (
    <div>
      <h1>채팅</h1>
      <div className="chat-list" ref={messagesContainerRef}>
        {messages.map((message, index) => (
          <div key={index}>{message.senderId} : {message.content}</div>
        ))}
      </div>
      <TextField
        value={chat}
        onChange={(e) => setChat(e.target.value)}
        onKeyDown={handleSendMessage}
        placeholder="메시지 입력"
        fullWidth
      />
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
          <Typography id="chat-modal-title" variant="h6" component="h2">
            {step === 1 ? "사용자 ID 선택" : step === 2 ? "채팅 룸 목록" : "채팅"}
          </Typography>
          <IconButton
            aria-label="close"
            onClick={handleClose}
            className="close-button"
          >
            <IoMdCloseCircleOutline size={30} />
          </IconButton>
          {step > 1 && (
            <IconButton
              aria-label="back"
              onClick={() => setStep((prevStep) => Math.max(prevStep - 1, 1))}
              className="back-button"
            >
              <MdKeyboardArrowLeft size={30} />
            </IconButton>
          )}
          {step === 1 && renderStep1()}
          {step === 2 && renderStep2()}
          {step === 3 && renderStep3()}
        </Box>
      </Modal>
    </div>
  );
};

export default Chatting;
