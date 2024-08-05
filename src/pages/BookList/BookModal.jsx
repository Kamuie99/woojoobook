/* eslint-disable react/prop-types */
import { useState, useContext } from 'react';
import styles from './BookModal.module.css';
import { getEmotionImage } from '../../util/get-emotion-image';
import axiosInstance from '../../util/axiosConfig';
import { AuthContext } from '../../contexts/AuthContext';
import Swal from 'sweetalert2';
import ExchangeModal from './ExchangeModal';

const BookModal = ({ book, onClose }) => {
  const { user } = useContext(AuthContext);
  const [showExchangeModal, setShowExchangeModal] = useState(false);

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
      setShowExchangeModal(true);
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
    <div className={styles.modalBackdrop} onClick={onClose}>
      <div className={styles.modalContent} onClick={e => e.stopPropagation()}>
        <button className={styles.closeButton} onClick={onClose}>×</button>
        <div className={styles.modalBody}>
          <div className={styles.bookImageContainer}>
            <img src={book.bookInfo.thumbnail} alt={book.bookInfo.title} className={styles.bookThumbnail} />
            <img 
              src={getQualityEmoticon(book.qualityStatus)} 
              alt={book.qualityStatus} 
              className={styles.qualityEmoticon}
            />
          </div>
          <div className={styles.bookDetails}>
            <h2 className={styles.bookTitle}>{book.bookInfo.title}</h2>
            <p><strong>줄거리 |</strong> {book.bookInfo.description}</p>
            <div className={styles.bookInfo}>
              <p><strong>출판사 |</strong> {book.bookInfo.publisher}</p>
              <p><strong>출판일 |</strong> {book.bookInfo.publicationDate}</p>
            </div>
            <p><strong>저자 |</strong> {book.bookInfo.author}</p>
            <p><strong className={styles.specialUser}>책권자 |</strong> {book.ownerInfo.nickname}</p>
          </div>
        </div>
        <div className={styles.buttonGroup}>
          {!isOwner && (
            <>
              <div className={styles.tooltipContainer}>
                <button 
                  onClick={handleRentalRequest} 
                  disabled={!isRentalEnabled}
                  className={isRentalEnabled ? styles.enabled : styles.disabled}
                >
                  대여 신청
                </button>
                {!isRentalEnabled && <span className={styles.tooltip}>대여가 불가능한 도서입니다.</span>}
              </div>
              <div className={styles.tooltipContainer}>
                <button 
                  onClick={handleExchangeRequest} 
                  disabled={!isExchangeEnabled}
                  className={isExchangeEnabled ? styles.enabled : styles.disabled}
                >
                  교환 신청
                </button>
                {!isExchangeEnabled && <span className={styles.tooltip}>교환이 불가능한 도서입니다.</span>}
              </div>
            </>
          )}
          <button onClick={() => console.log('채팅하기')} className={styles.tochatButton}>채팅하기</button>
        </div>
      </div>
      {showExchangeModal && (
        <ExchangeModal 
          receiverBook={book} 
          onClose={() => setShowExchangeModal(false)} 
        />
      )}
    </div>
  );
};

export default BookModal;
