/* eslint-disable react/prop-types */
import { useState, useContext } from 'react';
import { Link } from 'react-router-dom';
import styles from './BookModal.module.css';
import { getEmotionImage } from '../../util/get-emotion-image';
import axiosInstance from '../../util/axiosConfig';
import { AuthContext } from '../../contexts/AuthContext';
import Swal from 'sweetalert2';
import ExchangeModal from './ExchangeModal';

const BookModal = ({ book, onClose, onChatOpen }) => {
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
  const isOwner = user && user.id === book.userbookid;

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
          <p><strong style="color: var(--contrast-color)">책권자 |</strong> ${book.user.nickname}</p>
          <p><strong style="color: var(--accent-sub-color)">대여일 |</strong> 기본 7일</p>
          <p><strong style="color: var(--accent-color)">책제목 |</strong> ${truncateTitle(book.book.title, 20)}</p>
        </div> 
        `,
        showCancelButton: true,
        confirmButtonText: '신청',
        cancelButtonText: '취소',
        icon: 'question'
      });

      if (result.isConfirmed) {
        try {
          const response = await axiosInstance.post(`/userbooks/${book.userbookid}/rentals/offer`);
          // console.log('대여 신청 성공:', response.data);

          await Swal.fire({
            title: '대여 신청 완료',
            text: '대여 신청이 완료되었습니다.',
            confirmButtonText: '확인',
            icon: 'success'
          });

          onClose();
        } catch (error) {
          console.error('대여 신청 실패:', error.response);
          let title = '오류'
          let errMsg = '대여 신청 중 오류가 발생했습니다.'
          if (error.response.data === '이미 존재합니다.') {
            errMsg = '이미 대여 신청을 보냈습니다.'
          } else if (error.response.data === '포인트가 부족합니다.') {
            errMsg = err.response.data
            title = '포인트 부족'
          }
          
          Swal.fire({
            title: title,
            text: errMsg,
            confirmButtonText: '확인',
            icon: 'warning'
          });
          // const errMsg2 = error.response.data === '포인트가 부족합니다.' ?
          //   '포인트가 부족합니다.' :
          //   '대여 신청 중 오류가 발생했습니다.'
          // Swal.fire({
          //   title: '포인트 부족',
          //   text: errMsg2,
          //   confirmButtonText: '확인',
          //   icon: 'warning'
          // });
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
            <img src={book.book.thumbnail} alt={book.book.title} className={styles.bookThumbnail} />
            <img 
              src={getQualityEmoticon(book.qualityStatus)} 
              alt={book.qualityStatus} 
              className={styles.qualityEmoticon}
            />
          </div>
          <div className={styles.bookDetails}>
            <h2 className={styles.bookTitle}>{book.book.title}</h2>
            <p><strong>줄거리 |</strong> {book.book.description}</p>
            <div className={styles.bookInfo}>
              <p><strong>출판사 |</strong> {book.book.publisher}</p>
              <p><strong>출판일 |</strong> {book.book.publicationDate}</p>
            </div>
            <p><strong>저자 |</strong> {book.book.author}</p>
            <div className={styles.tooltipContainer}>
              <p className={styles.toLibraryButton}>
                <Link to={`/${book.user.id}/mylibrary`}>
                  <strong className={styles.specialUser}>책권자 |</strong> {book.user.nickname}
                </Link>
              </p>
              {<span className={styles.libraryTooltip}>
                {book.user.nickname === user.nickname ?
                  '내' : `${book.user.nickname} 님의`} 서재로 이동하기
              </span>}
            </div>
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
              <button onClick={(e) => handleNewChat(e, book.user.id)} className={styles.tochatButton}>채팅하기</button>
            </>
          )}
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
