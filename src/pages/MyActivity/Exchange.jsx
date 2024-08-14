import { useState, useEffect } from 'react'
import Swal from 'sweetalert2'
import InfiniteScroll from 'react-infinite-scroll-component'
import axiosInstance from '../../util/axiosConfig'
import ListComponent from './ListComponent'
import Modal from '../BookRegister/Modal'
import styles from './Exchange.module.css'
import { FaExchangeAlt } from "react-icons/fa";

const MODAL_TYPES = {
  EXCHANGE_REQUEST: 'EXCHANGE_REQUEST',
  RECEIVED_REQUEST: 'RECEIVED_REQUEST',
  REJECTED_REQUEST: 'REJECTED_REQUEST'
};

const Exchange = () => {
  const [exchangeRequests, setExchangeRequests] = useState([]);
  const [exchangeRequestsCnt, setExchangeRequestsCnt] = useState(0);
  const [receivedExchangeRequests, setReceivedExchangeRequests] = useState([]);
  const [receivedExchangeRequestsCnt, setReceivedExchangeRequestsCnt] = useState(0);
  const [rejectedExchangeRequests, setRejectedExchangeRequests] = useState([]);
  const [rejectedExchangeRequestsCnt, setRejectedExchangeRequestsCnt] = useState(0);
  const [exchangeRequestsPage, setExchangeRequestsPage] = useState(0);
  const [receivedExchangeRequestsPage, setReceivedExchangeRequestsPage] = useState(0);
  const [rejectedExchangeRequestsPage, setRejectedExchangeRequestsPage] = useState(0);
  const [selectedItem, setSelectedItem] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalType, setModalType] = useState(null);
  const [isExchangeRequestsExpanded, setIsExchangeRequestsExpanded] = useState(false);
  const [isReceivedRequestsExpanded, setIsReceivedRequestsExpanded] = useState(false);
  const [isRejectedRequestsExpanded, setIsRejectedRequestsExpanded] = useState(false);

  useEffect(() => {
    fetchExchangeList()
  }, [])

  const fetchExchangeList = async () => {
    try {
      const [response1, response2, response3] = await Promise.all([
        axiosInstance.get('/exchanges', {
          params: {
            userCondition: "SENDER",
            exchangeStatus: "IN_PROGRESS",
            page: 0,
            size: 10
          }
        }),
        axiosInstance.get('/exchanges', {
          params: {
            userCondition: "RECEIVER",
            exchangeStatus: "IN_PROGRESS",
            page: 0,
            size: 10
          }
        }),
        axiosInstance.get('/exchanges', {
          params: {
            userCondition: "SENDER",
            exchangeStatus: "REJECTED",
            page: 0,
            size: 10
          }
        })
      ])
      updateStateWithoutDuplicates(setExchangeRequests, response1.data.content, setExchangeRequestsCnt, setExchangeRequestsPage, response1.data.totalElements)
      updateStateWithoutDuplicates(setReceivedExchangeRequests, response2.data.content, setReceivedExchangeRequestsCnt, setReceivedExchangeRequestsPage, response2.data.totalElements)
      updateStateWithoutDuplicates(setRejectedExchangeRequests, response3.data.content, setRejectedExchangeRequestsCnt, setRejectedExchangeRequestsPage, response3.data.totalElements)
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

  const fetchExchangeRequests = async (reset = false) => {
    try {
      const page = reset ? 0 : exchangeRequestsPage;
      const response = await axiosInstance.get('/exchanges', {
        params: {
          userCondition: "SENDER",
          exchangeStatus: "IN_PROGRESS",
          page: page,
          size: 10
        }
      })

      const newItems = response.data.content;

      if (reset) {
        setExchangeRequests(newItems);
        setExchangeRequestsPage(0);
      } else {
        setExchangeRequests(prev => {
          const existingIds = new Set(prev.map(item => item.id));
          const uniqueNewItems = newItems.filter(item => !existingIds.has(item.id));
          return [...prev, ...uniqueNewItems];
        });
        setExchangeRequestsPage(prev => prev + 1);
      }
      setExchangeRequestsCnt(response.data.totalElements);
    } catch (error) {
      console.error(error)
    }
  }

  const fetchReceivedExchangeRequests = async (reset = false) => {
    try {
      const page = reset ? 0 : receivedExchangeRequestsPage;
      const response = await axiosInstance.get('/exchanges', {
        params: {
          userCondition: "RECEIVER",
          exchangeStatus: "IN_PROGRESS",
          page: page,
          size: 10
        }
      })

      const newItems = response.data.content;

      if (reset) {
        setReceivedExchangeRequests(newItems)
        setReceivedExchangeRequestsPage(0)
      } else {
        setReceivedExchangeRequests(prev => {
          const existingIds = new Set(prev.map(item => item.id));
          const uniqueNewItems = newItems.filter(item => !existingIds.has(item.id));
          return [...prev, ...uniqueNewItems];
        })
        setReceivedExchangeRequestsPage(prev => prev + 1)
      }
      setReceivedExchangeRequestsCnt(response.data.totalElements)
    } catch (error) {
      console.error(error)
    }
  }

  const fetchRejectedExchangeRequests = async () => {
    try {
      const response = await axiosInstance.get('/exchanges', {
        params: {
          userCondition: "SENDER",
          exchangeStatus: "REJECTED",
          page: rejectedExchangeRequestsPage,
          size: 10
        }
      })

      const newItems = response.data.content;

      setRejectedExchangeRequests(prev => {
        const existingIds = new Set(prev.map(item => item.id));
        const uniqueNewItems = newItems.filter(item => !existingIds.has(item.id));
        return [...prev, ...uniqueNewItems];

      })
      setRejectedExchangeRequestsCnt(response.data.totalElements)
      setRejectedExchangeRequestsPage(prev => prev + 1)
    } catch (error) {
      console.error(error)
    }
  }

  const loadMoreExchangeRequests = () => {
    fetchExchangeRequests()
  }

  const loadMoreReceivedExchangeRequests = () => {
    fetchReceivedExchangeRequests()
  }

  const loadMoreRejectedExchangeRequests = () => {
    fetchRejectedExchangeRequests()
  }

  const handleCancelExchange = async (offerId) => {
    Swal.fire({
      title: '교환 신청을 취소하시겠습니까?',
      showCancelButton: true,
      confirmButtonText: '신청취소',
      cancelButtonText: '돌아가기',
      icon: 'question'
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await axiosInstance.delete(`/exchanges/offer/${offerId}`)
          await fetchExchangeRequests(true)
          Swal.fire({
            title: '교환 신청 취소',
            text: '교환 신청을 취소했습니다.',
            confirmButtonText: '확인',
            icon: 'error'
          })
          closeModal()
        } catch (error) {
          console.error(error)
        }
      }
    })
  }

  const handleAccept = async (offerId, response) => {
    const confirmationOptions = getConfirmationOptions(response);
    
    Swal.fire(confirmationOptions).then(async (result) => {
      if (result.isConfirmed) {
        await processExchangeRequest(offerId, response);
      }
    });
  };

  const getConfirmationOptions = (response) => {
    return response === true
    ? {
      title: '교환 신청을 수락하시겠습니까?',
      showCancelButton: true,
      confirmButtonText: '수락',
      cancelButtonText: '취소',
      icon: 'question'
    }
    : {
      title: '교환 신청을 거절하시겠습니까?',
      showCancelButton: true,
      confirmButtonText: '거절',
      cancelButtonText: '취소',
      icon: 'question',
    };
  };

  const processExchangeRequest = async (offerId, response) => {
    try {
      const status = response === true ? "APPROVED" : "REJECTED";
      const successMessage = response === true ? '교환 신청을 수락했습니다.' : '교환 신청을 거절했습니다.';
      const successIcon = response === true ? 'success' : 'error';

      await axiosInstance.post(`/exchanges/offer/${offerId}`, { status });
      await fetchReceivedExchangeRequests(true);
        
      Swal.fire({
        title: response === true ? '교환 신청 수락' : '교환 신청 거절',
        text: successMessage,
        confirmButtonText: '확인',
        icon: successIcon
      });

      closeModal();
    } catch (error) {
      console.error(error);
    }
  };

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
      case MODAL_TYPES.EXCHANGE_REQUEST:
        return (
          <>
            <div className={styles.modalBook}>
              <div className={styles.senderBook}>
                <h2>상대 책</h2>
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
                <h2>교환승인자</h2>
                <div className={styles.modalContent}>
                  {selectedItem.receiverBook.ownerInfo.nickname === 'anonymous'
                  ? '(알 수 없음)' : selectedItem.receiverBook.ownerInfo.nickname}
                </div>
              </div>
              <div className={styles.receiverBook}>
                <h2>내 책</h2>
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
            <div className={styles.buttons}>
              <button className={styles.modalButton} onClick={() => handleCancelExchange(selectedItem.id)}>신청취소</button>
            </div>
          </>
        );
      case MODAL_TYPES.RECEIVED_REQUEST:
        return (
          <>
            <div className={styles.modalBook}>
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
                <h2>교환신청자</h2>
                <div className={styles.modalContent}>
                  {selectedItem.senderBook.ownerInfo.nickname === 'anonymous'
                  ? '(알 수 없음)' : selectedItem.senderBook.ownerInfo.nickname}
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
                <h2></h2>
                <div className={styles.modalContent}></div>
              </div>
            </div>
            <div className={styles.buttons}>
              <button className={styles.modalButton} onClick={() => handleAccept(selectedItem.id, true)}>수락</button>
              <button className={styles.modalButton} onClick={() => handleAccept(selectedItem.id, false)}>거절</button>
            </div>
          </>
        );
      // case MODAL_TYPES.REJECTED_REQUEST:
      //   return (
      //     <>
      //       <div>{item.senderBook.bookInfo.title}</div>
      //       <div>{item.receiverBook.bookInfo.title}</div>
      //     </>
      //   );
      default:
        return null;
    }
  }

  const toggleExpansion = (setter) => {
    setter(prev => !prev);
  };

  return (
    <div className={styles.exchangeContainer}>
      <div className={`${styles.exchangeContainerInner} ${isExchangeRequestsExpanded ? styles.expanded : ''}`}>
        <h2 onClick={() => toggleExpansion(setIsExchangeRequestsExpanded)}>
          <FaExchangeAlt size={'15px'} /> 교환 신청한 목록 (총 {exchangeRequestsCnt}개)
        </h2>
        <div className={styles.expandableContent}>
          <div className={styles.listHeader}>
            <div>상대 책</div>
            <div>내 책</div>
          </div>
          <InfiniteScroll
            dataLength={exchangeRequests.length}
            next={loadMoreExchangeRequests}
            hasMore={exchangeRequests.length < exchangeRequestsCnt}
            loader={<h4>Loading...</h4>}
            scrollableTarget="exchangeRequestsList"
          >
            <ListComponent
              items={exchangeRequests}
              emptyMessage="목록이 없습니다"
              renderItem={(item) => (
                <div className={styles.exchangeBook} onClick={() => openModal(item, MODAL_TYPES.EXCHANGE_REQUEST)} style={{cursor: 'pointer'}}>
                  <div className={styles.senderBook}>{item.receiverBook.bookInfo.title}</div>
                  <div className={styles.receiverBook}>{item.senderBook.bookInfo.title}</div>
                </div>
              )}
            />
          </InfiniteScroll>
        </div>
      </div>


      <div className={`${styles.exchangeContainerInner} ${isReceivedRequestsExpanded ? styles.expanded : ''}`}>
        <h2 onClick={() => toggleExpansion(setIsReceivedRequestsExpanded)}>
          <FaExchangeAlt size={'15px'} /> 교환 신청 받은 목록 (총 {receivedExchangeRequestsCnt}개)
        </h2>
        <div className={styles.expandableContent}>
          <div className={styles.listHeader}>
            <div>상대 책</div>
            <div>내 책</div>
          </div>
          <InfiniteScroll
            dataLength={receivedExchangeRequests.length}
            next={loadMoreReceivedExchangeRequests}
            hasMore={receivedExchangeRequests.length < receivedExchangeRequestsCnt}
            loader={<h4>Loading...</h4>}
            scrollableTarget="receivedExchangeRequestsList"
          >
            <ListComponent
              items={receivedExchangeRequests}
              emptyMessage="목록이 없습니다"
              renderItem={(item) => (
                <div className={styles.exchangeBook} onClick={() => openModal(item, MODAL_TYPES.RECEIVED_REQUEST)} style={{cursor: 'pointer'}}>
                  <div className={styles.senderBook}>{item.senderBook.bookInfo.title}</div>
                  <div className={styles.receiverBook}>{item.receiverBook.bookInfo.title}</div>
                </div>
              )}
            />
          </InfiniteScroll>
        </div>
      </div>
      
      <div className={`${styles.exchangeContainerInner} ${isRejectedRequestsExpanded ? styles.expanded : ''}`}>
        <h2 onClick={() => toggleExpansion(setIsRejectedRequestsExpanded)}>
          <FaExchangeAlt size={'15px'} /> 교환 신청 거절 당한 내역 (총 {rejectedExchangeRequestsCnt}개)
        </h2>
        <div className={styles.expandableContent}>  
          <div className={styles.listHeader}>
            <div>상대 책</div>
            <div>내 책</div>
          </div>
          <InfiniteScroll
            dataLength={rejectedExchangeRequests.length}
            next={loadMoreRejectedExchangeRequests}
            hasMore={rejectedExchangeRequests.length < rejectedExchangeRequestsCnt}
            loader={<h4>Loding...</h4>}
            scrollableTarget="rejectedExchangeRequestsList"
          >
            <ListComponent
              items={rejectedExchangeRequests}
              emptyMessage="목록이 없습니다"
              renderItem={(item) => (
                <div className={styles.exchangeBook}>
                  <div className={styles.senderBook}>{item.receiverBook.bookInfo.title}</div>
                  <div className={styles.receiverBook}>{item.senderBook.bookInfo.title}</div>
                </div>
              )}
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

export default Exchange
