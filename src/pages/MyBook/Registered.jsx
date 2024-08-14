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
    fetchRegisteredUserbooks(tradeStatus, true);
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
    fetchRegisteredUserbooks(e.target.value, true);
  }

  const fetchRegisteredUserbooks = async (tradeStatus, init = false) => {
    try {
      const [registeredResponse, rentalResponse] = await Promise.all([
        axiosInstance.get('/users/userbooks/registered', {
          params: {
            tradeStatus,
            page: init ? 0 : page,
            size: 10
          }
        }),
        axiosInstance.get('/rentals', {
          params: {
            userCondition: "RECEIVER",
            rentalStatus: "IN_PROGRESS",
            size: registeredUserbooksCount ? registeredUserbooksCount : 100,
          }
        })
      ])
      const registeredBooks = registeredResponse.data.content;
      const rentedBooks = rentalResponse.data.content;

      const mergedBooks = registeredBooks.map(book => {
        const rentedBook = rentedBooks.find(rental => rental.userbook.id === book.id);
        return rentedBook
          ? { ...book, startDate: rentedBook.startDate, endDate: rentedBook.endDate }
          : book;
      })
      
      if (init) {
        setRegisteredUserbooks(mergedBooks);
        setPage(1);
      } else {
        setRegisteredUserbooks(prev => [...prev,...mergedBooks]);
        setPage(prev => prev + 1);
      }
      if (tradeStatus === '') {
        setTotalElementsCount(registeredResponse.data.totalElements);
      }
      setRegisteredUserbooksCount(registeredResponse.data.totalElements);
    } catch (error) {
      console.error(error);
    }
  }

  const loadMoreRegisteredUserbooks = () => {
    fetchRegisteredUserbooks(tradeStatus);
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
        {/* <option value="UNAVAILABLE">거래 불가능</option> */}
      </select>
    </div>
    <div
      className={styles.registeredUserbooksList}
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
                fetchRegisteredUserbooks={fetchRegisteredUserbooks}
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