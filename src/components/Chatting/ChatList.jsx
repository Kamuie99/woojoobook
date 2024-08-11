import React, { useEffect } from 'react';
import { IoPersonOutline, IoChatbubbleEllipsesOutline } from "react-icons/io5";
import styles from './ChatList.module.css';

const ChatList = ({ chatRooms, userId, onSelectRoom, setChatRooms, chatRoomsEndRef, newMessage, newMessageChatRooms }) => {
  useEffect(() => {
    const updateChatRooms = chatRooms.map((room) => {
      return {
        ...room,
        hasNewMessage:!!newMessageChatRooms.current[room.id]
      }
    })
    setChatRooms([...updateChatRooms]);
  }, [newMessage])

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
          <p>{`${userId == room.receiverId ? room.senderNickname : room.receiverNickname}`}</p>
          {room.hasNewMessage && (
            <IoChatbubbleEllipsesOutline
              className={styles.newMessage}
              size={25}
            />
          )}
        </div>
      ))}
      <div ref={chatRoomsEndRef} />
    </div>
  );
};

export default ChatList;


//   return (
//     <div className={styles.chatList} id='chatList'>
//       <List>
//         {chatRooms.map((room) => (
//           <React.Fragment key={room.id}>
//             <ListItem
//               className={styles.chatListItem}
//               onClick={() => { 
//                 let otherUserId = room.receiverId;
//                 if (otherUserId == userId) otherUserId = room.senderId;
//                 onSelectRoom(otherUserId, room);
//                 console.log(room)
//               }}
//             >
//               <ListItemText primary={`
//                 ${userId == room.receiverId
//                   ? room.senderNickname : room.receiverNickname}
//               `} />
//               {room.hasNewMessage && (
//                 <div>new!</div>
//               )}
//             </ListItem>
//             <Divider />
//           </React.Fragment>
//         ))}
//         <div ref={chatRoomsEndRef}/>
//       </List>
//       {/* </InfiniteScroll> */}
//     </div>
//   );
// };