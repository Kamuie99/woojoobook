/* eslint-disable react/prop-types */
import styles from './SelectedBook.module.css';
import { getEmotionImage } from '../../util/get-emotion-image';

// eslint-disable-next-line react/prop-types
const SelectedBook = ({ book, isRentable, setIsRentable, isExchangeable, setIsExchangeable, quality, setQuality }) => {
  const getQualityFromCondition = (condition) => {
    switch(condition) {
      case 1: return 'VERY_GOOD';
      case 2: return 'GOOD';
      case 3: return 'NORMAL';
      case 4: return 'BAD';
      case 5: return 'VERY_BAD';
      default: return '';
    }
  };

  const getQualityText = (quality) => {
    switch(quality) {
      case 'VERY_GOOD': return { text: '매우 좋음', color: '#65C964' };
      case 'GOOD': return { text: '좋음', color: '#9ED672' };
      case 'NORMAL': return { text: '보통', color: '#FCCE18' };
      case 'BAD': return { text: '나쁨', color: '#FE8447' };
      case 'VERY_BAD': return { text: '매우 나쁨', color: '#FD565F' };
      default: return { text: '', color: '' };
    }
  };

  const getStatusClassName = () => {
    if (isRentable && isExchangeable) return styles.statusBoth;
    if (isRentable) return styles.statusRental;
    if (isExchangeable) return styles.statusExchange;
    return '';
  };

  return (
    <div className={styles.selectedBook}>
      <div className={styles.selectedImgDetail}>
        <img src={book.thumbnail} alt={book.title} />
        <div className={styles.selectedBookDetails}>
          <h3>{book.title}</h3>
          <p>저자 | {book.author}</p>
          <p>출판사 | {book.publisher}</p>
          <p>출판일 | {book.publicationDate}</p>
        </div>
      </div>
      <div className={styles.additionalInfo}>
        <div className={styles.rentalexchangestate}>
          <label>
            등록 정보: 
            <span className={`${styles.statusText} ${getStatusClassName()}`}>
              {isRentable && "대여 가능"}
              {isRentable && isExchangeable && ", "}
              {isExchangeable && "교환 가능"}
            </span>
          </label>
          <div className={styles.checkboxGroup}>
            <label>
              <input 
                type="checkbox" 
                checked={isRentable} 
                onChange={(e) => setIsRentable(e.target.checked)} 
              />
              대여 가능 여부
            </label>
            <label>
              <input 
                type="checkbox" 
                checked={isExchangeable} 
                onChange={(e) => setIsExchangeable(e.target.checked)} 
              />
              교환 가능 여부
            </label>
          </div>
        </div>
        <div className={styles.bookCondition}>
          <label>
            책 상태:&nbsp;
            <span
              className={styles.qualityText}
              style={{ color: getQualityText(quality).color }}
            >
              {getQualityText(quality).text}
            </span>
          </label>
          <div className={styles.emotionImages}>
            {[1, 2, 3, 4, 5].map((id) => (
              <img
                key={id}
                src={getEmotionImage(id)}
                alt={`Condition ${id}`}
                onClick={() => setQuality(getQualityFromCondition(id))}
                className={quality === getQualityFromCondition(id) ? styles.selected : ''}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SelectedBook;