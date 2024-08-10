import React, { useRef } from "react";
import { List, ListItem, ListItemText, Divider, TextField, Button } from '@mui/material'
import styles from "./ChatManagement.module.css"

const ChatManagement = ({ chatRooms, userId, fetchOrCreateChatRoom }) => {
  const receiverIdRef = useRef(null);

  const deleteChatRooms = (roomId) => {
    // TODO: 채팅 삭제
    console.log(roomId)
  };
  
  const handleNewChat = (e) => {
    e.preventDefault();
    const receiverId = receiverIdRef.current.value;
    if (receiverId) {
      fetchOrCreateChatRoom(receiverId);
    }
  }

  return (
    <div className={styles.management}>
      <h2>새로운 채팅 시작</h2>
      <form onSubmit={handleNewChat}>
        <TextField
          type="text"
          inputRef={receiverIdRef}
          placeholder="수신자 ID 입력"
        />
        <Button type="submit">채팅 시작</Button>
      </form>
      <List>
        {chatRooms.map((room) => (
          <React.Fragment key={room.id}>
            <ListItem
              className={styles.chatListItem}
              onClick={() => {deleteChatRooms(room.id)}}
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
  )
}

export default ChatManagement;