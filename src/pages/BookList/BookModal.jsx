/* eslint-disable react/prop-types */
import { useState, useContext } from 'react';
import styles from './BookModal.module.css';
import { getEmotionImage } from '../../util/get-emotion-image';
import axiosInstance from '../../util/axiosConfig';
import { AuthContext } from '../../contexts/AuthContext';
import Swal from 'sweetalert2';
import ExchangeModal from './ExchangeModal';

const BookModal = ({ book, onClose, onChatOpen }) => {
  const { user, sub: userId } = useContext(AuthContext);
  const [showExchangeModal, setShowExchangeModal] = useState(false);

  const userbook = book.userbook;
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

  const isRentalEnabled = userbook.tradeStatus === 'RENTAL_AVAILABLE' || userbook.tradeStatus === 'RENTAL_EXCHANGE_AVAILABLE';
  const isExchangeEnabled = userbook.tradeStatus === 'EXCHANGE_AVAILABLE' || userbook.tradeStatus === 'RENTAL_EXCHANGE_AVAILABLE';
  const isOwner = user && user.id === userbook.ownerInfo.id;

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
          <p><strong style="color: var(--contrast-color)">책권자 |</strong> ${userbook.ownerInfo.nickname}</p>
          <p><strong style="color: var(--accent-sub-color)">대여일 |</strong> 기본 7일</p>
          <p><strong style="color: var(--accent-color)">책제목 |</strong> ${truncateTitle(userbook.bookInfo.title, 20)}</p>
        </div> 
        `,
        showCancelButton: true,
        confirmButtonText: '신청',
        cancelButtonText: '취소',
        icon: 'question'
      });

      if (result.isConfirmed) {
        try {
          const response = await axiosInstance.post(`/userbooks/${userbook.id}/rentals/offer`);
          console.log('대여 신청 성공:', response.data);

          await Swal.fire({
            title: '대여 신청 완료',
            text: '대여 신청이 완료되었습니다.',
            confirmButtonText: '확인',
            icon: 'success'
          });

          onClose();
        } catch (error) {
          console.error('대여 신청 실패:', error.response);
          const errMsg = error.response.data === '이미 존재합니다.' ?
            '이미 대여 신청을 보냈습니다.' :
            '대여 신청 중 오류가 발생했습니다.'
          Swal.fire({
            title: '오류',
            text: errMsg,
            confirmButtonText: '확인',
            icon: 'warning'
          });
        }
      }
    }
  };
  
  const handleNewChat = (e, ownerId) => {
    e.preventDefault();
    onChatOpen(ownerId);
  }

  return (
    <div className={styles.modalBackdrop} onClick={onClose}>
      <div className={styles.modalContent} onClick={e => e.stopPropagation()}>
        <button className={styles.closeButton} onClick={onClose}>×</button>
        <div className={styles.modalBody}>
          <div className={styles.bookImageContainer}>
            <img src={userbook.bookInfo.thumbnail} alt={userbook.bookInfo.title} className={styles.bookThumbnail} />
            <img 
              src={getQualityEmoticon(userbook.qualityStatus)} 
              alt={userbook.qualityStatus} 
              className={styles.qualityEmoticon}
            />
          </div>
          <div className={styles.bookDetails}>
            <h2 className={styles.bookTitle}>{userbook.bookInfo.title}</h2>
            <p><strong>줄거리 |</strong> {userbook.bookInfo.description}</p>
            <div className={styles.bookInfo}>
              <p><strong>출판사 |</strong> {userbook.bookInfo.publisher}</p>
              <p><strong>출판일 |</strong> {userbook.bookInfo.publicationDate}</p>
            </div>
            <p><strong>저자 |</strong> {userbook.bookInfo.author}</p>
            <p><strong className={styles.specialUser}>책권자 |</strong> {userbook.ownerInfo.nickname}</p>
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
              <button onClick={(e) => handleNewChat(e, userbook.ownerInfo.id)} className={styles.tochatButton}>채팅하기</button>
            </>
          )}
        </div>
      </div>
      {showExchangeModal && (
        <ExchangeModal 
          receiverBook={userbook} 
          onClose={() => setShowExchangeModal(false)} 
        />
      )}
    </div>
  );
};

export default BookModal;
