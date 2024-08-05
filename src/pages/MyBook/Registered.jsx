import React, { useState, useEffect } from 'react';
import axiosInstance from '../../util/axiosConfig'
import InfiniteScroll from 'react-infinite-scroll-component'
import styles from './Registered.module.css';
import BookInfo from './BookInfo';
import ListComponent from './ListComponent'

const Registered = () => {
  const [registeredUserbooks, setRegisteredUserbooks] = useState([]);
  const [registeredUserbooksCount, setRegisteredUserbooksCount] = useState(0);
  const [page, setPage] = useState(0);
  
  useEffect(() => {
    fetchRegisteredUserbooks(true);
  }, [])

  useEffect(() => {
    console.log(registeredUserbooks)
  }, [registeredUserbooks]);
  
  const fetchRegisteredUserbooks = async (init = false) => {
    try {
      const response = await axiosInstance.get('/users/userbooks/registered', {
        params: {
          page,
          size: 10
        }
      });
      console.log(response.data);

      const newItems = response.data.content;
      if (init) {
        setRegisteredUserbooks(newItems);
        setRegisteredUserbooksCount(response.data.numberOfElements);
        setPage(0);
      } else {
        setRegisteredUserbooks(prev => [...prev,...newItems]);
        setRegisteredUserbooksCount(newItems.data.numberOfElements);
        setPage(prev => prev + 1);
      }

    } catch (error) {
      console.error(error);
    }
  }

  const loadMoreRegisteredUserbooks = () => {
    fetchRegisteredUserbooks();
  }

  return (
    <div className={styles.registeredContainer}>
      <p>Registered {registeredUserbooksCount}</p>
      <InfiniteScroll
        dataLength={registeredUserbooks.length}
        next={loadMoreRegisteredUserbooks}
        hasMore={registeredUserbooks.length < registeredUserbooksCount}
        loader={<h4>Loading...</h4>}
        scrollableTarget="registeredUserbooksList"
      >
        <ListComponent
          items={registeredUserbooks}
          emptyMessage="목록이 없습니다"
          renderItem={(item) => (
            <>
              <BookInfo 
                item={item}
              />
            </>
          )}
        />
      </InfiniteScroll>
    </div>
  )
}

export default Registered;