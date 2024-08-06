/* eslint-disable react/prop-types */
import { useState, useEffect } from 'react';
import axiosInstance from '../../util/axiosConfig';
import Swal from 'sweetalert2';
import { LuBookPlus } from "react-icons/lu";
import styles from './ExchangeModal.module.css';

const ExchangeModal = ({ receiverBook, onClose }) => {
  const [userBooks, setUserBooks] = useState([]);
  const [selectedBook, setSelectedBook] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  useEffect(() => {
    fetchUserBooks(currentPage);
  }, [currentPage]);

  const fetchUserBooks = async (page) => {
    try {
      const response = await axiosInstance.get(`/users/userbooks/registered?registerType=EXCHANGE&size=9&page=${page}`);
      setUserBooks(response.data.content);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
    } catch (error) {
      console.error('Error fetching user books:', error);
    }
  };

  const handleBookSelect = (book) => {
    setSelectedBook(book);
  };

  const handleExchangeConfirm = async () => {
    if (!selectedBook) return;

    const result = await Swal.fire({
      title: '교환 신청 하시겠습니까?',
      html: `
        <div style="text-align: left; line-height: 1.5;">
          <p><strong>내 책:</strong> ${selectedBook.bookInfo.title}</p>
          <p><strong>상대방 책:</strong> ${receiverBook.bookInfo.title}</p>
        </div>
      `,
      showCancelButton: true,
      confirmButtonText: '신청',
      cancelButtonText: '취소',
      icon: 'question'
    });

    if (result.isConfirmed) {
      try {
        const response = await axiosInstance.post(`/userbooks/${receiverBook.id}/exchanges/offer/${selectedBook.id}`, {
          senderBookId: selectedBook.id,
          receiverBookId: receiverBook.id
        });
        console.log('교환 신청 성공:', response.data);

        await Swal.fire({
          title: '교환 신청 완료',
          text: '교환 신청이 완료되었습니다.',
          confirmButtonText: '확인',
          icon: 'success'
        });

        onClose();
      } catch (error) {
        console.error('교환 신청 실패:', error);
        Swal.fire({
          title: '오류',
          text: '교환 신청 중 오류가 발생했습니다.',
          confirmButtonText: '확인',
          icon: 'warning'
        });
      }
    }
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  return (
    <div className={styles.exchangeModalBackdrop} onClick={onClose}>
      <div className={styles.exchangeModalContent} onClick={(e) => e.stopPropagation()}>
        <div className={styles.modalInnerContent}>  
          <h2><LuBookPlus /> 교환 가능한 내 책 목록</h2>
          <div className={styles.bookList}>
            {userBooks.map(book => (
              <div 
                key={book.id} 
                className={`${styles.bookItem} ${selectedBook?.id === book.id ? styles.bookItemSelected : ''}`}
                onClick={() => handleBookSelect(book)}
              >
                <img src={book.bookInfo.thumbnail} alt={book.bookInfo.title} />
                <div className={styles.bookInfo}>
                  <h3>{book.bookInfo.title}</h3>
                  <p>{book.bookInfo.author}</p>
                </div>
              </div>
            ))}
          </div>
          <div className={styles.pagination}>
            <button 
              onClick={() => handlePageChange(currentPage - 1)} 
              disabled={currentPage === 0}
            >
              이전
            </button>
            <span>{currentPage + 1} / {totalPages}</span>
            <button 
              onClick={() => handlePageChange(currentPage + 1)} 
              disabled={currentPage === totalPages - 1}
            >
              다음
            </button>
          </div>
          <p>총 {totalElements}개의 책</p>
        </div>
        <div className={styles.buttonGroup}>
          <button onClick={handleExchangeConfirm} disabled={!selectedBook}>교환 신청</button>
          <button onClick={onClose}>취소</button>
        </div>
      </div>
    </div>
  );
};

export default ExchangeModal;
