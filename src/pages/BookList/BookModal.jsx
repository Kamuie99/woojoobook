/* eslint-disable react/prop-types */
import { useContext } from 'react';
import './BookModal.css';
import { getEmotionImage } from '../../util/get-emotion-image';
import axiosInstance from '../../util/axiosConfig';
import { AuthContext } from '../../contexts/AuthContext';
import Swal from 'sweetalert2';

const BookModal = ({ book, onClose }) => {
  const { user } = useContext(AuthContext);

  const getQualityEmoticon = (qualityStatus) => {
    switch (qualityStatus) {
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
  const isOwner = user && user.id === book.ownerInfo.id;

  const handleExchangeRequest = () => {
    if (isExchangeEnabled && !isOwner) {
      console.log('교환 신청');
    }
  };

  const truncateTitle = (title, maxLength) => {
    return title.length > maxLength ? title.slice(0, maxLength) + '...' : title;
  };

  const handleRentalRequest = async () => {
    if (isRentalEnabled && !isOwner) {
      const result = await Swal.fire({
        title: '대여 신청 하시겠습니까?',
        html: `
        <div style="text-align: left; line-height: 1.5;">
          <p><strong style="color: var(--contrast-color)">책권자 |</strong> ${book.ownerInfo.nickname}</p>
          <p><strong style="color: var(--accent-sub-color)">대여일 |</strong> 기본 7일</p>
          <p><strong style="color: var(--accent-color)">책제목 |</strong> ${truncateTitle(book.bookInfo.title, 20)}</p>
        </div> 
        `,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '신청',
        cancelButtonText: '취소'
      });

      if (result.isConfirmed) {
        try {
          const response = await axiosInstance.post(`/userbooks/${book.id}/rentals/offer`);
          console.log('대여 신청 성공:', response.data);

          await Swal.fire({
            title: '대여 신청 완료',
            text: `대여 신청이 완료되었습니다. (대여 ID: ${response.data.rentalId})`,
            icon: 'success',
            confirmButtonText: '확인'
          });

          // 모달 닫기 및 부모 컴포넌트의 책 목록 갱신
          onClose();
        } catch (error) {
          console.error('대여 신청 실패:', error);
          Swal.fire({
            title: '오류',
            text: '대여 신청 중 오류가 발생했습니다.',
            icon: 'error',
            confirmButtonText: '확인'
          });
        }
      }
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
          </div>
        </div>
        <div className="button-group">
  {!isOwner && (
    <>
      <div className="tooltip-container">
        <button 
          onClick={handleRentalRequest} 
          disabled={!isRentalEnabled}
          className={isRentalEnabled ? 'enabled' : 'disabled'}
        >
          대여 신청
        </button>
        {!isRentalEnabled && <span className="tooltip">대여가 불가능한 도서입니다.</span>}
      </div>
      <div className="tooltip-container">
        <button 
          onClick={handleExchangeRequest} 
          disabled={!isExchangeEnabled}
          className={isExchangeEnabled ? 'enabled' : 'disabled'}
        >
          교환 신청
        </button>
        {!isExchangeEnabled && <span className="tooltip">교환이 불가능한 도서입니다.</span>}
      </div>
    </>
  )}
  <button onClick={() => console.log('채팅하기')} className='tochatButton'>채팅하기</button>
</div>
      </div>
    </div>
  );
};

export default BookModal;
