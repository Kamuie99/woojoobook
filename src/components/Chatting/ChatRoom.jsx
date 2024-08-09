import React, { useState, useEffect, useRef, useContext } from 'react';
import { TextField, Button, Snackbar } from '@mui/material';
import { BsArrowReturnLeft } from 'react-icons/bs';
import { IoIosArrowDown } from "react-icons/io";
import { AuthContext } from '../../contexts/AuthContext';
import axiosInstance from '../../util/axiosConfig';
import debounce from 'lodash.debounce';
import styles from './ChatRoom.module.css';

const ChatRoom = ({ chatRoom, receiverId }) => {
  const { isLoggedIn, sub, client } = useContext(AuthContext);
  const [userId, setUserId] = useState('');
  const [chat, setChat] = useState('');
  const [messages, setMessages] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [newMessageAlert, setNewMessageAlert] = useState(false);
  const scrollBarRef = useRef(null);
  const messagesEndRef = useRef(null);
  const loadMoreMessagesTarget = useRef(null);

  useEffect(() => {
    if (isLoggedIn) {
      setUserId(sub);
    }
  }, [isLoggedIn, sub]);

  useEffect(() => {
    let subscription;
    if (client.current && chatRoom) {
      const destination = `/topic/user_${userId}`;
      subscription = client.current.subscribe(destination, debounce((message) => {
        console.log('수신된 메시지:', message.body);
        const messageBody = JSON.parse(message.body);
        const scrollableDiv = scrollBarRef.current;
        const isAtBottom = scrollableDiv.scrollHeight - scrollableDiv.scrollTop === scrollableDiv.clientHeight;
      
        setMessages((prev) => [
          { userId: messageBody.userId, content: messageBody.content },
          ...prev,
        ]);

        if (!isAtBottom && messageBody.userId != userId) {
          setNewMessageAlert(true); // 새로운 메시지가 도착했음을 알림
        }
      }, 500, { leading: true, trailing: false}));
      return () => {
        if (subscription) subscription.unsubscribe();
      };
    }
  }, [messages]);
  
  useEffect(() => {
    fetchChatMessages(0, true);
  }, [chatRoom]);

  
  useRef(() => {
    scrollToBottom();
  }, []);
  
  useEffect(() => {
    if (currentPage > 0) {
      fetchChatMessages(currentPage, false);
    }
  }, [currentPage]);
  
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore) {
          loadMoreMessages();
        }
      },
      { threshold: 1.0 }
    );
    if (loadMoreMessagesTarget.current) {
      observer.observe(loadMoreMessagesTarget.current);
    }
    return () => {
      if (loadMoreMessagesTarget.current) {
        observer.unobserve(loadMoreMessagesTarget.current);
      }
    };
  }, [hasMore]);
  
  const scrollToBottom = () => {
    setTimeout(() => {
      messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, 100);
  }

  const fetchChatMessages = async (currentPage, reset = false) => {
    try {
      const scrollableDiv = scrollBarRef.current;
      const previousScrollHeight = scrollableDiv.scrollHeight;
      const previousScrollTop = scrollableDiv.scrollTop;

      const page = reset ? 0 : currentPage;
      const response = await axiosInstance.get(`chat/${chatRoom.id}`, {
        params: { page, size: 20 }
      })
      const newChats = response.data.content || [];
      if (reset) {
        setMessages(newChats);
        setCurrentPage(0);
        setHasMore(true);
      } else {
        setMessages((prev) => [...prev, ...newChats]);
        setHasMore(newChats.length === 20);
      }
      setTimeout(() => {
        const newScrollHeight = scrollableDiv.scrollHeight;
        scrollableDiv.scrollTop = previousScrollTop + (newScrollHeight - previousScrollHeight);
      }, 1);
    } catch (error) {
      console.error(error);
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
  };

  const sendMessage = async () => {
    console.log('Attempting to send message', client.current);
    if (client.current && client.current.connected) {
      const newMessage = {
        content: chat,
        chatRoomId: chatRoom.id,
        senderId: userId,
        userId,
        receiverId,
      }
      client.current.publish({
        destination: '/app/chat',
        body: JSON.stringify(newMessage),
      });
      console.log('Message sent', newMessage);
      setChat('');
      scrollToBottom();
    } else {
      console.error('WebSocket is not connected');
    }
  };

  const loadMoreMessages = () => {
    setCurrentPage((prev) => prev + 1);
  }

  const handleCloseSnackbar = () => {
    setNewMessageAlert(false);
  };

  return (
    <div className={styles.message_list} ref={scrollBarRef}>
      <div className={styles.messages_container}>
        <div ref={loadMoreMessagesTarget} />
          {[...messages].reverse().map((message, index) => (
            <div key={index} className={
              `${styles.message} ${message.userId == userId ? styles.sent : styles.received}`
            }>
              <p>{message.content}</p>
            </div>
          ))}
        <div ref={messagesEndRef}/>
      </div>
      <div className={`
        ${styles.message_input_container}
      `}>
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
      <Snackbar
        open={newMessageAlert}
        autoHideDuration={5000}
        onClose={handleCloseSnackbar}
        message="새 메시지가 도착했습니다."
        action={
          <Button size="small" onClick={() => {
            handleCloseSnackbar();
            scrollToBottom();
          }}>
            <IoIosArrowDown color='white' size={20}/>
          </Button>
        }
      />
    </div>
  );
};

export default ChatRoom;
