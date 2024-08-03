import React, { useState, useEffect } from 'react'
import InfiniteScroll from 'react-infinite-scroll-component'
import axiosInstance from '../../util/axiosConfig'
import ListComponent from './ListComponent'
import styles from './History.module.css'

const History = () => {
  const [rentalHistory, setRentalHistory] = useState([]);
  const [rentalHistoryCnt, setRentalHistoryCnt] = useState(0);
  const [exchangeHistory, setExchangeHistory] = useState([]);
  const [exchangeHistoryCnt, setExchangeHistoryCnt] = useState(0);
  const [rentalPage, setRentalPage] = useState(0);
  const [exchangePage, setExchangePage] = useState(0);

  useEffect(() => {
    fetchHistory()
  }, [])

  const fetchHistory = async () => {
    try {
      const [response1, response2] = await Promise.all([
        axiosInstance.get('/rentals', {
          params: {
            userCondition: "SENDER",
            rentalStatus: "COMPLETED",
            page: rentalPage,
            size: 10
          }
        }),
        axiosInstance.get('/exchanges', {
          params: {
            userCondition: "SENDER_RECEIVER",
            exchangeStatus: "APPROVED",
            page: exchangePage,
            size: 10
          }
        })
      ])
      setRentalHistory(prev => [...prev, ...response1.data.content])
      setRentalHistoryCnt(response1.data.totalElements)

      setExchangeHistory(prev => [...prev, ...response2.data.content])
      setExchangeHistoryCnt(response2.data.totalElements)
    } catch (error) {
      console.log(error)
    }
  }

  const loadMoreRentals = () => {
    setRentalPage(prev => prev + 1)
    fetchHistory()
  }

  const loadMoreExchanges = () => {
    setExchangePage(prev => prev + 1)
    fetchHistory()
  }

  return (
    <div className={styles.historyContainer}>
      <h2>대여했던 목록 (총 {rentalHistoryCnt}개)</h2>
      <InfiniteScroll
        dataLength={rentalHistory.length}
        next={loadMoreRentals}
        hasMore={rentalHistory.length < rentalHistoryCnt}
        loader={<h4>Loading...</h4>}
        scrollableTarget="rentalList"
      >
        <ListComponent
          items={rentalHistory}
          emptyMessage="목록이 없습니다"
          renderItem={(item) => (
            <>
              <div>{item.userbook.bookInfo.title}</div>
              <div>{item.userbook.ownerInfo.nickname}</div>
              <div>{item.startDate}</div>
              <div>{item.endDate}</div>
            </>
          )}
        />
      </InfiniteScroll>

      <h2>교환했던 목록 (총 {exchangeHistoryCnt}개)</h2>
      <InfiniteScroll
        dataLength={exchangeHistory.length}
        next={loadMoreExchanges}
        hasMore={exchangeHistory.length < exchangeHistoryCnt}
        loader={<h4>Loading...</h4>}
        scrollableTarget="exchangeList"
      >
        <ListComponent
          items={exchangeHistory}
          emptyMessage="목록이 없습니다"
          renderItem={(item) => (
            <>
              {item.senderBook.ownerInfo.id === userId ? (
                <>
                  <div>{item.senderBook.bookInfo.title}</div>
                  <div>{item.senderBook.ownerInfo.nickname}</div>
                  <div>{item.receiverBook.bookInfo.title}</div>
                  <div>{item.receiverBook.ownerInfo.nickname}</div>
                </>
              ) : (
                <>
                  <div>{item.receiverBook.bookInfo.title}</div>
                  <div>{item.receiverBook.ownerInfo.nickname}</div>
                  <div>{item.senderBook.bookInfo.title}</div>
                  <div>{item.senderBook.ownerInfo.nickname}</div>
                </>
              )}
              <div>{item.exchangeDate}</div>
            </>
          )}
        />
      </InfiniteScroll>
    </div>
  )
}

export default History