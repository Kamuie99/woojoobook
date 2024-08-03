import React, { useState, useEffect } from 'react'
import Swal from 'sweetalert2'
import InfiniteScroll from 'react-infinite-scroll-component'
import axiosInstance from '../../util/axiosConfig'
import ListComponent from './ListComponent'
import styles from './Extension.module.css'

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
            page: extensionRequestsPage,
            size: 10
          }
        }),
        // ERROR : 내가 받은 연장 목록 요청 시 401 에러
        // 서버 확인 필요!
        axiosInstance.get('/extensions', {
          params: {
            userCondition: "RECEIVER",
            extensionStatus: "OFFERING",
            page: receivedExtensionRequestsPage,
            size: 10
          }
        }),
        axiosInstance.get('/extensions', {
          params: {
            userCondition: "SENDER",
            extensionStatus: "REJECTED",
            page: rejectedExtensionRequestsPage,
            size: 10
          }
        })
      ])
      setExtensionRequests(prev => [...prev, ...response1.data.content])
      setExtensionRequestsCnt(response1.data.totalElements)
      
      setReceivedExtensionRequests(prev => [...prev, ...response2.data.content])
      setReceivedExtensionRequestsCnt(response2.data.totalElements)
      
      setRejectedExtensionRequests(prev => [...prev, ...response3.data.content])
      setRejectedExtensionRequestsCnt(response3.data.totalElements)
    } catch (error) {
      console.log(error)
    }
  }

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
          const uniqueNewItems = newItems.filter(item > !existingIds.has(item.id));
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
    fetchExchangeRequests()
  }

  const loadMoreReceivedExtensionRequests = () => {
    fetchReceivedExchangeRequests()
  }

  const loadMoreRejectedExtensionRequests = () => {
    fetchRejectedExtensionRequests()
  }

  const handleCancleExtension = async (offerId) => {
    Swal.fire({
      title: "연장 신청을 취소하시겠습니까?",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "신청취소하기",
      cancelButtonText: "돌아가기"
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await axiosInstance.delete(`/extensions/${offerId}`)
          await fetchExtensionRequests(true)
          Swal.fire({
            title: "연장 신청을 취소했습니다",
            icon: "success"
          })
        } catch (error) {
          console.log(error)
        }
      }
    })
  }

  const handleAccept = async (offerId, response) => {
    try {
      await axiosInstance.put(`/extensions/${offerId}`, {
        isApproved: response
      })
      await fetchReceivedExtensionRequests(true)
      if (response === true) {
        Swal.fire({
          title: "연장신청을 수락했습니다.",
          icon: "success"
        })
      } else {
        Swal.fire({
          title: "연장신청을 거절했습니다.",
          icon: "warning"
        })
      }
    } catch (error) {
      console.log(error)
    }
  }

  return (
    <div className={styles.extensionContainer}>
      <h2>연장 신청한 목록 (총 {extensionRequestsCnt}개)</h2>
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
            <>
              <div>{item.userbook.bookInfo.title}</div>
              <div>{item.userbook.ownerInfo.nickname}</div>
              <div>{item.startDate}</div>
              <div><button onClick={() => handleCancleExtension(item.id)}>신청취소</button></div>
            </>
          )}
        />
      </InfiniteScroll>

      <h2>연장 신청 받은 목록 (총 {receivedExtensionRequestsCnt}개)</h2>
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
            <>
              <div>{item.userbook.bookInfo.title}</div>
              <div>{item.user.nickname}</div>
              <div>{item.startDate}</div>
              <div><button onClick={() => handleAccept(item.id, true)}>수락</button></div>
              <div><button onClick={() => handleAccept(item.id, false)}>거절</button></div>
            </>
          )}

        />
      </InfiniteScroll>
      
      <h2>연장 신청 거절 당한 목록 (총 {rejectedExtensionRequestsCnt}개)</h2>
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
            <>
              <div>{item.userbook.bookInfo.title}</div>
            </>
          )}
        />
      </InfiniteScroll>
    </div>
  )
}

export default Extension