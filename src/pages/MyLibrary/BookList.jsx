/* eslint-disable react/prop-types */
import styles from './BookList.module.css';

const BookList = ({ books, onSelect, onRemove, selectable = false }) => (
  <div className={styles.bookListContainer}>
    {books.map((book) => (
      <div 
        key={book.isbn} 
        className={styles.bookItem}
        onClick={selectable ? () => onSelect(book) : undefined}
        style={selectable ? { cursor: 'pointer' } : {}}
      >
        <img src={book.thumbnail} alt={book.title} className={styles.bookThumbnail} />
        <div className={styles.bookDetails}>
          <p className={styles.bookTitle}>{book.title}</p>
          <p className={styles.bookAuthor}>{book.author}</p>
        </div>
        {onRemove && (
          <div className={styles.actionButtons}>
            <button
              type="button"
              className={styles.actionButton}
              onClick={(e) => {
                e.stopPropagation();
                onRemove(book.isbn);
              }}
            >
              제거
            </button>
          </div>
        )}
      </div>
    ))}
  </div>
);


export default BookList;