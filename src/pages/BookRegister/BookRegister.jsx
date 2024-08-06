import { useState, useEffect } from 'react';
import { LuBookPlus } from "react-icons/lu";
import { useNavigate } from 'react-router-dom';
import Swal from "sweetalert2";
import Header from "../../components/Header";
import BookSearch from "./BookSearch";
import SelectedBook from "./SelectedBook";
import styles from './BookRegister.module.css';
import axiosInstance from '../../util/axiosConfig';

const BookRegister = () => {
  const [selectedBook, setSelectedBook] = useState(null);
  const [isRentable, setIsRentable] = useState(false);
  const [isExchangeable, setIsExchangeable] = useState(false);
  const [quality, setQuality] = useState('');
  const [isFormValid, setIsFormValid] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    setIsFormValid(selectedBook && (isRentable || isExchangeable) && quality);
  }, [selectedBook, isRentable, isExchangeable, quality]);

  const getRegisterType = () => {
    if (isRentable && isExchangeable) return 'RENTAL_EXCHANGE';
    if (isRentable) return 'RENTAL';
    if (isExchangeable) return 'EXCHANGE';
    return '';
  };

  const handleSubmit = async () => {
    if (!isFormValid) {
      await Swal.fire({
        title: '입력 오류',
        text: '모든 필드를 입력해주세요.',
        icon: 'error',
        confirmButtonText: '확인'
      });
      return;
    }
    
    const registerType = getRegisterType();

    const truncateTitle = (title, maxLength) => {
      return title.length > maxLength ? title.slice(0, maxLength) + '...' : title;
    };
    
    const result = await Swal.fire({
      title: '다음과 같이 등록 하시겠습니까?',
      html: `
        <div style="text-align: left; line-height: 1.5;">
          <p><strong>제목:</strong> ${truncateTitle(selectedBook.title, 20)}</p>
          <p><strong>대여:</strong> ${isRentable ? '가능' : '불가능'}</p>
          <p><strong>교환:</strong> ${isExchangeable ? '가능' : '불가능'}</p>
        </div>
      `,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: '등록',
      cancelButtonText: '취소'
    });

    if (result.isConfirmed) {
      try {
        await axiosInstance.post('/userbooks', {
          isbn: selectedBook.isbn,
          registerType: registerType,
          quality: quality
        });
        
        await Swal.fire({
          title: "등록 완료!",
          html: `책이 정상적으로 등록되었습니다.`,
          icon: "success",
          confirmButtonText: "확인",
        });
        
        navigate('/'); // 홈으로 이동
      } catch (error) {
        await Swal.fire({
          title: '오류',
          text: '책 등록 중 오류가 발생했습니다.',
          icon: 'error',
          confirmButtonText: '확인'
        });
      }
    }
  };

  const getButtonTooltip = () => {
    if (!selectedBook) return "도서를 선택해주세요";
    if (!isRentable && !isExchangeable) return "등록 정보를 선택해주세요";
    if (!quality) return "도서 상태를 선택해주세요";
    return "";
  };

  return (
    <div className={styles.bookRegisterContainer}>
      <Header />
      <main className={styles.bookRegister}>
        <div className={styles.titleDiv}>
          <LuBookPlus /> 우주도서 등록
        </div>
        <BookSearch onSelectBook={setSelectedBook} />
        {selectedBook && (
          <>
            <SelectedBook
              book={selectedBook}
              isRentable={isRentable}
              setIsRentable={setIsRentable}
              isExchangeable={isExchangeable}
              setIsExchangeable={setIsExchangeable}
              quality={quality}
              setQuality={setQuality}
            />
            <button 
              onClick={handleSubmit} 
              className={`${styles.submitButton} ${!isFormValid ? styles.disabled : ''}`}
              disabled={!isFormValid}
              title={!isFormValid ? getButtonTooltip() : ""}
            >
              등록하기
            </button>
          </>
        )}
      </main>
    </div>
  );
};

export default BookRegister;