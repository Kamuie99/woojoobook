import React from "react";
import { Fab } from '@mui/material';
import { IoChatbox } from "react-icons/io5";
import styles from "./ChatButton.module.css";

const ChatButton = ({toggleOpen}) => {
  return (
    <Fab
      color="primary"
      aria-label="chat"
      onClick={toggleOpen}
      className={styles.chat_button}
    >
      <IoChatbox size={30} />
    </Fab>
  )
}

export default ChatButton;