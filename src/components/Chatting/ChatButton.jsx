import React from "react";
import { Fab } from '@mui/material';
import { IoChatbox } from "react-icons/io5";
import styles from "./ChatButton.module.css";

const ChatButton = ({handleOpen}) => {
  return (
    <Fab
      color="primary"
      aria-label="chat"
      onClick={handleOpen}
      className={styles.chat_button}
    >
      <IoChatbox size={30} />
    </Fab>
  )
}

export default ChatButton;