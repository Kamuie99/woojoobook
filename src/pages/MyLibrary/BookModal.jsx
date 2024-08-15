import { useState, useEffect } from 'react';
import styles from './BookModal.module.css';
import BookDetail from './BookDetail';

const BookModal = ({ book, onClose }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [isBookOpen, setIsBookOpen] = useState(false);
  const [isFluttering, setIsFluttering] = useState(true);

  useEffect(() => {
    setIsOpen(true);
  }, []);

  const handleBookOpen = () => {
    setIsFluttering(false);
    setTimeout(() => {
      setIsBookOpen(true);
    }, 50);
  };

  const handleClose = () => {
    if (isBookOpen) {
      setIsBookOpen(false);
      setTimeout(() => {
        setIsFluttering(true);
      }, 500);
      return;
    }
    setIsOpen(false);
    onClose();
  };

  return (
    <div className={`${styles.modalOverlay} ${isOpen ? styles.open : ''}`} onClick={handleClose}>
      <div className={`${styles.book} ${isBookOpen ? styles.bookOpen : ''}`} onClick={(e) => e.stopPropagation()}>
        <div className={`${styles.bookCover} ${isFluttering ? styles.flutter : ''}`} onClick={handleBookOpen}>
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