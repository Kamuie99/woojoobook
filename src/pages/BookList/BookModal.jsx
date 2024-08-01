/* eslint-disable react/prop-types */
import './BookModal.css';
import { getEmotionImage } from '../../util/get-emotion-image';

const BookModal = ({ book, onClose }) => {
  const getQualityEmoticon = (qualityStatus) => {
    switch(qualityStatus) {
      case 'VERY_GOOD': return getEmotionImage(1);
      case 'GOOD': return getEmotionImage(2);
      case 'NORMAL': return getEmotionImage(3);
      case 'BAD': return getEmotionImage(4);
      case 'VERY_BAD': return getEmotionImage(5);
      default: return null;
    }
  };

  const isRentalEnabled = book.tradeStatus === 'RENTAL_AVAILABLE' || book.tradeStatus === 'RENTAL_EXCHANGE_AVAILABLE';
  const isExchangeEnabled = book.tradeStatus === 'EXCHANGE_AVAILABLE' || book.tradeStatus === 'RENTAL_EXCHANGE_AVAILABLE';

  const handleRentalRequest = () => {
    if (isRentalEnabled) {
      console.log('대여 신청');
    }
  };

  const handleExchangeRequest = () => {
    if (isExchangeEnabled) {
      console.log('교환 신청');
    }
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <button className="close-button" onClick={onClose}>×</button>
        <div className="modal-body">
          <div className="book-image-container">
            <img src={book.bookInfo.thumbnail} alt={book.bookInfo.title} className="book-thumbnail" />
            <img 
              src={getQualityEmoticon(book.qualityStatus)} 
              alt={book.qualityStatus} 
              className="quality-emoticon"
            />
          </div>
          <div className="book-details">
            <h2 className='book-title'>{book.bookInfo.title}</h2>
            <p><strong>줄거리 |</strong> {book.bookInfo.description}</p>
            <div className='book-info'>
              <p><strong>출판사 |</strong> {book.bookInfo.publisher}</p>
              <p><strong>출판일 |</strong> {book.bookInfo.publicationDate}</p>
            </div>
            <p><strong>저자 |</strong> {book.bookInfo.author}</p>
            <p><strong className='special-user'>책권자 |</strong> {book.ownerInfo.nickname}</p>
            {/* <p><strong>거래 상태:</strong> {book.tradeStatus}</p> */}
          </div>
        </div>
        <div className="button-group">
          <button 
            onClick={handleRentalRequest} 
            disabled={!isRentalEnabled}
            className={isRentalEnabled ? 'enabled' : 'disabled'}
          >
            대여 신청
          </button>
          <button 
            onClick={handleExchangeRequest} 
            disabled={!isExchangeEnabled}
            className={isExchangeEnabled ? 'enabled' : 'disabled'}
          >
            교환 신청
          </button>
          <button onClick={() => console.log('채팅하기')}>채팅하기</button>
        </div>
      </div>
    </div>
  );
};

export default BookModal;