import { useState, useEffect, useCallback, useContext } from "react";
import { useSearch } from '../../contexts/SearchContext';
import { LuBookPlus } from "react-icons/lu";
import { CiHeart } from "react-icons/ci";
import { getEmotionImage } from '../../util/get-emotion-image';
import { AuthContext } from '../../contexts/AuthContext';
import { FiMapPin } from "react-icons/fi";
import Header from "../../components/Header"
import axiosInstance from './../../util/axiosConfig';
import AreaSelector from "../../components/AreaSelector";
import BookModal from './BookModal';
import styles from './BookList.module.css';

const BookList = () => {
  const { searchTerm: initialSearchTerm  } = useSearch();
  const [searchTerm, setSearchTerm] = useState(initialSearchTerm);
  const [inputValue, setInputValue] = useState(initialSearchTerm || '');
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [areaNames, setAreaNames] = useState({});
  const [selectedAreaCode, setSelectedAreaCode] = useState('');
  const [userAreaName, setUserAreaName] = useState('');
  const { user } = useContext(AuthContext);
  const [selectedBook, setSelectedBook] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchBooks = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = { 
        keyword: searchTerm,
        page: currentPage,
        size: 10
      };
      if (selectedAreaCode) {
        params.areaCodeList = selectedAreaCode;
      }
      const response = await axiosInstance.get('/userbooks', { params });
      setBooks(response.data.content);
      setTotalPages(response.data.totalPages);
      return response.data.content;
    } catch (err) {
      setError('힝 실패했음...')
      console.error('Error fetching Books: ', err)
    } finally {
      setLoading(false);
    }
  }, [searchTerm, selectedAreaCode, currentPage]);

  const fetchAreaName = useCallback(async (areaCode) => {
    if (areaCode) {
      try {
        const response = await axiosInstance.get('/area', { params: { areaCode } });
        return response.data.dongName;
      } catch (err) {
        console.error('지역 이름 조회 중 에러 발생:', err);
        return '알 수 없음';
      }
    }
    return '';
  }, []);

  useEffect(() => {
    const updateUserAreaName = async () => {
      const areaName = await fetchAreaName(user?.areaCode);
      setUserAreaName(areaName);
    };
    updateUserAreaName();
  }, [user?.areaCode, fetchAreaName]);

  useEffect(() => {
    fetchBooks().then(fetchedBooks => {
      if (fetchedBooks) {
        fetchAreaNames(fetchedBooks);
      }
    });
  }, [fetchBooks, currentPage]);

  const fetchAreaNames = async (books) => {
    const areaPromises = books.map(book => 
      axiosInstance.get('/area', { params: { areaCode: book.areaCode } })
    );

    try {
      const areaResponses = await Promise.all(areaPromises);
      const newAreaNames = {};
      areaResponses.forEach((response, index) => {
        newAreaNames[books[index].areaCode] = response.data.dongName;
      });
      setAreaNames(newAreaNames);
    } catch (err) {
      console.error('지역코드로 동 이름 조회중 에러가 발생: ', err);
    }
  };

  const handleSearch = () => {
    setSearchTerm(inputValue);
    setCurrentPage(0);
  };

  const handleInputChange = (e) => {
    setInputValue(e.target.value);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const handleAreaSelected = async (areaCode) => {
    setSelectedAreaCode(areaCode);
    setBooks([]);
    setCurrentPage(0);
    const areaName = await fetchAreaName(areaCode);
    setUserAreaName(areaName);
  };

  const openModal = (book) => {
    setSelectedBook(book);
  };

  const closeModal = () => {
    setSelectedBook(null);
    fetchBooks();
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  const getQualityEmoticon = (qualityStatus) => {
    switch(qualityStatus) {
      case 'VERY_GOOD': return getEmotionImage(1);
      case 'GOOD': return getEmotionImage(2);
      case 'NORMAL': return getEmotionImage(3);
      case 'BAD': return getEmotionImage(4);
      case 'VERY_BAD': return getEmotionImage(5);
      default: return null;
    }
  };

  const truncateAuthor = (author) => {
    const firstAuthor = author.split('^')[0];
    return firstAuthor;
  };

  return (
    <>
      <Header />
      <div className={styles.titleAndSearch}>
        <div className={styles.titleDiv}>
          <LuBookPlus /> 우주 도서 ({userAreaName || '지역 정보 로딩 중...'})
        </div>
      </div>
      <div className={styles.areaSelectorContainer}>
        <div>
          <AreaSelector onAreaSelected={handleAreaSelected} />
        </div>
        <div className={styles.searchBox}>
          <input
            type="text"
            value={inputValue}
            onChange={handleInputChange}
            onKeyPress={handleKeyPress}
            placeholder="책 제목 또는 저자를 검색"
            style={{ padding: '10px', borderRadius: '10px' }}
          />
          <button onClick={handleSearch} style={{ padding: '5px 10px' }}>검색</button>
        </div>  
      </div>
      <main className={styles.main}>
        {loading && <p>로딩중...</p>}
        {error && <p>{error}</p>}
        {!loading && !error && (
          books.length > 0 ? (
            <div className={styles.list}>
              {books.map((book) => (
                <div key={book.id} className={styles.item}>
                  <div className={styles.imageContainer}>
                    <img 
                      src={getQualityEmoticon(book.qualityStatus)} 
                      alt={book.qualityStatus} 
                      className={styles.qualityEmoticon}
                    />
                    <img 
                      src={book.bookInfo.thumbnail} 
                      alt={book.bookInfo.title} 
                      style={{ width: '100px', height: '140px' }} 
                    />
                  </div>
                  <div className={styles.description} onClick={() => openModal(book)}>
                    <h3>{book.bookInfo.title}</h3>
                    <div className={styles.innerBox}>
                      <p><strong>저자 |</strong> {truncateAuthor(book.bookInfo.author)}</p>
                      <p><strong>책권자 |</strong> {book.ownerInfo.nickname}</p>
                      <p>책 ID {book.id}</p>
                    </div>
                    <div className={styles.innerBox}>
                      <p><strong>출판사 |</strong> {book.bookInfo.publisher}</p>
                      <p><strong>출판일 |</strong> {book.bookInfo.publicationDate}</p>
                    </div>
                    <div className={styles.innerBox}>
                      {book.tradeStatus === 'RENTAL_AVAILABLE' && <p className={styles.statusMessage}>대여가능</p>}
                      {book.tradeStatus === 'EXCHANGE_AVAILABLE' && <p className={styles.statusMessage}>교환가능</p>}
                      {book.tradeStatus === 'RENTAL_EXCHANGE_AVAILABLE' && <p className={styles.statusMessage}>대여, 교환가능</p>}
                    </div>
                  </div>
                  <div className={styles.status}>
                    <div className={styles.statusIcon}>
                      <button><CiHeart size={'35px'}/></button>
                    </div>
                    <div className={styles.statusDong}>
                      <button className={styles.region}>
                        <FiMapPin  />{areaNames[book.areaCode] || '로딩 중...'}
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p>검색 결과가 없습니다.</p>
          )
        )}
      </main>
      <div className={styles.pagination}>
        <button 
          onClick={() => handlePageChange(currentPage - 1)} 
          disabled={currentPage === 0}
        >
          이전
        </button>
        
        {Array.from({ length: totalPages }, (_, index) => (
          <button
            key={index}
            onClick={() => handlePageChange(index)}
            disabled={currentPage === index}
          >
            {index + 1}
          </button>
        ))}
        
        <button 
          onClick={() => handlePageChange(currentPage + 1)} 
          disabled={currentPage === totalPages - 1}
        >
          다음
        </button>
      </div>
      {selectedBook && <BookModal book={selectedBook} onClose={closeModal} />}
    </>
  )
}

export default BookList;