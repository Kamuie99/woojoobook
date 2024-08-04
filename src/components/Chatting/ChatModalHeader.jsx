import React from "react";
import { IconButton } from '@mui/material';
import { MdKeyboardArrowLeft } from "react-icons/md";
import styles from './ChatModalHeader.module.css';

const ChatModalHeader = ({chatRoomId, handleBack, receiverId}) => {
  return (
    <div className={styles.modal_header}>
      {chatRoomId ? (
        <IconButton
          aria-label="back"
          onClick={handleBack}
        >
          <MdKeyboardArrowLeft size={30} />
        </IconButton>
      ) : (
        <div></div>
      )}
      <div className={styles.modal_title}>
        <p>
          {chatRoomId ?
            `${receiverId } 님과의 채팅` : '전체 채팅 목록'
          }
        </p>
      </div>
    </div>
  )
}

export default ChatModalHeader;