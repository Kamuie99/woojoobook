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
      const newItems = response.data.content;
      console.log(newItems);
      if (init) {
        setRegisteredUserbooks(newItems);
        setPage(0);
      } else {
        setRegisteredUserbooks(prev => [...prev,...newItems]);
        setPage(prev => prev + 1);
      }
      setRegisteredUserbooksCount(response.data.numberOfElements);
    } catch (error) {
      console.error(error);
    }
  }

  const loadMoreRegisteredUserbooks = () => {
    fetchRegisteredUserbooks();
  }

  return (
    <>
    {/* TODO: 내가 등록한 책 중에서, 전체 / 대여중 / 비 대여중 필터 선택할 수 있게 */}
      <h2 className={styles.registered}><strong>내가 등록한 책 | </strong> {registeredUserbooksCount}권</h2>
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
    </>
  )
}

export default Registered;