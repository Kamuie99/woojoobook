import { useState, useEffect } from 'react'
import Swal from 'sweetalert2'
import InfiniteScroll from 'react-infinite-scroll-component'
import axiosInstance from '../../util/axiosConfig'
import ListComponent from './ListComponent'
import Modal from '../BookRegister/Modal'
import styles from './Extension.module.css'
import { SiGitextensions } from "react-icons/si";

const MODAL_TYPES = {
  EXTENSION_REQUEST: 'EXTENSION_REQUEST',
  RECEIVED_REQUEST: 'RECEIVED_REQUEST',
  REJECTED_REQUEST: 'REJECTED_REQUEST'
};

const Extension = () => {
  const [extensionRequests, setExtensionRequests] = useState([]);
  const [extensionRequestsCnt, setExtensionRequestsCnt] = useState(0);
  const [receivedExtensionRequests, setReceivedExtensionRequests] = useState([]);
  const [receivedExtensionRequestsCnt, setReceivedExtensionRequestsCnt] = useState(0);
  const [rejectedExtensionRequests, setRejectedExtensionRequests] = useState([]);
  const [rejectedExtensionRequestsCnt, setRejectedExtensionRequestsCnt] = useState(0);
  const [extensionRequestsPage, setExtensionRequestsPage] = useState(0);
  const [receivedExtensionRequestsPage, setReceivedExtensionRequestsPage] = useState(0);
  const [rejectedExtensionRequestsPage, setRejectedExtensionRequestsPage] = useState(0);
  const [selectedItem, setSelectedItem] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalType, setModalType] = useState(null);
  const [isExtensionRequestExpanded, setIsExtensionRequestExpanded] = useState(false);
  const [isReceivedRequestExpanded, setIsReceivedRequestExpanded] = useState(false);
  const [isRejectedRequestExpanded, setIsRejectedRequestExpanded] = useState(false);

  useEffect(() => {
    fetchExtensionList()
  }, [])

  const fetchExtensionList = async () => {
    try {
      const [response1, response2, response3] = await Promise.all([
        axiosInstance.get('/extensions', {
          params: {
            userCondition: "SENDER",
            extensionStatus: "OFFERING",
            page: 0,
            size: 10
          }
        }),
        axiosInstance.get('/extensions', {
          params: {
            userCondition: "RECEIVER",
            extensionStatus: "OFFERING",
            page: 0,
            size: 10
          }
        }),
        axiosInstance.get('/extensions', {
          params: {
            userCondition: "SENDER",
            extensionStatus: "REJECTED",
            page: 0,
            size: 10
          }
        })
      ])
      updateStateWithoutDuplicates(setExtensionRequests, response1.data.content, setExtensionRequestsCnt, setExtensionRequestsPage, response1.data.totalElements)
      updateStateWithoutDuplicates(setReceivedExtensionRequests, response2.data.content, setReceivedExtensionRequestsCnt, setReceivedExtensionRequestsPage, response2.data.totalElements)
      updateStateWithoutDuplicates(setRejectedExtensionRequests, response3.data.content, setRejectedExtensionRequestsCnt, setRejectedExtensionRequestsPage, response3.data.totalElements)
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

  const fetchExtensionRequests = async (reset = false) => {
    try {
      const page = reset ? 0 : extensionRequestsPage
      const response = await axiosInstance.get('/extensions', {
        params: {
          userCondition: "SENDER",
          extensionStatus: "OFFERING",
          page: page,
          size: 10
        }
      })

      const newItems = response.data.content;

      if (reset) {
        setExtensionRequests(newItems)
        setExtensionRequestsPage(0)
      } else {
        setExtensionRequests(prev => {
          const existingIds = new Set(prev.map(item => item.id));
          const uniqueNewItems = newItems.filter(item => !existingIds.has(item.id));
          return [...prev, ...uniqueNewItems];
        })
        setExtensionRequestsPage(prev => prev + 1)
      }
      setExtensionRequestsCnt(response.data.totalElements)
    } catch (error) {
      console.log(error)
    }
  }

  const fetchReceivedExtensionRequests = async (reset = false) => {
    try {
      const page = reset ? 0 : receivedExtensionRequestsPage;
      const response = await axiosInstance.get('/extensions', {
        params: {
          userCondition: "RECEIVER",
          extensionStatus: "OFFERING",
          page: page,
          size: 10
        }
      })

      const newItems = response.data.content;

      if (reset) {
        setReceivedExtensionRequests(newItems)
        setReceivedExtensionRequestsPage(0)
      } else {
        setReceivedExtensionRequests(prev => {
          const existingIds = new Set(prev.map(item => item.id));
          const uniqueNewItems = newItems.filter(item => !existingIds.has(item.id));
          return [...prev, ...uniqueNewItems];
        })
        setReceivedExtensionRequestsPage(prev => prev + 1)
      }
      setReceivedExtensionRequestsCnt(response.data.totalElements)
    } catch (error) {
      console.log(error)
    }
  }

  const fetchRejectedExtensionRequests = async () => {
    try {
      const response = await axiosInstance.get('/extensions', {
        params: {
          userCondition: "SENDER",
          extensionStatus: "REJECTED",
          page: rejectedExtensionRequestsPage,
          size: 10
        }
      })

      const newItems = response.data.content;

      setRejectedExtensionRequests(prev => {
        const existingIds = new Set(prev.map(item => item.id));
        const uniqueNewItems = newItems.filter(item => !existingIds.has(item.id));
        return [...prev, ...uniqueNewItems];
      })
      setRejectedExtensionRequestsCnt(response.data.totalElements)
      setRejectedExtensionRequestsPage(prev => prev + 1)
    } catch (error) {
      console.log(error)
    }
  }

  const loadMoreExtensionRequests = () => {
    fetchExtensionRequests()
  }

  const loadMoreReceivedExtensionRequests = () => {
    fetchReceivedExtensionRequests()
  }

  const loadMoreRejectedExtensionRequests = () => {
    fetchRejectedExtensionRequests()
  }

  const handleCancelExtension = async (offerId) => {
    Swal.fire({
      title: '연장 신청을 취소하시겠습니까?',
      showCancelButton: true,
      confirmButtonText: '신청취소',
      cancelButtonText: '돌아가기',
      icon: 'question'
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await axiosInstance.delete(`/extensions/${offerId}`)
          await fetchExtensionRequests(true)
          Swal.fire({
            title: '연장 신청 취소',
            text: '연장 신청을 취소했습니다.',
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
    if (response === true) {
      Swal.fire({
        title: '연장 신청을 수락하시겠습니까?',
        showCancelButton: true,
        confirmButtonText: '수락',
        cancelButtonText: '취소',
        icon: 'question'
      }).then(async (result) => {
        if (result.isConfirmed) {
          try {
            await axiosInstance.put(`/extensions/${offerId}`, {
              isApproved: response
            })
            await fetchReceivedExtensionRequests(true)
            Swal.fire({
              title: '연장 신청 수락',
              text: '연장 신청을 수락했습니다.',
              confirmButtonText: '확인',
              icon: 'success'
            })
            closeModal()
          } catch (error) {
            console.log(error)
          }
        }
      })
    } else {
      Swal.fire({
        title: '연장 신청을 거절하시겠습니까?',
        showCancelButton: true,
        confirmButtonText: '거절',
        cancelButtonText: '취소',
        icon: 'question'
      }).then(async (result) => {
        if (result.isConfirmed) {
          try {
            await axiosInstance.put(`/extensions/${offerId}`, {
              isApproved: response
            })
            await fetchReceivedExtensionRequests(true)
            Swal.fire({
              title: '연장 신청 거절',
              text: '연장 신청을 거절했습니다.',
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
      case MODAL_TYPES.EXTENSION_REQUEST:
        return (
          <>
            <div className={styles.modalBook}>
              <div className={styles.modalBookImg}>
                <img src={selectedItem.rentalResponse.userbook.bookInfo.thumbnail} alt=""/>
              </div>
              <div className={styles.modalBookInfo}>
                <h2>제목</h2>
                <div className={styles.modalContent}>
                  {selectedItem.rentalResponse.userbook.bookInfo.title}
                </div>
                <h2>저자</h2>
                <div className={styles.modalContent}>
                  {selectedItem.rentalResponse.userbook.bookInfo.author}
                </div>
                <h2>책권자</h2>
                <div className={styles.modalContent}>
                  {selectedItem.rentalResponse.userbook.ownerInfo.nickname}
                </div>
              </div>
            </div>
            <div className={styles.rentalInfo}>
              <h2>반납 예정일</h2>
              <p>{selectedItem.rentalResponse.endDate.split('T')[0]}</p>
            </div>
            <div className={styles.buttons}>
              <button className={styles.modalButton} onClick={() => handleCancelExtension(selectedItem.id)}>신청취소</button>
            </div>
          </>
        );
      case MODAL_TYPES.RECEIVED_REQUEST:
        return (
          <>
            <div className={styles.modalBook}>
              <div className={styles.modalBookImg}>
                <img src={selectedItem.rentalResponse.userbook.bookInfo.thumbnail} alt=""/>
              </div>
              <div className={styles.modalBookInfo}>
                <h2>제목</h2>
                <div className={styles.modalContent}>
                  {selectedItem.rentalResponse.userbook.bookInfo.title}
                </div>
                <h2>저자</h2>
                <div className={styles.modalContent}>
                  {selectedItem.rentalResponse.userbook.bookInfo.author}
                </div>
              </div>
            </div>
            <div className={styles.rentalInfo}>
              <h2>신청자</h2>
              <p>{selectedItem.rentalResponse.user.nickname}</p>
              <h2>반납 예정일</h2>
              <p>{selectedItem.rentalResponse.endDate.split('T')[0]}</p>
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
      //       <h2>{selectedItem.rentalResponse.userbook.bookInfo.title}</h2>
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
    <div className={styles.extensionContainer}>
      <div className={`${styles.extensionContainerInner} ${isExtensionRequestExpanded ? styles.expanded : ''}`}>
        <h2 onClick={() => toggleExpansion(setIsExtensionRequestExpanded)}>
          <SiGitextensions size={'17px'}/> 연장 신청한 목록 (총 {extensionRequestsCnt}개)
        </h2>
        <div className={styles.expandableContent}>
          <div className={styles.listHeader}>
            <div>제목</div>
            <div>책권자</div>
          </div>
          <InfiniteScroll
            dataLength={extensionRequests.length}
            next={loadMoreExtensionRequests}
            hasMore={extensionRequests.length < extensionRequestsCnt}
            loader={<h4>Loading...</h4>}
            scrollableTarget="extensionRequestsList"
          >
            <ListComponent
              items={extensionRequests}
              emptyMessage="목록이 없습니다"
              renderItem={(item) => (
                <div className={styles.listItem} onClick={() => openModal(item, MODAL_TYPES.EXTENSION_REQUEST)} style={{cursor: 'pointer'}}>
                  <div>{item.rentalResponse.userbook.bookInfo.title}</div>
                  <div>{item.rentalResponse.userbook.ownerInfo.nickname}</div>
                </div>
              )}
            />
          </InfiniteScroll>
        </div>
      </div>
          
      <div className={`${styles.extensionContainerInner} ${isReceivedRequestExpanded ? styles.expanded : ''}`}>
        <h2 onClick={() => toggleExpansion(setIsReceivedRequestExpanded)}>
          <SiGitextensions size={'17px'}/> 연장 신청 받은 목록 (총 {receivedExtensionRequestsCnt}개)
        </h2>
        <div className={styles.expandableContent}>
          <div className={styles.listHeader}>
            <div>제목</div>
            <div>신청자</div>
          </div>
          <InfiniteScroll
            dataLength={receivedExtensionRequests.length}
            next={loadMoreReceivedExtensionRequests}
            hasMore={receivedExtensionRequests.length < receivedExtensionRequestsCnt}
            loader={<h4>Loading...</h4>}
            scrollableTarget="receivedExtensionRequestsList"
          >
            <ListComponent
              items={receivedExtensionRequests}
              emptyMessage="목록이 없습니다"
              renderItem={(item) => (
                <div className={styles.listItem} onClick={() => openModal(item, MODAL_TYPES.RECEIVED_REQUEST)} style={{cursor: 'pointer'}}>
                  <div>{item.rentalResponse.userbook.bookInfo.title}</div>
                  <div>{item.rentalResponse.user.nickname}</div>
                </div>
              )}
            />
          </InfiniteScroll>
        </div>
      </div>
      
      <div className={`${styles.extensionContainerInner} ${isRejectedRequestExpanded ? styles.expanded : ''}`}>
        <h2 onClick={() => toggleExpansion(setIsRejectedRequestExpanded)}>
          <SiGitextensions size={'17px'}/> 연장 신청 거절 당한 목록 (총 {rejectedExtensionRequestsCnt}개)
        </h2>
        <div className={styles.expandableContent}>
          <div className={styles.listHeader}>
            <div>제목</div>
          </div>
          <InfiniteScroll
            dataLength={rejectedExtensionRequests.length}
            next={loadMoreRejectedExtensionRequests}
            hasMore={rejectedExtensionRequests.length < rejectedExtensionRequestsCnt}
            loader={<h4>Loading...</h4>}
            scrollableTarget="rejectedExtensionRequestsList"
          >
            <ListComponent
              items={rejectedExtensionRequests}
              emptyMessage="목록이 없습니다"
              renderItem={(item) => (
                <div className={styles.listItem}>
                  <div>{item.rentalResponse.userbook.bookInfo.title}</div>
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

export default Extension