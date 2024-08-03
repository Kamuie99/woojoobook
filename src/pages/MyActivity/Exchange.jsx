import React, { useState, useEffect } from 'react'
import Swal from 'sweetalert2'
import InfiniteScroll from 'react-infinite-scroll-component'
import axiosInstance from '../../util/axiosConfig'
import ListComponent from './ListComponent'
import styles from './Exchange.module.css'

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
            page: exchangeRequestsPage,
            size: 10
          }
        }),
        axiosInstance.get('/exchanges', {
          params: {
            userCondition: "RECEIVER",
            exchangeStatus: "IN_PROGRESS",
            page: receivedExchangeRequestsPage,
            size: 10
          }
        }),
        axiosInstance.get('/exchanges', {
          params: {
            userCondition: "SENDER",
            exchangeStatus: "REJECTED",
            page: rejectedExchangeRequestsPage,
            size: 10
          }
        })
      ])
      setExchangeRequests(prev => [...prev, ...response1.data.content])
      setExchangeRequestsCnt(response1.data.totalElements)

      setReceivedExchangeRequests(prev => [...prev, ...response2.data.content])
      setReceivedExchangeRequestsCnt(response2.data.totalElements)
      
      setRejectedExchangeRequests(prev => [...prev, ...response3.data.content])
      setRejectedExchangeRequestsCnt(response3.data.totalElements)
    } catch (error) {
      console.log(error)
    }
  }

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
      console.log(error)
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
      console.log(error)
    }
  }

  const fetchRejectedRentalRequests = async () => {
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
        const uniqueNewItems = existingIds.filter(item => !existingIds.has(item.id));
        return [...prev, ...uniqueNewItems];

      })
      setRejectedExchangeRequestsCnt(response.data.totalElements)
      setRejectedExchangeRequestsPage(prev => prev + 1)
    } catch (error) {
      console.log(error)
    }
  }

  const loadMoreExchangeRequests = () => {
    fetchExchangeRequests()
  }

  const loadMoreReceivedExchangeRequests = () => {
    fetchReceivedExchangeRequests()
  }

  const loadMoreRejectedExchangeRequests = () => {
    fetchRejectedRentalRequests()
  }

  const handleCancleExchange = async (offerId) => {
    Swal.fire({
      title: "교환 신청을 취소하시겠습니까?",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "신청취소하기",
      cancelButtonText: "돌아가기"
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await axiosInstance.delete(`/exchanges/offer/${offerId}`)
          await fetchExchangeRequests(true)
          Swal.fire({
            title: "교환 신청을 취소했습니다",
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
      await axiosInstance.post(`/exchanges/offer/${offerId}`, {
        status: response
      })
      await fetchReceivedExchangeRequests(true)
      if (response === true) {
        Swal.fire({
          title: "교환신청을 수락했습니다.",
          icon: "success"
        })
      } else {
        Swal.fire({
          title: "교환신청을 거절했습니다.",
          icon: "warning"
        })
      }
    } catch (error) {
      console.log(error)
    }
  }

  return (
    <div className={styles.exchangeContainer}>
      <h2>교환 신청한 목록 (총 {exchangeRequestsCnt}개)</h2>
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
            <>
              <div>{item.senderBook.bookInfo.title}</div>
              <div>{item.receiverBook.bookInfo.title}</div>
              <div>{item.receiverBook.ownerInfo.nickname}</div>
              <div><button onClick={() => handleCancleExchange(item.id)}>신청취소</button></div>
            </>
          )}
        />
      </InfiniteScroll>

      <h2>교환 신청 받은 목록 (총 {receivedExchangeRequestsCnt}개)</h2>
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
            <>
              <div>{item.receiverBook.bookInfo.title}</div>
              <div>{item.senderBook.bookInfo.title}</div>
              <div>{item.senderBook.ownerInfo.nickname}</div>
              <div><button onClick={() => handleAccept(item.id, true)}>수락</button></div>
              <div><button onClick={() => handleAccept(item.id, false)}>거절</button></div>
            </>
          )}
        />
      </InfiniteScroll>
      
      <h2>교환 신청 거절 당한 내역</h2>
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
            <>
              <div>{item.senderBook.bookInfo.title}</div>
              <div>{item.receiverBook.bookInfo.title}</div>
            </>
          )}
        />
      </InfiniteScroll>
    </div>
  )
}

export default Exchange
