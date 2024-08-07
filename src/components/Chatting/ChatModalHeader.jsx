import React, { useContext, useEffect, useState } from "react";
import { AuthContext } from "../../contexts/AuthContext";
import { IconButton } from '@mui/material';
import { MdKeyboardArrowLeft } from "react-icons/md";
import styles from './ChatModalHeader.module.css';

const ChatModalHeader = ({chatRoom, handleBack}) => {
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
    <div className={styles.modal_header}>
      {chatRoom ? (
        <IconButton aria-label="back" onClick={handleBack}>
          <MdKeyboardArrowLeft size={30} />
        </IconButton>
      ) : (
        <div></div>
      )}
      <div className={styles.modal_title}>
        <p>
          {chatRoom ?
            `${receiverNickname} 님과의 채팅` : '전체 채팅 목록'
          }
        </p>
      </div>
    </div>
  )
}

export default ChatModalHeader;