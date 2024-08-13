import { useState, useEffect } from 'react'
import Swal from 'sweetalert2'
import InfiniteScroll from 'react-infinite-scroll-component'
import axiosInstance from '../../util/axiosConfig'
import ListComponent from './ListComponent'
import Modal from '../BookRegister/Modal'
import styles from './Rental.module.css'
import { FiList } from "react-icons/fi";

const MODAL_TYPES = {
  CURRENT_RENT: 'CURRENT_RENT',
  RENTAL_REQUEST: 'RENTAL_REQUEST',
  RECEIVED_REQUEST: 'RECEIVED_REQUEST',
  REJECTED_REQUEST: 'REJECTED_REQUEST'
};

const Rental = () => {
  const [currentRent, setCurrentRent] = useState([]);
  const [currentRentCnt, setCurrentRentCnt] = useState(0);
  const [rentalRequests, setRentalRequests] = useState([]);
  const [rentalRequestsCnt, setRentalRequestsCnt] = useState(0);
  const [receivedRentalRequests, setReceivedRentalRequests] = useState([]);
  const [receivedRentalRequestsCnt, setReceivedRentalRequestsCnt] = useState(0);
  const [rejectedRentalRequests, setRejectedRentalRequests] = useState([]);
  const [rejectedRentalRequestsCnt, setRejectedRentalRequestsCnt] = useState(0);
  const [currentRentPage, setCurrentRentPage] = useState(0);
  const [rentalRequestsPage, setRentalRequestsPage] = useState(0);
  const [receivedRentalRequestsPage, setReceivedRentalRequestsPage] = useState(0);
  const [rejectedRentalRequestsPage, setRejectedRentalRequestsPage] = useState(0);
  const [selectedItem, setSelectedItem] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalType, setModalType] = useState(null);
  const [isCurrentRentExpanded, setIsCurrentRentExpanded] = useState(false);
  const [isRentalRequestsExpanded, setIsRentalRequestsExpanded] = useState(false);
  const [isReceivedRequestsExpanded, setIsReceivedRequestsExpanded] = useState(false);
  const [isRejectedRequestsExpanded, setIsRejectedRequestsExpanded] = useState(false);

  useEffect(() => {
    fetchRentalList()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const fetchRentalList = async () => {
    try {
      const [response1, response2, response3, response4] = await Promise.all([
        axiosInstance.get('/rentals', {
          params: {
            userCondition: "SENDER",
            rentalStatus: "IN_PROGRESS",
            page: 0,
            size: 10
          }
        }),
        axiosInstance.get('/rentals', {
          params: {
            userCondition: "SENDER",
            rentalStatus: "OFFERING",
            page: 0,
            size: 10
          }
        }),
        axiosInstance.get('/rentals', {
          params: {
            userCondition: "RECEIVER",
            rentalStatus: "OFFERING",
            page: 0,
            size: 10
          }
        }),
        axiosInstance.get('/rentals', {
          params: {
            userCondition: "SENDER",
            rentalStatus: "REJECTED",
            page: 0,
            size: 10
          }
        })
      ])
      updateStateWithoutDuplicates(setCurrentRent, response1.data.content, setCurrentRentCnt, setCurrentRentPage, response1.data.totalElements);
      updateStateWithoutDuplicates(setRentalRequests, response2.data.content, setRentalRequestsCnt, setRentalRequestsPage, response2.data.totalElements);
      updateStateWithoutDuplicates(setReceivedRentalRequests, response3.data.content, setReceivedRentalRequestsCnt, setReceivedRentalRequestsPage, response3.data.totalElements);
      updateStateWithoutDuplicates(setRejectedRentalRequests, response4.data.content, setRejectedRentalRequestsCnt, setRejectedRentalRequestsPage, response4.data.totalElements);
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

  const fetchCurrentRent = async (reset = false) => {
    try {
      const page = reset ? 0 : currentRentPage;
      const response = await axiosInstance.get('/rentals', {
        params: {
          userCondition: "SENDER",
          rentalStatus: "IN_PROGRESS",
          page: page,
          size: 10
        }
      })

      const newItems = response.data.content;

      if (reset) {
        setCurrentRent(newItems);
        setCurrentRentPage(0);
      } else {
        setCurrentRent(prev => {
          const existingIds = new Set(prev.map(item => item.id));
          const uniqueNewItems = newItems.filter(item => !existingIds.has(item.id));
          return [...prev, ...uniqueNewItems];
        });
        setCurrentRentPage(prev => prev + 1);
      }

      setCurrentRentCnt(response.data.totalElements);
    } catch (error) {
      console.log(error);
    }
  }

  const fetchRentalRequests = async (reset = false) => {
    try {
      const page = reset ? 0 : rentalRequestsPage;
      const response = await axiosInstance.get('/rentals', {
        params: {
          userCondition: "SENDER",
          rentalStatus: "OFFERING",
          page: page,
          size: 10
        }
      })

      const newItems = response.data.content;

      if (reset) {
        setRentalRequests(newItems);
        setRentalRequestsPage(0);
      } else {
        setRentalRequests(prev => {
          const existingIds = new Set(prev.map(item => item.id));
          const uniqueNewItems = newItems.filter(item => !existingIds.has(item.id));
          return [...prev, ...uniqueNewItems];
        });
        setRentalRequestsPage(prev => prev + 1);
      }

      setRentalRequestsCnt(response.data.totalElements);
    } catch (error) {
      console.log(error);
    }
  }


  const fetchReceivedRentalRequests = async (reset = false) => {
    try {
      const page = reset ? 0 : receivedRentalRequestsPage;
      const response = await axiosInstance.get('/rentals', {
        params: {
          userCondition: "RECEIVER",
          rentalStatus: "OFFERING",
          page: page,
          size: 10
        }
      })

      const newItems = response.data.content;

      if (reset) {
        setReceivedRentalRequests(newItems)
        setReceivedRentalRequestsPage(0)
      } else {
        setReceivedRentalRequests(prev => {
          const existingIds = new Set(prev.map(item => item.id));
          const uniqueNewItems = newItems.filter(item => !existingIds.has(item.id));
          return [...prev, ...uniqueNewItems];
        });
        setReceivedRentalRequestsPage(prev => prev + 1)
      }
      setReceivedRentalRequestsCnt(response.data.totalElements)
    } catch (error) {
      console.log(error)
    }
  }

  const fetchRejectedRentalRequests = async () => {
    try {
      const response = await axiosInstance.get('/rentals', {
        params: {
          userCondition: "SENDER",
          rentalStatus: "REJECTED",
          page: rejectedRentalRequestsPage,
          size: 10
        }
      })

      const newItems = response.data.content;

      setRejectedRentalRequests(prev => {
        const existingIds = new Set(prev.map(item => item.id));
        const uniqueNewItems = newItems.filter(item => !existingIds.has(item.id));
        return [...prev, ...uniqueNewItems];
      })
      setRejectedRentalRequestsCnt(response.data.totalElements)
      setRejectedRentalRequestsPage(prev => prev + 1)
    } catch (error) {
      console.log(error)
    }
  }

  const loadMoreCurrentRent = () => {
    fetchCurrentRent()
  }

  const loadMoreRentalRequests = () => {
    fetchRentalRequests()
  }

  const loadMoreReceivedRentalRequests = () => {
    fetchReceivedRentalRequests()
  }

  const loadMoreRejectedRentalRequests = () => {
    fetchRejectedRentalRequests()
  }

  const handleExtension = async (rentalId) => {
    Swal.fire({
      title: '연장 신청하시겠습니까?',
      showCancelButton: true,
      confirmButtonText: '신청',
      cancelButtonText: '취소',
      icon: 'question'
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await axiosInstance.post(`/rentals/${rentalId}/extensions`)
          await fetchCurrentRent(true)
          Swal.fire({
            title: '연장 신청 완료',
            text: '연장 신청이 완료되었습니다.',
            confirmButtonText: '확인',
            icon: 'success'
          })
          closeModal()
        } catch (error) {
          console.log(error)
          if (error.response.data === '이미 존재합니다.') {
            Swal.fire({
              title: '연장 신청 오류',
              text: '이미 연장 신청을 보냈습니다.',
              confirmButtonText: '확인',
              icon: 'warning'
            })
            closeModal()
          }
        }
      }
    })
  }

  const handleCancelRental = async (offerId) => {
    Swal.fire({
      title: '대여 신청을 취소하시겠습니까?',
      showCancelButton: true,
      confirmButtonText: '신청취소',
      cancelButtonText: '돌아가기',
      icon: 'question'
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await axiosInstance.delete(`/rentals/offer/${offerId}`)
          await fetchRentalRequests(true)
          Swal.fire({
            title: '대여 신청 취소',
            text: '대여 신청을 취소했습니다.',
            confirmButtonText: '확인',
            icon: 'error'
          })
          closeModal()
        } catch (error) {
          console.log(error)
        }
      }
    })
  }
  const handleAccept = async (offerId, response) => {
    const confirmationOptions = getConfirmationOptions(response);

    Swal.fire(confirmationOptions).then(async (result) => {
      if (result.isConfirmed) {
        await processRentalRequest(offerId, response);
      }
    });
  };

  const getConfirmationOptions = (response) => {
    return response === true
      ? {
        title: '대여 신청을 수락하시겠습니까?',
        showCancelButton: true,
        confirmButtonText: '수락',
        cancelButtonText: '취소',
        icon: 'question',
      }
      : {
        title: '대여 신청을 거절하시겠습니까?',
        showCancelButton: true,
        confirmButtonText: '거절',
        cancelButtonText: '취소',
        icon: 'question',
      };
  };

  const processRentalRequest = async (offerId, response) => {
    try {
      await axiosInstance.put(`/rentals/offer/${offerId}`, {
        isApproved: response,
      });
      await fetchReceivedRentalRequests(true);

      Swal.fire({
        title: response === true ? '대여 신청 수락' : '대여 신청 거절',
        text: response === true ? '대여 신청을 수락했습니다.' : '대여 신청을 거절했습니다.',
        confirmButtonText: '확인',
        icon: response === true ? 'success' : 'error',
      });

      closeModal();
    } catch (error) {
      console.log(error);
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
      case MODAL_TYPES.CURRENT_RENT:
        return (
          <>
            <div className={styles.modalBook}>
              <div className={styles.modalBookImg}>
                <img src={selectedItem.userbook.bookInfo.thumbnail} alt="" />
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
                  {selectedItem.userbook.ownerInfo.nickname}
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
            <div className={styles.buttons}>
              <button className={styles.modalButton} onClick={() => handleExtension(selectedItem.id)}>연장 신청</button>
            </div>
          </>
        );
      case MODAL_TYPES.RENTAL_REQUEST:
        return (
          <>
            <div className={styles.modalBook}>
              <div className={styles.modalBookImg}>
                <img src={selectedItem.userbook.bookInfo.thumbnail} alt="" />
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
                  {selectedItem.userbook.ownerInfo.nickname}
                </div>
                <h2></h2>
                <div className={styles.modalContent}></div>
              </div>
            </div>
            <div className={styles.buttons}>
              <button className={styles.modalButton} onClick={() => handleCancelRental(selectedItem.id)}>신청취소</button>
            </div>
          </>
        );
      case MODAL_TYPES.RECEIVED_REQUEST:
        return (
          <>
            <div className={styles.modalBook}>
              <div className={styles.modalBookImg}>
                <img src={selectedItem.userbook.bookInfo.thumbnail} alt="" />
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
                <h2></h2>
                <div className={styles.modalContent}></div>
                <h2></h2>
                <div className={styles.modalContent}></div>
              </div>
            </div>
            <div className={styles.rentalInfo}>
              <h2>신청자</h2>
              <p>{selectedItem.user.nickname}</p>
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
      //       <h2>{selectedItem.userbook.bookInfo.title}</h2>
      //     </>
      //   );
      default:
        return null;
    }
  };

  const toggleExpansion = (setter) => {
    setter(prev => !prev);
  };

  return (
    <div className={styles.rentalContainer}>
      <div className={`${styles.rentalContainerInner} ${isCurrentRentExpanded ? styles.expanded : ''}`}>
        <h2 onClick={() => toggleExpansion(setIsCurrentRentExpanded)}>
          <FiList /> 대여 중인 목록 (총 {currentRentCnt}개)
        </h2>
        <div className={styles.expandableContent}>
          <div className={styles.listHeader}>
            <div>제목</div>
            <div>책권자</div>
          </div>
          <InfiniteScroll
            dataLength={currentRent.length}
            next={loadMoreCurrentRent}
            hasMore={currentRent.length < currentRentCnt}
            loader={<h4>Loading...</h4>}
            scrollableTarget="currentRentList"
          >
            <ListComponent
              items={currentRent}
              emptyMessage="목록이 없습니다"
              renderItem={(item) => (
                <div className={styles.listItem} onClick={() => openModal(item, MODAL_TYPES.CURRENT_RENT)} style={{ cursor: 'pointer' }}>
                  <div>{item.userbook.bookInfo.title}</div>
                  <div>{item.userbook.ownerInfo.nickname}</div>
                </div>
              )}
            />
          </InfiniteScroll>
        </div>
      </div>


      <div className={`${styles.rentalContainerInner} ${isRentalRequestsExpanded ? styles.expanded : ''}`}>
        <h2 onClick={() => toggleExpansion(setIsRentalRequestsExpanded)}>
          <FiList /> 대여 신청한 목록 (총 {rentalRequestsCnt}개)
        </h2>
        <div className={styles.expandableContent}>
          <div className={styles.listHeader}>
            <div>제목</div>
            <div>책권자</div>
          </div>
          <InfiniteScroll
            dataLength={rentalRequests.length}
            next={loadMoreRentalRequests}
            hasMore={rentalRequests.length < rentalRequestsCnt}
            loader={<h4>Loading...</h4>}
            scrollableTarget="rentalRequestsList"
          >
            <ListComponent
              items={rentalRequests}
              emptyMessage="목록이 없습니다"
              renderItem={(item) => (
                <div className={styles.listItem} onClick={() => openModal(item, MODAL_TYPES.RENTAL_REQUEST)} style={{ cursor: 'pointer' }}>
                  <div>{item.userbook.bookInfo.title}</div>
                  <div>{item.userbook.ownerInfo.nickname}</div>
                </div>
              )}
            />
          </InfiniteScroll>
        </div>
      </div>

      <div className={`${styles.rentalContainerInner} ${isReceivedRequestsExpanded ? styles.expanded : ''}`}>
        <h2 onClick={() => toggleExpansion(setIsReceivedRequestsExpanded)}>
          <FiList /> 대여 신청 받은 목록 (총 {receivedRentalRequestsCnt}개)
        </h2>
        <div className={styles.expandableContent}>
          <div className={styles.listHeader}>
            <div>제목</div>
            <div>신청자</div>
          </div>
          <InfiniteScroll
            dataLength={receivedRentalRequests.length}
            next={loadMoreReceivedRentalRequests}
            hasMore={receivedRentalRequests.length < receivedRentalRequestsCnt}
            loader={<h4>Loading...</h4>}
            scrollableTarget="receivedRentalRequestsList"
          >
            <ListComponent
              items={receivedRentalRequests}
              emptyMessage="목록이 없습니다"
              renderItem={(item) => (
                <div className={styles.listItem} onClick={() => openModal(item, MODAL_TYPES.RECEIVED_REQUEST)} style={{ cursor: 'pointer' }}>
                  <div>{item.userbook.bookInfo.title}</div>
                  <div>{item.user.nickname}</div>
                </div>
              )}
            />
          </InfiniteScroll>
        </div>
      </div>

      <div className={`${styles.rentalContainerInner} ${isRejectedRequestsExpanded ? styles.expanded : ''}`}>
        <h2 onClick={() => toggleExpansion(setIsRejectedRequestsExpanded)}>
          <FiList /> 대여 신청 거절당한 목록 (총 {rejectedRentalRequestsCnt}개)
        </h2>
        <div className={styles.expandableContent}>
          <div className={styles.listHeader}>
            <div>제목</div>
          </div>
          <InfiniteScroll
            dataLength={rejectedRentalRequests.length}
            next={loadMoreRejectedRentalRequests}
            hasMore={rejectedRentalRequests.length < rejectedRentalRequestsCnt}
            loader={<h4>Loading...</h4>}
            scrollableTarget="rejectedRentalRequestsList"
          >
            <ListComponent
              items={rejectedRentalRequests}
              emptyMessage="목록이 없습니다"
              renderItem={(item) => (
                <div className={styles.listItem}>
                  <div>{item.userbook.bookInfo.title}</div>
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

export default Rental
