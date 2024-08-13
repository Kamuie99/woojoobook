import React from "react";
import { Fab } from '@mui/material';
import { IoChatbox, IoAlertCircleSharp } from "react-icons/io5";
import styles from "./ChatButton.module.css";

const ChatButton = ({toggleOpen, newMessage, isOpen}) => {
  return (
    <Fab
      color="primary"
      aria-label="chat"
      onClick={toggleOpen}
      className={styles.chat_button}
    >
      {newMessage && !isOpen && (
        <IoAlertCircleSharp
          className={styles.newMessage}
          size={30}
        />
      )}
      <IoChatbox size={30} />
    </Fab>
  )
}

export default ChatButton;