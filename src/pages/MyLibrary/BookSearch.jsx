import React, { useState, useCallback } from 'react';
import BookList from './BookList';
import Modal from '../BookRegister/Modal';
import axiosInstance from '../../util/axiosConfig';
import styles from './BookSearch.module.css';

const BookSearch = ({ onSelect }) => {
  const [keyword, setKeyword] = useState('');
  const [page, setPage] = useState(1);
  const [books, setBooks] = useState([]);
  const [maxPage, setMaxPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [noResults, setNoResults] = useState(false);

  const handleSearch = () => {
    if (!keyword.trim()) {
      alert('검색어를 입력해주세요.');
      return;
    }
    searchBooks(keyword, 1);
    setIsModalOpen(true);
  };

  const searchBooks = useCallback(async (searchKeyword, searchPage) => {
    if (!searchKeyword) return;
    
    try {
      const response = await axiosInstance.get('/books', {
        params: {keyword: searchKeyword, page: searchPage}
      })
      setBooks(response.data.bookList)
      setMaxPage(response.data.maxPage)
      setPage(searchPage)
      setNoResults(response.data.bookList.length === 0)
    } catch (error) {
      console.log(error)
      setNoResults(true)
    }
  }, [])

  const handleSelect = (book) => {
    onSelect(book);
    setIsModalOpen(false);
    setKeyword('');
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 1 && newPage <= maxPage) {
      searchBooks(keyword, newPage);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleSearch();
    }
  }

  const closeModal = () => {
    setIsModalOpen(false);
    setKeyword('');
  };

  return (
    <div className={styles.searchContainer}>
      <div className={styles.searchInputContainer}>
        <input
          className={styles.searchInput}
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="책 제목을 입력하세요"
        />
        <button className={styles.searchButton} onClick={handleSearch} type='button'>검색</button>
      </div>

      <Modal 
        isOpen={isModalOpen} 
        onRequestClose={closeModal} 
        contentLabel="Book Search Results"
      >
        <div className={styles.modalContent}>
          {noResults ? (
            <div className={styles.noResults}>검색 결과가 없습니다.</div>
          ) : (
            <>
              <BookList books={books} onSelect={handleSelect} selectable={true} />
              <div className={styles.paginationContainer}>
                <button
                  className={styles.pageButton}
                  onClick={() => handlePageChange(page - 1)}
                  disabled={page <= 1}
                >
                  이전 페이지
                </button>
                <button
                  className={styles.pageButton}
                  onClick={() => handlePageChange(page + 1)}
                  disabled={page >= maxPage}
                >
                  다음 페이지
                </button>
              </div>
          </>
          )}
        </div>
      </Modal>
    </div>
  );
};

export default BookSearch;