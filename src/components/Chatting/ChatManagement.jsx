import React from "react";
import { List, ListItem, ListItemText, Divider } from '@mui/material'
import styles from "./ChatManagement.module.css"

const ChatManagement = ({ chatRooms, userId }) => {
  const deleteChatRooms = (roomId) => {
    // TODO: 채팅 삭제
    console.log(roomId)
  };

  return (
    <div className={styles.management}>
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