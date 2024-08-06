import { useState, useRef } from 'react';
import Modal from './Modal';
import styles from './BookSearch.module.css';
import { IoSearchOutline } from "react-icons/io5";
import axiosInstance from '../../util/axiosConfig';
import Swal from 'sweetalert2';

// eslint-disable-next-line react/prop-types
const BookSearch = ({ onSelectBook }) => {
  const [searchKeyword, setSearchKeyword] = useState('');
  const [bookList, setBookList] = useState([]);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [page, setPage] = useState(1);
  const [isBookSelected, setIsBookSelected] = useState(false);
  const uniqueBooks = useRef(new Set());

  const handleSearch = async () => {
    if (!searchKeyword.trim()) {
      Swal.fire({
        title: '검색 실패',
        text: '책 제목 / 저자 / ISBN을 입력해주세요.',
        confirmButtonText: '확인',
        icon: 'error'
      });
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

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const handleSelectBook = (book) => {
    onSelectBook(book);
    setModalIsOpen(false);
    setIsBookSelected(true);
  };

  return (
    <div className={`${styles.inputContainer} ${isBookSelected ? styles.inputContainerSelected : ''}`}>
      <div className={styles.searchInputWrapper}>
        <input
          type="text"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="등록하실 책 제목을 입력해주세요"
        />
        <button onClick={handleSearch}>
          <IoSearchOutline size={"35px"} /> 
        </button>
      </div>
      <Modal
        isOpen={modalIsOpen}
        onRequestClose={() => setModalIsOpen(false)}
        contentLabel="Book Search Results"
      >
        <h2>Search Results</h2>
        <ul className={styles.bookList}>
          {bookList.map((book) => (
            <li key={book.isbn} className={styles.bookItem} onClick={() => handleSelectBook(book)}>
              <img src={book.thumbnail} alt={book.title} />
              <div className={styles.bookDetails}>
                <h3>{book.title}</h3>
                <p>{book.author}</p>
              </div>
            </li>
          ))}
        </ul>
      </Modal>
    </div>
  );
};

export default BookSearch;