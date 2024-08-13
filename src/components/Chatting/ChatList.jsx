import React, { useEffect } from 'react';
import { IoPersonOutline, IoChatbubbleEllipsesOutline } from "react-icons/io5";
import styles from './ChatList.module.css';

const ChatList = ({ chatRooms, userId, onSelectRoom, setChatRooms, newMessage, newMessageChatRooms }) => {
  useEffect(() => {
    const updateChatRooms = chatRooms.map((room) => {
      return {
        ...room,
        hasNewMessage:!!newMessageChatRooms.current[room.id]
      }
    })
    setChatRooms([...updateChatRooms]);
  }, [newMessage])

  const getDisplayName = (room) => {
    const isUserSender = userId == room.receiverId;
    const nickname = isUserSender ? room.senderNickname : room.receiverNickname;
    return nickname === 'anonymous' ? '(알 수 없음)' : nickname;
  };

  return (
    <div className={styles.chatList} id="chatList">
      {chatRooms.map((room, index) => (
        <div
          key={room.id}
          className={styles.chatListItem}
          onClick={() => {
            let otherUserId = room.receiverId;
            if (otherUserId == userId) otherUserId = room.senderId;
            onSelectRoom(otherUserId, room);
            console.log(room);
          }}
        >
          <IoPersonOutline size={40}/>
          <p>
            {getDisplayName(room)}
          </p>
          {room.hasNewMessage && (
            <IoChatbubbleEllipsesOutline
              className={styles.newMessage}
              size={25}
            />
          )}
        </div>
      ))}
    </div>
  );
};

export default ChatList;