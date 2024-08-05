import React, {useState, useEffect } from 'react';
import axiosInstance from '../../util/axiosConfig'
import InfiniteScroll from 'react-infinite-scroll-component'
import styles from './Liked.module.css';
import ListComponent from './ListComponent'

const Liked = () => {
  const [likedUserbooks, setLikedUserbooks] = useState([]);
  const [likedUserbooksCount, setLikedUserbooksCount] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchInitialUserbooks = async () => {
      try {
        const { data } = await axiosInstance.get(`/users/userbooks/likes`, {
          params: {
            page,
            size: 10
          }
        });

        setLikedUserbooks(prev => [...prev, ...data.content]);
        setLikedUserbooksCount(data.totalElements);
        setPage(prev => prev + 1);
        setLoading(false)
      } catch (error) {
        console.error(error);
      }
      fetchInitialUserbooks();
    }
  }, [])

  const fetchLikedUserbooks = async (reset = false) => {
    try {
      const response = await axiosInstance.get(`/users/userbooks/likes`, {
        params: {
          page,
          size: 10
        }
      });

      const newItems = response.data.content;

      if (reset) {
        setLikedUserbooks(newItems);
        setPage(0);
      } else {
        setLikedUserbooks(prev => {
          return [...prev, ...newItems];
        });
      };

      setLikedUserbooksCount(response.data.totalElements);
      setPage(prev => prev + 1);
    } catch (error) {
      console.error(error);
    }
  }

  const loadMoreLikedUserbooks = () => {
    fetchLikedUserbooks();
  }

  return (
    <div className={styles.liked_container}>
      Liked
      <InfiniteScroll
        dataLength={likedUserbooks.length}
        next={loadMoreLikedUserbooks}
        hasMore={likedUserbooks.length < likedUserbooksCount}
        loader={<h4>Loading...</h4>}
        scrollableTarget="LikedUserbooksList"
      >
        <ListComponent
          items={likedUserbooks}
          emptyMessage="목록이 없습니다"
          renderItem={(book) => {
            <>
              <div>{book}</div>
            </>
          }}
        />
      </InfiniteScroll>
    </div>
  )
}

export default Liked;