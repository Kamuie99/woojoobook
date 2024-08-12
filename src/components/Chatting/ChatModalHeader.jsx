import React, { useContext, useEffect, useState } from "react";
import { AuthContext } from "../../contexts/AuthContext";
import { IconButton } from '@mui/material';
import { MdKeyboardArrowLeft } from "react-icons/md";
import styles from './ChatModalHeader.module.css';

const ChatModalHeader = ({chatRoom, openChatManagement, handleBack}) => {
  const { sub: userId } = useContext(AuthContext);
  const [receiverNickname, setReceiverNickname] = useState('');

  useEffect(() => {
    if (chatRoom) {
      if (chatRoom.senderId == userId) {
        setReceiverNickname(chatRoom.receiverNickname);
      } else {
        setReceiverNickname(chatRoom.senderNickname);
      }
    } else {
      setReceiverNickname('');
    }
  }, [chatRoom]);

  return (
    <div className={styles.modalHeader}>
      {chatRoom ? (
        <IconButton aria-label="back" onClick={() => handleBack(chatRoom.id)}  className={styles.backButton}>
          <MdKeyboardArrowLeft size={28}/>
        </IconButton>
      ) : (
        openChatManagement ? "채팅방 관리하기" : "전체 채팅 목록"
      )}
      <div className={styles.modalTitle}>
        <p>
          {chatRoom ?
            `${receiverNickname === 'anonymous' ? '(알 수 없음)' : receiverNickname} 님과의 채팅` :
            <div></div>    
          }
        </p>
      </div>

    </div>
  )
}

export default ChatModalHeader;