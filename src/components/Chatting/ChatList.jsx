import React, { useRef } from 'react';
import { List, ListItem, ListItemText, Divider, TextField, Button } from '@mui/material';
import axiosInstance from '../../util/axiosConfig';
import styles from './ChatList.module.css';

const ChatList = ({ chatRooms, userId, onSelectRoom, handleNewChatSubmit }) => {
  const receiverIdRef = useRef(null);

  return (
    <div className={styles.chat_list}>
      <List>
        {chatRooms.map((room) => (
          <React.Fragment key={room.id}>
            <ListItem onClick={() => { 
              let otherUserId = room.receiverId;
              if (otherUserId == userId) otherUserId = room.senderId;
              const fetchOtherUserNickname = async () => {
                // 다른 사람 닉네임 받아오는 api 필요
                try {
                  const response = await axiosInstance.get(`/users`, {
                    params: { userId: otherUserId }
                  });
                  console.log(response.data)
                } catch (error) {
                  console.log(error);
                }
              }
              fetchOtherUserNickname();
              
              onSelectRoom(otherUserId, room.id);
            }}>
              {/* TODO: 상대 유저 닉네임 받아오기 */}
              <ListItemText primary={`
                ${userId == room.receiverId ?
                  room.senderId : room.receiverId} 님과의 채팅방
              `} />
            </ListItem>
            <Divider />
          </React.Fragment>
        ))}
      </List>
      <h2>새로운 채팅 시작</h2>
      <form onSubmit={(e) => handleNewChatSubmit(e, receiverIdRef.current.value)}>
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
