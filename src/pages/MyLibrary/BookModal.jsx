/* eslint-disable react/prop-types */
import { useState, useEffect } from 'react';
import styles from './BookModal.module.css';
import BookDetail from './BookDetail';

const BookModal = ({ book, onClose }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [isBookOpen, setIsBookOpen] = useState(false);

  useEffect(() => {
    setIsOpen(true);
    // setTimeout(() => setIsBookOpen(true), 500);
  }, []);

  const handleBookOpen = () => {
    setIsBookOpen(true);
  };

  const handleClose = () => {
    if (isBookOpen) {
      setIsBookOpen(false);
      return;
    }
    setTimeout(() => {
      setIsOpen(false);
      onClose();
    }, 500);
  };

  return (
    <div className={`${styles.modalOverlay} ${isOpen ? styles.open : ''}`} onClick={handleClose}>
      <div className={`${styles.book} ${isBookOpen ? styles.bookOpen : ''}`} onClick={(e) => e.stopPropagation()}>
        <div className={styles.bookCover} onClick={handleBookOpen}>
          <img src={book.thumbnail} alt={book.title} />
        </div>
        <div className={styles.bookContent}>
          <BookDetail book={book} />
        </div>
      </div>
      <button className={styles.closeButton} onClick={handleClose}>닫기</button>
    </div>
  );
};

export default BookModal;