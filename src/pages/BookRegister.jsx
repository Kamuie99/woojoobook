import { useState, useRef } from 'react';
import Modal from 'react-modal';
import Header from "../components/Header";
import '../styles/BookRegister.css';
import { LuBookPlus } from "react-icons/lu";
import axiosInstance from '../util/axiosConfig';
import { IoSearchOutline } from "react-icons/io5";
import { getEmotionImage } from './../util/get-emotion-image';

Modal.setAppElement('#root');

const BookRegister = () => {
  const [searchKeyword, setSearchKeyword] = useState('');
  const [bookList, setBookList] = useState([]);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [selectedBook, setSelectedBook] = useState(null);
  const [page, setPage] = useState(1);
  const uniqueBooks = useRef(new Set());

  const [isRentable, setIsRentable] = useState(false);
  const [isExchangeable, setIsExchangeable] = useState(false);
  const [quality, setQuality] = useState('');

  const handleSearch = async () => {
    if (!searchKeyword.trim()) {
      alert('책 제목을 입력해주세요.');
      return;
    }
    setPage(1);
    uniqueBooks.current.clear();
    try {
      const response = await axiosInstance.get('/books', {
        params: {
          keyword: searchKeyword,
          page: page
        }
      });
      const newBooks = response.data.bookList.filter(book => !uniqueBooks.current.has(book.isbn));
      newBooks.forEach(book => uniqueBooks.current.add(book.isbn));
      setBookList(newBooks);
      setModalIsOpen(true);
    } catch (error) {
      console.error('Error: ', error);
    }
  };

  const handleSelectBook = (book) => {
    setSelectedBook(book);
    setModalIsOpen(false);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const getRegisterType = () => {
    if (isRentable && isExchangeable) return 'RENTAL_EXCHANGE';
    if (isRentable) return 'RENTAL';
    if (isExchangeable) return 'EXCHANGE';
    return '';
  };

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

  return (
    <div className="bookRegisterContainer">
      <Header />
      <main className="BookRegister">
        <div className="titleDiv">
          <LuBookPlus /> 내 책 등록하기
        </div>
        <div className="inputContainer">
          <div className='searchInputWrapper'>
            <input
              type="text"
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder="등록하실 책 제목을 입력해주세요"
            />
            {searchKeyword.trim() && (
              <button onClick={handleSearch}>
                <IoSearchOutline /> 
              </button>
            )}
          </div>
        </div>
        {selectedBook && (
          <div className="selectedBook">
            <div className='selectedImgDetail'>
              <img src={selectedBook.thumbnail} alt={selectedBook.title} />
              <div className="selectedBookDetails">
                <h3>{selectedBook.title}</h3>
                <p>저자 | {selectedBook.author}</p>
                <p>출판사 | {selectedBook.publisher}</p>
                <p>출판일 | {selectedBook.publicationDate}</p>
              </div>
            </div>
            <div className="additionalInfo">
              <div className='rentalexchangestate'>
                <label>
                  대여, 교환 상태: 
                  <span className="status-text">
                    {isRentable && "대여가능"}
                    {isRentable && isExchangeable && ", "}
                    {isExchangeable && "교환가능"}
                  </span>
                </label>
                <div className="checkboxGroup">
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
              <div className="bookCondition">
                <label>
                  책 상태:&nbsp;
                  <span
                    className="quality-text"
                    style={{ color: getQualityText(quality).color }}
                  >
                    {getQualityText(quality).text}
                  </span>
                </label>
                <div className="emotionImages">
                  {[1, 2, 3, 4, 5].map((id) => (
                    <img
                      key={id}
                      src={getEmotionImage(id)}
                      alt={`Condition ${id}`}
                      onClick={() => setQuality(getQualityFromCondition(id))}
                      className={quality === getQualityFromCondition(id) ? 'selected' : ''}
                    />
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}
        {selectedBook && (isRentable || isExchangeable) && quality && (
          <button onClick={handleSubmit} className="submitButton">등록하기</button>
        )}
      </main>
      <Modal
        isOpen={modalIsOpen}
        onRequestClose={() => setModalIsOpen(false)}
        contentLabel="Book Search Results"
        className="modal"
        overlayClassName="overlay"
      >
        <h2>Search Results</h2>
        <ul className="bookList">
          {bookList.map((book) => (
            <li key={book.isbn} className="bookItem" onClick={() => handleSelectBook(book)}>
              <img src={book.thumbnail} alt={book.title} />
              <div className="bookDetails">
                <h3>{book.title}</h3>
                <p>{book.author}</p>
              </div>
            </li>
          ))}
        </ul>
        <button className="closeButton" onClick={() => setModalIsOpen(false)}>Close</button>
      </Modal>
    </div>
  );
};

export default BookRegister;