import React, { useState, useEffect } from 'react'
import Swal from 'sweetalert2'
import InfiniteScroll from 'react-infinite-scroll-component'
import axiosInstance from '../../util/axiosConfig'
import ListComponent from './ListComponent'
import styles from './Rental.module.css'

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

  useEffect(() => {
    fetchRentalList()
  }, [])

  const fetchRentalList = async () => {
    try {
      const [response1, response2, response3, response4] = await Promise.all([
        axiosInstance.get('/rentals', {
          params: {
            userCondition: "SENDER",
            rentalStatus: "IN_PROGRESS",
            page: currentRentPage,
            size: 10
          }
        }),
        axiosInstance.get('/rentals', {
          params: {
            userCondition: "SENDER",
            rentalStatus: "OFFERING",
            page: rentalRequestsPage,
            size: 10
          }
        }),
        axiosInstance.get('/rentals', {
          params: {
            userCondition: "RECEIVER",
            rentalStatus: "OFFERING",
            page: receivedRentalRequestsPage,
            size: 10
          }
        }),
        axiosInstance.get('/rentals', {
          params: {
            userCondition: "SENDER",
            rentalStatus: "REJECTED",
            page: rejectedRentalRequestsPage,
            size: 10
          }
        })
      ])
      setCurrentRent(prev => [...prev, ...response1.data.content])
      setCurrentRentCnt(response1.data.totalElements)
  
      setRentalRequests(prev => [...prev, ...response2.data.content])
      setRentalRequestsCnt(response2.data.totalElements)
  
      setReceivedRentalRequests(prev => [...prev, ...response3.data.content])
      setReceivedRentalRequestsCnt(response3.data.totalElements)
      
      setRejectedRentalRequests(prev => [...prev, ...response4.data.content])
      setRejectedRentalRequestsCnt(response4.data.totalElements)
    } catch (error) {
      console.log(error)
    }
  }

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

  const handleReturn = async (rentalId) => {
    Swal.fire({
      title: "반납하시겠습니까?",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "반납하기",
      cancelButtonText: "취소하기"
    }).then(async (result) => {
      // ERROR : (현) 책권자가 반납 확인하는 요청임
      // 수정필요!!
      if (result.isConfirmed) {
        try {
          await axiosInstance.put(`/rentals/${rentalId}/return`)
          await fetchCurrentRent(true)
          Swal.fire({
            title: "반납이 완료되었습니다",
            text: "이용해주셔서 감사합니다",
            icon: "success"
          })
        } catch (error) {
          console.log(error)
        }
      }
    })
  }

  const handleExtension = async (rentalId) => {
    Swal.fire({
      title: "연장 신청하시겠습니까?",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "신청하기",
      cancelButtonText: "취소하기"
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await axiosInstance.post(`/rentals/${rentalId}/extensions`)
          await fetchCurrentRent(true)
          Swal.fire({
            title: "연장신청이 완료되었습니다",
            icon: "success"
          })
        } catch (error) {
          console.log(error)
        }
      }
    })
  }

  const handleCancleRental = async (offerId) => {
    Swal.fire({
      title: "대여 신청을 취소하시겠습니까?",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "신청취소하기",
      cancelButtonText: "돌아가기"
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await axiosInstance.delete(`/rentals/offer/${offerId}`)
          await fetchRentalRequests(true)
          Swal.fire({
            title: "대여 신청을 취소했습니다",
            icon: "success"
          })
        } catch (error) {
          console.log(error)
        }
      }
    })
  }

  const handleAccept = async (offerId, response) => {
    if (response === true) {
      Swal.fire({
        title: "대여 신청을 수락하시겠습니까?",
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "수락하기",
        cancelButtonText: "돌아가기"
      }).then(async (result) => {
        if (result.isConfirmed) {
          try {
            await axiosInstance.put(`/rentals/offer/${offerId}`, {
              isApproved: response
            })
            await fetchReceivedRentalRequests(true)
            Swal.fire({
              title: "대여 신청을 수락했습니다",
              icon: "success"
            })
          } catch (error) {
            console.log(error)
          }      
        }
      })
    } else {
      Swal.fire({
        title: "대여 신청을 거절하시겠습니까?",
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "거절하기",
        cancelButtonText: "돌아가기"
      }).then(async (result) => {
        if (result.isConfirmed) {
          try {
            await axiosInstance.put(`/rentals/offer/${offerId}`, {
              isApproved: response
            })
            await fetchReceivedRentalRequests(true)
            Swal.fire({
              title: "대여 신청을 거절했습니다",
              icon: "success"
            })
          } catch (error) {
            console.log(error)
          }      
        }
      })
    }
  }

  return (
    <div className={styles.rentalContainer}>
      <h2>대여 중인 목록 (총 {currentRentCnt}개)</h2>
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
            <>
              <div>{item.userbook.bookInfo.title}</div>
              <div>{item.userbook.ownerInfo.nickname}</div>
              <div>{item.endDate}</div>
              <div><button onClick={() => handleReturn(item.id)}>반납</button></div>
              <div><button onClick={() => handleExtension(item.id)}>연장</button></div>
            </>
          )}
        />
      </InfiniteScroll>

      <h2>대여 신청한 목록 (총 {rentalRequestsCnt}개)</h2>
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
            <>
              <div>{item.userbook.bookInfo.title}</div>
              <div>{item.userbook.ownerInfo.nickname}</div>
              <div>{item.startDate}</div>
              <div><button onClick={() => handleCancleRental(item.id)}>신청취소</button></div>
            </>
          )}
        />
      </InfiniteScroll>

      <h2>대여 신청 받은 목록 (총 {receivedRentalRequestsCnt}개)</h2>
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
            <>
              <div>{item.userbook.bookInfo.title}</div>
              <div>{item.startDate}</div>
              <div>{item.user.nickname}</div>
              <div><button onClick={() => handleAccept(item.id, true)}>수락</button></div>
              <div><button onClick={() => handleAccept(item.id, false)}>거절</button></div>
            </>
          )}
        />
      </InfiniteScroll>

      <h2>대여 신청 거절당한 목록 (총 {rejectedRentalRequestsCnt}개)</h2>
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
            <>
              <div>{item.userbook.bookInfo.title}</div>
            </>
          )}
        />
      </InfiniteScroll>
    </div>
  )
}

export default Rental
