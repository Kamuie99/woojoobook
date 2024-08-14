import { useState, useRef } from 'react';
import Modal from './Modal';
import styles from './BookSearch.module.css';
import { IoSearchOutline } from "react-icons/io5";
import axiosInstance from '../../util/axiosConfig';
import Swal from 'sweetalert2';
import { HiOutlineInformationCircle } from "react-icons/hi";


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
      const newBooktest = response.data
      console.log(newBooktest.bookItems);
      const newBooks = response.data.bookItems.filter(book => !uniqueBooks.current.has(book.isbn));
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
        <div className={styles.titlebox}>
          <HiOutlineInformationCircle />
          등록을 원하시는 도서를 선택해주세요!  
        </div>
        {bookList.length > 0 ? (
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
        ) : (
          <div className={styles.noResults}>
            <p>검색 결과가 없습니다. 책 제목을 확인해 주세요.</p>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default BookSearch;