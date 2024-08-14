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
  const [isMessageEndVisible, setIsMessageEndVisible] = useState(true);
  const [deactivateChat, setDeactivateChat] = useState(false);
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
        const messageBody = JSON.parse(message.body);
        if (messageBody.chatRoomId != chatRoom.id) {
          return;
        }
        const scrollableDiv = scrollBarRef.current;
        
        const heightDiff = scrollableDiv.scrollHeight - scrollableDiv.scrollTop - 555
        const isAtBottom = Math.abs(heightDiff) < 1

        setMessages((prev) => [
          {
            userId: messageBody.userId,
            content: messageBody.content,
            createdAt: messageBody.createdAt
          },
          ...prev,
        ]);
        if (isAtBottom) {
          scrollToBottom();
        } else if (messageBody.userId != userId) {
          setNewMessageAlert(true);
        }
      }, 500, { leading: true, trailing: false}));
      return () => {
        if (subscription) subscription.unsubscribe();
      };
    }
  }, [messages]);
  
  useEffect(() => {
    fetchChatMessages(0, true);
    setDeactivateChat(false);
    if (chatRoom.senderNickname === 'anonymous' ||
      chatRoom.receiverNickname === 'anonymous') {
      setDeactivateChat(true)
    }
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

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        setIsMessageEndVisible(entries[0].isIntersecting);
      },
      { threshold: 0 }
    );
    if (messagesEndRef.current) {
      observer.observe(messagesEndRef.current);
    }
    return () => {
      if (messagesEndRef.current) {
        observer.unobserve(messagesEndRef.current);
      }
    };
  }, []);
  
  useEffect(() => {
    if (isMessageEndVisible) {
      setNewMessageAlert(false);
    }
  }, [isMessageEndVisible]);

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
    // console.log('Attempting to send message', client.current);
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
      // console.log('Message sent', newMessage);
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
    <div className={styles.messageList} ref={scrollBarRef}>
      <div className={styles.messagesContainer}>
        <div ref={loadMoreMessagesTarget} />
        {[...messages].reverse().map((message) => {
          const originalHour = parseInt(message.createdAt.slice(11, 13), 10);
          const adjustedHour = (originalHour + 9) % 24;
          const period = adjustedHour > 12 ? '오후' : '오전';
          const formattedHour = adjustedHour > 12 ? adjustedHour - 12 : adjustedHour;
          const formattedTime = `${formattedHour}:${message.createdAt.slice(14, 16)}`;

          return (
            <div key={message.id} className={
              `${styles.message} ${message.userId == userId ? styles.sent : styles.received}`
            }>
              <p className={styles.createdAt}>
                {message.content}
                <p className={styles.dateTime}>
                  <p>{period}</p>
                  <p>{formattedTime}</p>
                </p>
              </p>
            </div>
          );
        })}
        <div ref={messagesEndRef}/>
      </div>
      <div className={`
        ${styles.messageInputContainer}
      `}>
        <TextField
          value={chat}
          onChange={(e) => setChat(e.target.value)}
          onKeyDown={handleSendMessage}
          // placeholder="메시지 입력"
          placeholder={deactivateChat ? '탈퇴한 사용자입니다.' : '메시지 입력'}
          fullWidth
          className={styles.messageInput}
          disabled={deactivateChat}
        />
        <div className={styles.messageInputButton}>
          <BsArrowReturnLeft
            onClick={handleSendMessageClick}
            className={styles.inputButton}
            disabled={deactivateChat}  
          />
          {/* <Button>
          </Button> */}
        </div>
      </div>
      <Snackbar
        open={newMessageAlert}
        // autoHideDuration={5000}
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
