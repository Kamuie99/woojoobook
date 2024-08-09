import React, { useState, useEffect } from 'react';
import axiosInstance from '../../util/axiosConfig'
import InfiniteScroll from 'react-infinite-scroll-component'
import styles from './Registered.module.css';
import BookInfo from './BookInfo';
import ListComponent from './ListComponent'

const Registered = () => {
  const [registeredUserbooks, setRegisteredUserbooks] = useState([]);
  const [page, setPage] = useState(0);
  const [registeredUserbooksCount, setRegisteredUserbooksCount] = useState(0);
  const [totalElementsCount, setTotalElementsCount] = useState(0);
  const [tradeStatus, setTradeStatus] = useState(() => {
    const storedContent = localStorage.getItem('tradeStatus');
    return storedContent || '';
  })

  useEffect(() => {
    localStorage.setItem('tradeStatus', tradeStatus);
  }, [tradeStatus]);
  
  useEffect(() => {
    fetchRegisteredUserbooks(true, tradeStatus);
  }, [])

  useEffect(() => {
    localStorage.setItem('tradeStatus', tradeStatus);
    return () => {
      localStorage.removeItem('tradeStatus');
    }
  }, [tradeStatus]);
  
  const handleSelectChange = (e) => {
    setTradeStatus(e.target.value)
    setPage(0);
    fetchRegisteredUserbooks(true, e.target.value);
  }

  const fetchRegisteredUserbooks = async (init = false, tradeStatus) => {
    try {
      const response = await axiosInstance.get('/users/userbooks/registered', {
        params: {
          tradeStatus,
          page: init ? 0 : page,
          size: 10
        }
      });
      const newItems = response.data.content;
      if (init) {
        setRegisteredUserbooks(newItems);
        setPage(1);
      } else {
        setRegisteredUserbooks(prev => [...prev,...newItems]);
        setPage(prev => prev + 1);
      }
      if (tradeStatus === '') {
        setTotalElementsCount(response.data.totalElements);
      }
      setRegisteredUserbooksCount(response.data.totalElements);
    } catch (error) {
      console.error(error);
    }
  }

  const loadMoreRegisteredUserbooks = () => {
    fetchRegisteredUserbooks(false, tradeStatus);
  }

  return (
    <>
    <div className={styles.miniHeader}>
      <h2 className={styles.registered}><strong>내가 등록한 책 | </strong> {totalElementsCount}권</h2>
      <select className={styles.option} value={tradeStatus} onChange={handleSelectChange}>
        <option value="">전체</option>
        {/* <option value="RENTAL_EXCHANGE_AVAILABLE">대여, 교환 가능</option> */}
        <option value="RENTAL_AVAILABLE">대여 가능</option>
        <option value="EXCHANGE_AVAILABLE">교환 가능</option>
        <option value="RENTED">대여중</option>
        <option value="UNAVAILABLE">거래 불가능</option>
      </select>
    </div>
    <div
      className={styles.registeredUserbooks}
      id="registeredUserbooksList"
      style={{ height: 'calc(100vh - 320px)', overflow: 'auto' }}
    >
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
              key={item.id}
                item={item}
              />
            </>
          )}
        />
      </InfiniteScroll>
    </div>
    </>
  )
}

export default Registered;