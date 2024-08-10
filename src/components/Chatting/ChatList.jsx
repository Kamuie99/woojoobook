import React, { useState, useRef } from 'react';
import { List, ListItem, ListItemText, Divider, TextField, Button } from '@mui/material';
import styles from './ChatList.module.css';

const ChatList = ({ chatRooms, userId, onSelectRoom, fetchOrCreateChatRoom }) => {
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
    </div>
  );
};

export default ChatList;
