import React, { useState, useRef } from 'react';
import { List, ListItem, ListItemText, Divider, TextField, Button } from '@mui/material';
import styles from './ChatList.module.css';

const ChatList = ({ chatRooms, userId, onSelectRoom, handleNewChatSubmit }) => {
  const receiverIdRef = useRef(null);
  
  const handleNewChat = (e) => {
    e.preventDefault();
    const receiverId = receiverIdRef.current.value;
    if (receiverId) {
      handleNewChatSubmit(receiverId);
    }
  }

  return (
    <div className={styles.chatList}>
      <List>
        {chatRooms.map((room) => (
          <React.Fragment key={room.id}>
            <ListItem
              className={styles.chatListItem}
              onClick={() => { 
                let otherUserId = room.receiverId;
                if (otherUserId == userId) otherUserId = room.senderId;
                onSelectRoom(otherUserId, room);
                console.log(room)
              }}
            >
              <ListItemText primary={`
                ${userId == room.receiverId ?
                  room.senderNickname : room.receiverNickname} 님과의 채팅
              `} />
            </ListItem>
            <Divider />
          </React.Fragment>
        ))}
      </List>
      <h2>새로운 채팅 시작</h2>
      <form onSubmit={handleNewChat}>
        <TextField
          type="text"
          inputRef={receiverIdRef}
          placeholder="수신자 ID 입력"
        />
        <Button type="submit">채팅 시작</Button>
      </form>
    </div>
  );
};

export default ChatList;
