import React, { useEffect, useRef, useState } from 'react'
import BookModal from '../MyLibrary/BookModal'
import styles from './ListComponent.module.css'
import axiosInstance from '../../util/axiosConfig';

const ListComponent = ({ items, renderItem, emptyMessage }) => {
  const [showModal, setShowModal] = useState(false);
  const [bookDetail, setBookDetail] = useState('');
  const ref = useRef(null);

  const openBookModal = async (book) => {
    try {
      const response = await axiosInstance.get(`books?keyword=${book.isbn}&page=1`);
      setBookDetail(response.data.bookList[0]);
      setShowModal(true);
    } catch (error) {
      console.error('Error fetching book details:', error);
    }
  };

  useEffect(() => {
    console.log(bookDetail)
    console.log(showModal);
  }, [bookDetail, showModal]);

  return (
    <>
      <div className={styles.listContainer}>
        {items.length > 0 ? (
          items.map((item, index) => (
            <div
              key={index}
              className={styles.listItem}
              onClick={() => openBookModal(item.bookInfo)}
            >
              <div className={styles.listItemContent}>
                {renderItem(item)}
              </div>
            </div>
          ))
        ) : (
          <div className={styles.emptyList}>{emptyMessage}</div>
        )}
      </div>
      {showModal && bookDetail && (
        <BookModal
          book={bookDetail}
          onClose={() => setShowModal(false)}
        />
      )}
    </>
  )
}

export default ListComponent;