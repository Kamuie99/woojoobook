import React, {useState, useEffect } from 'react';
import axiosInstance from '../../util/axiosConfig'
import InfiniteScroll from 'react-infinite-scroll-component'
import styles from './Liked.module.css';
import BookInfo from './BookInfo';
import ListComponent from './ListComponent'

const Liked = () => {
  const [likedUserbooks, setLikedUserbooks] = useState([]);
  const [likedUserbooksCount, setLikedUserbooksCount] = useState(0);
  const [page, setPage] = useState(0);

  useEffect(() => {
    fetchLikedUserbooks(true);
  }, []);

  useEffect(() => {
    console.log(likedUserbooks);
  }, [likedUserbooks]);

  const fetchLikedUserbooks = async (init = false) => {
    try {
      const response = await axiosInstance.get(`/users/userbooks/likes`, {
        params: {
          page,
          size: 10
        }
      });
      const newItems = response.data.content;
      if (init) {
        setLikedUserbooks(newItems);
        setPage(0);
      } else {
        setLikedUserbooks(prev => [...prev,...newItems]);
        setPage(prev => prev + 1);
      };
      setLikedUserbooksCount(response.data.numberOfElements);
    } catch (error) {
      console.error(error);
    }
  }

  const loadMoreLikedUserbooks = () => {
    fetchLikedUserbooks();
  }

  return (
    <div className={styles.likedContainer}>
      <h2 className={styles.liked}><strong>내가 관심 등록한 책 | </strong> {likedUserbooksCount}</h2>
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
          renderItem={(item) => (
            <>
              <BookInfo
                item={item}
              />
            </>
          )}
        />
      </InfiniteScroll>
    </div>
  )
}

export default Liked;