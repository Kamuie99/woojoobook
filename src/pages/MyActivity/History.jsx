import { useState, useEffect } from 'react'
import InfiniteScroll from 'react-infinite-scroll-component'
import axiosInstance from '../../util/axiosConfig'
import ListComponent from './ListComponent'
import Modal from '../BookRegister/Modal'
import styles from './History.module.css'
import { FiList } from "react-icons/fi";

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
  const [isRentalExpanded, setIsRentalExpanded] = useState(false);
  const [isExchangeExpanded, setIsExchangeExpanded] = useState(false);

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
      console.error(error)
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
      console.error(error)
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
      console.error(error)
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
                <div className={styles.modalContent}>
                  {selectedItem.userbook.bookInfo.title}
                </div>
                <h2>저자</h2>
                <div className={styles.modalContent}>
                  {selectedItem.userbook.bookInfo.author}
                </div>
                <h2>책권자</h2>
                <div className={styles.modalContent}>
                  {selectedItem.userbook.ownerInfo.nickname === 'anonymous'
                    ? '(알 수 없음)' : selectedItem.userbook.ownerInfo.nickname}
                </div>
                <h2></h2>
                <div className={styles.modalContent}></div>
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
            {selectedItem.senderBook.ownerInfo.id == userId.userId ? (
              <>
                <div className={styles.exModalBook}>
                  <div className={styles.senderBook}>
                    <p>상대 책</p>
                    <div className={styles.modalImg}>
                      <img src={selectedItem.receiverBook.bookInfo.thumbnail} alt="" />
                    </div>
                    <h2>제목</h2>
                    <div className={styles.modalContent}>
                      {selectedItem.receiverBook.bookInfo.title}
                    </div>
                    <h2>저자</h2>
                    <div className={styles.modalContent}>
                      {selectedItem.receiverBook.bookInfo.author}
                    </div>
                  </div>
                  <div className={styles.receiverBook}>
                    <p>내 책</p>
                    <div className={styles.modalImg}>
                      <img src={selectedItem.senderBook.bookInfo.thumbnail} alt="" />
                    </div>
                    <h2>제목</h2>
                    <div className={styles.modalContent}>
                      {selectedItem.senderBook.bookInfo.title}
                    </div>
                    <h2>저자</h2>
                    <div className={styles.modalContent}>
                      {selectedItem.senderBook.bookInfo.author}
                    </div>
                  </div>
                </div>
                <div className={styles.rentalInfo}>
                  <h2>교환자</h2>
                  <p>
                    {selectedItem.receiverBook.ownerInfo.nickname === 'anonymous'
                    ? '(알 수 없음)' : selectedItem.receiverBook.ownerInfo.nickname}
                  </p>
                  <h2>교환일</h2>
                  <p>{selectedItem.exchangeDate.split('T')[0]}</p>
                </div>
              </>
            ) : (
              <>
                <div className={styles.exModalBook}>
                  <div className={styles.senderBook}>
                    <h2>상대 책</h2>
                    <div className={styles.modalImg}>
                      <img src={selectedItem.senderBook.bookInfo.thumbnail} alt="" />
                    </div>
                    <h2>제목</h2>
                    <div className={styles.modalContent}>
                      {selectedItem.senderBook.bookInfo.title}
                    </div>
                    <h2>저자</h2>
                    <div className={styles.modalContent}>
                      {selectedItem.senderBook.bookInfo.author}
                    </div>
                  </div>
                  <div className={styles.receiverBook}>
                    <h2>내 책</h2>
                    <div className={styles.modalImg}>
                      <img src={selectedItem.receiverBook.bookInfo.thumbnail} alt="" />
                    </div>
                    <h2>제목</h2>
                    <div className={styles.modalContent}>
                      {selectedItem.receiverBook.bookInfo.title}
                    </div>
                    <h2>저자</h2>
                    <div className={styles.modalContent}>
                      {selectedItem.receiverBook.bookInfo.author}
                    </div>
                  </div>
                </div>
                <div className={styles.rentalInfo}>
                  <h2>교환자</h2>
                  <p>
                    {selectedItem.senderBook.ownerInfo.nickname === 'anonymous'
                    ? '(알 수 없음)' : selectedItem.senderBook.ownerInfo.nickname}
                  </p>
                  <h2>교환일</h2>
                  <p>{selectedItem.exchangeDate.split('T')[0]}</p>
                </div>
              </>
            )}
            {/* <div className={styles.rentalInfo}>
              <h2>교환자</h2>
              <p>{selectedItem.receiverBook.ownerInfo.nickname}</p>
              <h2>교환일</h2>
              <p>{selectedItem.exchangeDate.split('T')[0]}</p>
            </div> */}
          </>
        );
      default:
        return null;
    }
  };

  const toggleRentalExpansion = () => {
    setIsRentalExpanded(!isRentalExpanded);
  };

  const toggleExchangeExpansion = () => {
    setIsExchangeExpanded(!isExchangeExpanded);
  };

  return (
    <div className={styles.historyContainer}>
      <div className={`${styles.historyContainerInner} ${isRentalExpanded ? styles.expanded : ''}`}>
        <h2 onClick={toggleRentalExpansion}>
          <FiList /> 대여했던 목록 (총 {rentalHistoryCnt}개)
        </h2>
        <div className={styles.expandableContent}>
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
                  <div>
                    {item.userbook.ownerInfo.nickname === 'anonymous'
                    ? '(알 수 없음)' : item.userbook.ownerInfo.nickname}
                  </div>
                </div>
              )}
            />
          </InfiniteScroll>
        </div>
      </div>

      <div className={`${styles.historyContainerInner} ${isExchangeExpanded ? styles.expanded : ''}`}>
        <h2 onClick={toggleExchangeExpansion}>
          <FiList /> 교환했던 목록 (총 {exchangeHistoryCnt}개)
        </h2>
        <div className={styles.expandableContent}>
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
              renderItem={(item) => {
                // console.log('Item:', item);
                // console.log('userId:', userId);
                // console.log('Sender ID:', item.senderBook.ownerInfo.id);
                // console.log('Is sender:', item.senderBook.ownerInfo.id == userId);
              
                return (
                  <div className={styles.exchangeBook} onClick={() => openModal(item, MODAL_TYPES.EXCHANGE_HISTORY)} style={{cursor: 'pointer'}}>
                    <div className={styles.senderBook}>
                      {item.senderBook.ownerInfo.id == userId.userId 
                        ? item.receiverBook.bookInfo.title
                        : item.senderBook.bookInfo.title
                      }
                    </div>
                    <div className={styles.receiverBook}>
                      {item.senderBook.ownerInfo.id == userId.userId
                        ? item.senderBook.bookInfo.title
                        : item.receiverBook.bookInfo.title
                      }
                    </div>
                  </div>
                );
              }}
            />
          </InfiniteScroll>
        </div>
      </div>

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