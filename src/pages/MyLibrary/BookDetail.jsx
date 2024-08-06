/* eslint-disable react/prop-types */
// BookDetail.jsx
import styles from './BookDetail.module.css';

const BookDetail = ({ book }) => {
  return (
    <div className={styles.bookDetail}>
      <h3 className={styles.title}>{book.title}</h3>
      <div className={styles.info}>
        <p><strong>저자:</strong> {book.author}</p>
        <p><strong>출판사:</strong> {book.publisher}</p>
        <p><strong>출판일:</strong> {book.publicationDate}</p>
        <p><strong>ISBN:</strong> {book.isbn}</p>
      </div>
      <div className={styles.description}>
        <strong>설명:</strong>
        <p>{book.description}</p>
      </div>
    </div>
  );
};

export default BookDetail;