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

  const fetchLikedUserbooks = async (init = false) => {
    try {
      const response = await axiosInstance.get(`/users/userbooks/likes`, {
        params: {
          page: init ? 0 : page,
          size: 10
        }
      });
      const newItems = response.data.content;
      if (init) {
        setLikedUserbooks(newItems);
        setPage(1);
      } else {
        setLikedUserbooks(prev => [...prev,...newItems]);
        setPage(prev => prev + 1);
      };
      setLikedUserbooksCount(response.data.totalElements);
    } catch (error) {
      console.error(error);
    }
  }

  const loadMoreLikedUserbooks = () => {
    fetchLikedUserbooks();
  }

  const handleWishChange = (bookId) => {
    setLikedUserbooks(prevBooks => prevBooks.filter(book => book.id !== bookId));
    setLikedUserbooksCount(prevCount => prevCount - 1);
  }

  return (
    <>
    <h2 className={styles.liked}><strong>내가 관심 등록한 책 | </strong> {likedUserbooksCount}</h2>
    <div
      className={styles.likedUserbooksList}
      id="likedUserbooksList"
      // style={{ height: 'calc(100vh - 300px)', overflow: 'auto' }}
    >
      <InfiniteScroll
        dataLength={likedUserbooks.length}
        next={loadMoreLikedUserbooks}
        hasMore={likedUserbooks.length < likedUserbooksCount}
        loader={<h4>Loading...</h4>}
        scrollableTarget="likedUserbooksList"
      >
        <ListComponent
          items={likedUserbooks}
          emptyMessage="목록이 없습니다"
          renderItem={(item) => (
            <>
              <BookInfo
                item={item}
                onWishChange={handleWishChange}
              />
            </>
          )}
        />
      </InfiniteScroll>
    </div>
    </>
  )
}

export default Liked;