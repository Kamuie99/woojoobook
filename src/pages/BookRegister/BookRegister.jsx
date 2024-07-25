import { useState } from 'react';
import { LuBookPlus } from "react-icons/lu";

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

  const handleSubmit = async () => {
    if (!selectedBook || (!isRentable && !isExchangeable) || !quality) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    const registerType = getRegisterType();

    try {
      const response = await axiosInstance.post('/userbooks', {
        isbn: selectedBook.isbn,
        registerType: registerType,
        quality: quality
      });
      console.log('Book registered:', response.data);
      // 성공 메시지 표시 또는 다른 작업 수행
    } catch (error) {
      console.error('Error registering book:', error);
      // 에러 메시지 표시
    }
  };

  const getRegisterType = () => {
    if (isRentable && isExchangeable) return 'RENTAL_EXCHANGE';
    if (isRentable) return 'RENTAL';
    if (isExchangeable) return 'EXCHANGE';
    return '';
  };

  return (
    <div className={styles.bookRegisterContainer}>
      <Header />
      <main className={styles.bookRegister}>
        <div className={styles.titleDiv}>
          <LuBookPlus /> 내 책 등록하기
        </div>
        <BookSearch onSelectBook={setSelectedBook} />
        {selectedBook && (
          <SelectedBook
            book={selectedBook}
            isRentable={isRentable}
            setIsRentable={setIsRentable}
            isExchangeable={isExchangeable}
            setIsExchangeable={setIsExchangeable}
            quality={quality}
            setQuality={setQuality}
          />
        )}
        {selectedBook && (isRentable || isExchangeable) && quality && (
          <button onClick={handleSubmit} className={styles.submitButton}>등록하기</button>
        )}
      </main>
    </div>
  );
};

export default BookRegister;