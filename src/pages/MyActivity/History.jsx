import React, { useState, useEffect } from 'react'
import InfiniteScroll from 'react-infinite-scroll-component'
import axiosInstance from '../../util/axiosConfig'
import ListComponent from './ListComponent'
import Modal from '../BookRegister/Modal'
import styles from './History.module.css'

const MODAL_TYPES = {
  RENTAL_HISTORY: 'RENTAL_HISTORY',
  EXCHANGE_HISTORY: 'EXCHANGE_HISTORY'
}

const History = (userId) => {
  const [rentalHistory, setRentalHistory] = useState([]);
  const [rentalHistoryCnt, setRentalHistoryCnt] = useState(0);
  const [exchangeHistory, setExchangeHistory] = useState([]);
  const [exchangeHistoryCnt, setExchangeHistoryCnt] = useState(0);
  const [rentalPage, setRentalPage] = useState(0);
  const [exchangePage, setExchangePage] = useState(0);
  const [selectedItem, setSelectedItem] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalType, setModalType] = useState(null);

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
            page: 0,
            size: 10
          }
        }),
        axiosInstance.get('/exchanges', {
          params: {
            userCondition: "SENDER_RECEIVER",
            exchangeStatus: "APPROVED",
            page: 0,
            size: 10
          }
        })
      ])
      updateStateWithoutDuplicates(setRentalHistory, response1.data.content, setRentalHistoryCnt, setRentalPage, response1.data.totalElements)
      updateStateWithoutDuplicates(setExchangeHistory, response2.data.content, setExchangeHistoryCnt, setExchangePage, response2.data.totalElements)
    } catch (error) {
      console.log(error)
    }
  }

  const updateStateWithoutDuplicates = (setter, newItems, countSetter, pageSetter, totalElements) => {
    setter(prevItems => {
      const existingIds = new Set(prevItems.map(item => item.id));
      const uniqueNewItems = newItems.filter(item => !existingIds.has(item.id));
      return [...prevItems, ...uniqueNewItems];
    });
    countSetter(totalElements);
    pageSetter(1);
  };

  const fetchMoreRentals = async () => {
    try {
      const response = await axiosInstance.get('/rentals', {
        params: {
          userCondition: "SENDER",
          rentalStatus: "COMPLETED",
          page: rentalPage,
          size: 10
        }
      })
      const newItems = response.data.content.filter(
        newItem => !rentalHistory.some(item => item.id === newItem.id)
      )
      setRentalHistory(prev => [...prev, ...newItems])
      setRentalHistoryCnt(response.data.totalElements)
      setRentalPage(prev => prev + 1)
    } catch (error) {
      console.log(error)
    }
  }

  const fetchMoreExchanges = async () => {
    try {
      const response = await axiosInstance.get('/exchanges', {
        params: {
          userCondition: "SENDER_RECEIVER",
          exchangeStatus: "APPROVED",
          page: exchangePage,
          size: 10
        }
      })
      const newItems = response.data.content.filter(
        newItem => !exchangeHistory.some(item => item.id === newItem.id)
      )
      setExchangeHistory(prev => [...prev, ...newItems])
      setExchangeHistoryCnt(response.data.totalElements)
      setExchangePage(prev => prev + 1)
    } catch (error) {
      console.log(error)
    }
  }

  const openModal = (item, type) => {
    setSelectedItem(item);
    setModalType(type);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setSelectedItem(null);
    setModalType(null);
    setIsModalOpen(false);
  };

  const renderModalContent = () => {
    if (!selectedItem) return null;

    switch (modalType) {
      case MODAL_TYPES.RENTAL_HISTORY:
        return (
          <>
            <div className={styles.modalBook}>
              <div className={styles.modalBookImg}>
                <img src={selectedItem.userbook.bookInfo.thumbnail} alt=""/>
              </div>
              <div className={styles.modalBookInfo}>
                <h2>제목</h2>
                <p>{selectedItem.userbook.bookInfo.title}</p>
                <h2>저자</h2>
                <p>{selectedItem.userbook.bookInfo.author}</p>
                <h2>책권자</h2>
                <p>{selectedItem.userbook.ownerInfo.nickname}</p>
              </div>
            </div>
            <div className={styles.rentalInfo}>
              <h2>대여 시작일</h2>
              <p>{selectedItem.startDate.split('T')[0]}</p>
              <h2>대여 종료일</h2>
              <p>{selectedItem.endDate.split('T')[0]}</p>
            </div>
          </>
        );
      case MODAL_TYPES.EXCHANGE_HISTORY:
        return (
          <>
            {selectedItem.senderBook.ownerInfo.id === userId ? (
              <>
                <div className={styles.exModalBook}>
                  <div className={styles.senderBook}>
                    <p>상대 책</p>
                    <img src={selectedItem.receiverBook.bookInfo.thumbnail} alt="" />
                    <h2>제목</h2>
                    <p>{selectedItem.receiverBook.bookInfo.title}</p>
                    <h2>저자</h2>
                    <p>{selectedItem.receiverBook.bookInfo.author}</p>
                  </div>
                  <div className={styles.receiverBook}>
                    <p>내 책</p>
                    <img src={selectedItem.senderBook.bookInfo.thumbnail} alt="" />
                    <h2>제목</h2>
                    <p>{selectedItem.senderBook.bookInfo.title}</p>
                    <h2>저자</h2>
                    <p>{selectedItem.senderBook.bookInfo.author}</p>
                  </div>
                </div>
              </>
            ) : (
              <>
                <div className={styles.exModalBook}>
                  <div className={styles.senderBook}>
                    <p>상대 책</p>
                    <img src={selectedItem.senderBook.bookInfo.thumbnail} alt="" />
                    <h2>제목</h2>
                    <p>{selectedItem.senderBook.bookInfo.title}</p>
                    <h2>저자</h2>
                    <p>{selectedItem.senderBook.bookInfo.author}</p>
                  </div>
                  <div className={styles.receiverBook}>
                    <p>내 책</p>
                    <img src={selectedItem.receiverBook.bookInfo.thumbnail} alt="" />
                    <h2>제목</h2>
                    <p>{selectedItem.receiverBook.bookInfo.title}</p>
                    <h2>저자</h2>
                    <p>{selectedItem.receiverBook.bookInfo.author}</p>
                  </div>
                </div>
              </>
            )}
            <div className={styles.rentalInfo}>
              <h2>교환자</h2>
              <p>{selectedItem.receiverBook.ownerInfo.nickname}</p>
              <h2>교환일</h2>
              <p>{selectedItem.exchangeDate.split('T')[0]}</p>
            </div>
          </>
        );
      default:
        return null;
    }
  };

  return (
    <div className={styles.historyContainer}>
      <h2>대여했던 목록 (총 {rentalHistoryCnt}개)</h2>
      <div className={styles.listHeader}>
        <div>제목</div>
        <div>책권자</div>
      </div>
      <InfiniteScroll
        dataLength={rentalHistory.length}
        next={fetchMoreRentals}
        hasMore={rentalHistory.length < rentalHistoryCnt}
        loader={<h4>Loading...</h4>}
        scrollableTarget="rentalList"
      >
        <ListComponent
          items={rentalHistory}
          emptyMessage="목록이 없습니다"
          renderItem={(item) => (
            <div className={styles.listItem} onClick={() => openModal(item, MODAL_TYPES.RENTAL_HISTORY)} style={{cursor: 'pointer'}}>
              <div>{item.userbook.bookInfo.title}</div>
              <div>{item.userbook.ownerInfo.nickname}</div>
            </div>
          )}
        />
      </InfiniteScroll>

      <h2>교환했던 목록 (총 {exchangeHistoryCnt}개)</h2>
      <div className={styles.listHeader}>
        <div>상대책</div>
        <div>내책</div>
      </div>
      <InfiniteScroll
        dataLength={exchangeHistory.length}
        next={fetchMoreExchanges}
        hasMore={exchangeHistory.length < exchangeHistoryCnt}
        loader={<h4>Loading...</h4>}
        scrollableTarget="exchangeList"
      >
        <ListComponent
          items={exchangeHistory}
          emptyMessage="목록이 없습니다"
          renderItem={(item) => (
            <div className={styles.exchangeBook} onClick={() => openModal(item, MODAL_TYPES.EXCHANGE_HISTORY)} style={{cursor: 'pointer'}}>
              {item.senderBook.ownerInfo.id === userId ? (
                <>
                  <div className={styles.senderBook}>{item.receiverBook.bookInfo.title}</div>
                  <div className={styles.receiverBook}>{item.senderBook.bookInfo.title}</div>
                </>
              ) : (
                <>
                  <div className={styles.senderBook}>{item.senderBook.bookInfo.title}</div>
                  <div className={styles.receiverBook}>{item.receiverBook.bookInfo.title}</div>
                </>
              )}
            </div>
          )}
        />
      </InfiniteScroll>

      <Modal
        isOpen={isModalOpen}
        onRequestClose={closeModal}
        contentLabel="상세 정보"
      >
        {renderModalContent()}
      </Modal>
    </div>
  )
}

export default History