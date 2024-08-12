import { useState, useEffect, useCallback, useContext } from "react";
import { useSearch } from '../../contexts/SearchContext';
import { Link } from 'react-router-dom';

import { LuBookPlus } from "react-icons/lu";
import { IoHeartOutline, IoHeartSharp } from "react-icons/io5";
import { getEmotionImage } from '../../util/get-emotion-image';
import { AuthContext } from '../../contexts/AuthContext';
import { FiMapPin } from "react-icons/fi";
import { RiMenuSearchLine } from "react-icons/ri";
import { BsPeople } from "react-icons/bs";
import Header from "../../components/Header"
import axiosInstance from './../../util/axiosConfig';
import AreaSelector from "../../components/AreaSelector";
import BookModal from './BookModal';
import Swal from 'sweetalert2'
import styles from './BookList.module.css';

// eslint-disable-next-line react/prop-types
const BookList = ({setDirectMessage}) => {
  const { searchTerm: initialSearchTerm } = useSearch();
  const [searchTerm, setSearchTerm] = useState(initialSearchTerm);
  const [inputValue, setInputValue] = useState(initialSearchTerm || '');
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [areaNames, setAreaNames] = useState({});
  const [selectedArea, setSelectedArea] = useState(null);
  const { user, sub: userId, client } = useContext(AuthContext);
  const [selectedBook, setSelectedBook] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [userAreaName, setUserAreaName] = useState('');
  const [onlineUsers, setOnlineUsers] = useState([]);
  const [showOnlineUsers, setShowOnlineUsers] = useState(false);

  useEffect(() => {
    fetchUserInfo();
  }, []);

  useEffect(() => {
    if (selectedArea && selectedArea.areaCode) {
      const destination = `/topic/area:${selectedArea.areaCode}`;
      client.current.subscribe(destination, (message) => {
        // console.log('수신된 메시지:', message.body);
        const messageBody = JSON.parse(message.body);
        // console.log(messageBody);
        setOnlineUsers(messageBody);
      });
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedArea]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (showOnlineUsers && !event.target.closest(`.${styles.onlineUsersWrapper}`)) {
        setShowOnlineUsers(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [showOnlineUsers]);

  const fetchUserInfo = async () => {
    try {
      setIsLoading(true);
      const userResponse = await axiosInstance.get('/users');
      const userData = userResponse.data;

      const areaResponse = await axiosInstance.get(`/area?areaCode=${userData.areaCode}`);
      const areaData = areaResponse.data;
      setSelectedArea({
        siName: areaData.siName,
        guName: areaData.guName,
        dongName: areaData.dongName,
        areaCode: userData.areaCode
      });
      setUserAreaName(areaData.dongName);
    } catch (error) {
      console.error("Error fetching user info:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleChatOpen = (ownerId) => {
    setDirectMessage(ownerId);
  }

  const fetchBooks = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = { 
        keyword: searchTerm,
        page: currentPage,
        size: 10
      };
      if (selectedArea && selectedArea.areaCode) {
        params.areaCodeList = selectedArea.areaCode;
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
  }, [searchTerm, selectedArea, currentPage]);

  useEffect(() => {
    if (selectedArea) {
      fetchBooks().then(fetchedBooks => {
        if (fetchedBooks) {
          fetchAreaNames(fetchedBooks);
        }
      });
    }
  }, [fetchBooks, currentPage, selectedArea]);

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
      axiosInstance.get('/area', { params: { areaCode: book.userbook.areaCode } })
    );
    
    try {
      const areaResponses = await Promise.all(areaPromises);
      const newAreaNames = {};
      areaResponses.forEach((response, index) => {
        newAreaNames[books[index].userbook.areaCode] = response.data.dongName;
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

  const handleAreaSelected = (area) => {
    setSelectedArea(area);
    setUserAreaName(area ? area.dongName : '');
    setBooks([]);
    setCurrentPage(0);
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
  
  const toggleWish = async (bookId, wished) => {
    if (wished) {
      const result = await Swal.fire({
        title: "관심 등록을 취소하시겠습니까?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#d33",
        cancelButtonColor: "#3085d6",
        confirmButtonText: "관심 해제",
        cancelButtonText: "취소"
      });
  
      if (!result.isConfirmed) {
        return;
      }
    }
    try {
      await axiosInstance.post(`/userbooks/${bookId}/wish`, {userId, wished})
      setBooks(prev => prev.map(book =>
        book.userbook.id === bookId ? {...book, like: !book.like } : book
      ));
    } catch (err) {
      console.error('toggle wish 에러 :', err);
    }
  }

  const toggleOnlineUsers = () => {
    setShowOnlineUsers(!showOnlineUsers);
  };

  return (
    <>
      <Header />
      <div className={styles.titleAndSearch}>
        <div className={styles.titleDiv}>
          <LuBookPlus /> 우주 도서 ({userAreaName || '지역 정보 로딩 중...'})
          <div className={styles.onlineUsersWrapper}>
            <BsPeople onClick={toggleOnlineUsers} className={styles.peopleIcon} />
            {showOnlineUsers && (
              <div className={styles.onlineUsersPopup}>
                <p className={styles.onlineUserTitle}>현재 온라인 유저 ({onlineUsers.length})</p>
                {onlineUsers.length > 0 ? (
                  onlineUsers.map((user, index) => (
                    <Link to={`/${user.id}/mylibrary`} key={index} className={styles.onlineUserLink}>
                      <span className={styles.onlineIndicator}></span>
                      {user.nickname}
                    </Link>
                  ))
                ) : (
                  <p>현재 온라인인 유저가 없습니다.</p>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
      {isLoading ? (
        <div>로딩 중...</div>
      ) : (
        <div className={styles.areaSelectorContainer}>
          <div>
            <AreaSelector onAreaSelected={handleAreaSelected} initialArea={selectedArea} />
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
            <button className={styles.searchButton} onClick={handleSearch}>
              <RiMenuSearchLine size={'35px'} />
            </button>
          </div>  
        </div>
      )}
      <main className={styles.main}>
        {loading && <p>로딩중...</p>}
        {error && <p>{error}</p>}
        {!loading && !error && (
          books.length > 0 ? (
            <div className={styles.list}>
              {books.map((book) => (
                <div key={book.userbook.id} className={styles.item}>
                  <div className={styles.imageContainer}>
                    <img 
                      src={getQualityEmoticon(book.userbook.qualityStatus)} 
                      alt={book.userbook.qualityStatus} 
                      className={styles.qualityEmoticon}
                    />
                    <img 
                      src={book.userbook.bookInfo.thumbnail} 
                      alt={book.userbook.bookInfo.title} 
                      style={{ width: '100px', height: '140px' }}
                      className={styles.thumbnail}
                      onClick={() => openModal(book)}
                    />
                  </div>
                  <div className={styles.description}>
                    <h3 onClick={() => openModal(book)} >{book.userbook.bookInfo.title}</h3>
                    <div className={styles.innerBox}>
                      <p><strong>저자 |</strong> {truncateAuthor(book.userbook.bookInfo.author)}</p>
                      <p className={styles.ownerNickname}>
                        <strong>책권자 |</strong> {book.userbook.ownerInfo.nickname}
                      </p>
                      <p>책 ID {book.userbook.id}</p>
                    </div>
                    <div className={styles.innerBox}>
                      <p><strong>출판사 |</strong> {book.userbook.bookInfo.publisher}</p>
                      <p><strong>출판일 |</strong> {book.userbook.bookInfo.publicationDate}</p>
                    </div>
                    <div className={styles.innerBox}>
                      {book.userbook.tradeStatus === 'RENTAL_AVAILABLE' && <p className={styles.statusMessage}>대여가능</p>}
                      {book.userbook.tradeStatus === 'EXCHANGE_AVAILABLE' && <p className={styles.statusMessage}>교환가능</p>}
                      {book.userbook.tradeStatus === 'RENTAL_EXCHANGE_AVAILABLE' && <p className={styles.statusMessage}>대여, 교환가능</p>}
                      {book.userbook.tradeStatus === 'RENTED' && <p className={styles.statusMessage2}>대여중</p>}
                    </div>
                  </div>
                  <div className={styles.status}>
                    <div className={styles.statusIcon}>
                      <button
                        onClick={() => toggleWish(book.userbook.id, book.like)}
                        style={{ color: 'var(--contrast-color)' }}
                        className={`${styles.heartIcon} ${book.like ? styles.liked : ''}`}
                        >
                        {book.like ? <IoHeartSharp size={'30px'} /> : <IoHeartOutline size={'30px'}/>}
                      </button>
                    </div>
                    <div className={styles.statusDong}>
                      <button className={styles.region}>
                        <FiMapPin  />{areaNames[book.userbook.areaCode] || '로딩 중...'}
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
      {totalPages > 1 && (
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
      )}
      {selectedBook && <BookModal book={selectedBook} onClose={closeModal} onChatOpen={handleChatOpen} />}
      </>
  )
}

export default BookList;