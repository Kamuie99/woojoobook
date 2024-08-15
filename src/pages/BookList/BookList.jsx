import { useState, useEffect, useCallback, useContext, useRef } from "react";
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

const BookList = ({setDirectMessage}) => {
  const { searchTerm: initialSearchTerm } = useSearch();
  const [searchTerm, setSearchTerm] = useState(initialSearchTerm);
  const [inputValue, setInputValue] = useState(initialSearchTerm || '');
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selectedArea, setSelectedArea] = useState(null);
  const { user, sub: userId, client, isConnected } = useContext(AuthContext);
  const [selectedBook, setSelectedBook] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [userAreaName, setUserAreaName] = useState('');
  const [onlineUsers, setOnlineUsers] = useState([]);
  const [showOnlineUsers, setShowOnlineUsers] = useState(false);
  const [lastUserbook, setLastUserBook] = useState(null);
  const [isBottomVisible, setIsBottomVisible] = useState(false);
  const mainRef = useRef(null);

  useEffect(() => {
    fetchUserInfo();
  }, []);

  useEffect(() => {
    let subscription;
    if (!isConnected) {
      return;
    }

    if (selectedArea && selectedArea.areaCode) {
      const destination = `/topic/area:${selectedArea.areaCode}`;
      
      subscription = client.current.subscribe(destination, (message) => {
        const messageBody = JSON.parse(message.body);
        setOnlineUsers([]);
        setTimeout(() => {
          setOnlineUsers(messageBody);
        }, 10);
      });
    }
  
    return () => {
      if (subscription) {
        subscription.unsubscribe();
      }
    };
  }, [selectedArea]);

  const fetchOnlineUsers = async (areaCode) => {
    try {
      const response = await axiosInstance.get(`usersOn/${areaCode}`);
      setOnlineUsers([]);
      setTimeout(() => {
        setOnlineUsers(response.data)
      }, 10);
    } catch (err) {
      console.error(err)
    }
  }

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

  const fetchBooks = useCallback(async (searchTerm, userbookId = null, init = true) => {
    setLoading(true);
    setError(null);
    try {
      const prevScrollTop = mainRef.current ? mainRef.current.scrollTop : 0;

      const params = { 
        keyword: searchTerm,
        userbookId,
      };
      if (selectedArea && selectedArea.areaCode) {
        params.areaCode = selectedArea.areaCode;
      }
      const response = await axiosInstance.get('/userbooks', { params });
      if (init) {
        setBooks(response.data);
      } else {
        setBooks(prev => [...prev,...response.data]);
        setTimeout(() => {
          if (mainRef.current) {
            mainRef.current.scrollTop = prevScrollTop
          }
        }, 0);
      }
      return response.data.content;
    } catch (err) {
      setError('로딩중 ..')
      console.error('Error fetching Books: ', err)
    } finally {
      setLoading(false);
    }
  }, [selectedArea]);

  useEffect(() => {
    if (selectedArea) {
      fetchBooks(searchTerm)
    }
  }, [selectedArea]);

  useEffect(() => {
    if (books) {
      setLastUserBook(books[books.length - 1]);
    }
  }, [books])

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
    const handleScroll = () => {
      if (mainRef.current) {
        const { scrollTop, scrollHeight, clientHeight } = mainRef.current;
        const isBottom = scrollTop + clientHeight >= scrollHeight - 520;
        setIsBottomVisible(isBottom);
      }
    };

    const mainElement = mainRef.current;
    if (mainElement) {
      mainElement.addEventListener('scroll', handleScroll);
    }

    return () => {
      if (mainElement) {
        mainElement.removeEventListener('scroll', handleScroll);
      }
    };
  }, []);

  useEffect(() => {
    if (isBottomVisible) {
      fetchBooks(searchTerm, lastUserbook.userbookid, false)
    }
  }, [isBottomVisible]);

  useEffect(() => {
    fetchBooks(searchTerm)
  }, [searchTerm])

  const handleSearch = () => {
    setSearchTerm(inputValue);
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
  };

  const openModal = (book) => {
    setSelectedBook(book);
  };

  const closeModal = () => {
    setSelectedBook(null);
    fetchBooks(searchTerm);
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
        book.userbookid === bookId ? {...book, likeStatus: !book.likeStatus } : book
      ));
    } catch (err) {
      console.error('toggle wish 에러 :', err);
    }
  }

  const toggleOnlineUsers = () => {
    fetchOnlineUsers(selectedArea.areaCode);
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
                <p className={styles.onlineUserTitle}>
                  <strong>{userAreaName}</strong> <br/>현재 온라인 유저 ({onlineUsers.length})
                </p>
                {onlineUsers.length > 0 ? (
                  onlineUsers.map((user) => (
                    <Link to={`/${user.id}/mylibrary`} key={user.id} className={styles.onlineUserLink}>
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
      <main className={styles.main} ref={mainRef}>
        {loading && <p>로딩중...</p>}
        {error && <p>{error}</p>}
        {!loading && !error && (
          books.length > 0 ? (
            <div className={styles.list}>
              {books.map((book, index) => (
                <div key={index} className={styles.item}>
                  <div className={styles.imageContainer}>
                    <img 
                      src={getQualityEmoticon(book.qualityStatus)} 
                      alt={book.qualityStatus} 
                      className={styles.qualityEmoticon}
                    />
                    <img 
                      src={book.book.thumbnail} 
                      alt={book.book.title} 
                      style={{ width: '100px', height: '140px' }}
                      className={styles.thumbnail}
                      onClick={() => openModal(book)}
                    />
                  </div>
                  <div className={styles.description}>
                    <h3 onClick={() => openModal(book)} >{book.book.title}</h3>
                    <div className={styles.innerBox}>
                      <p><strong>저자 |</strong> {truncateAuthor(book.book.author)}</p>
                      <p className={styles.ownerNickname}>
                        <strong>책권자 |</strong> {book.user.nickname}
                      </p>
                      <p>책 ID {book.userbookid}</p>
                    </div>
                    <div className={styles.innerBox}>
                      <p><strong>출판사 |</strong> {book.book.publisher}</p>
                      <p><strong>출판일 |</strong> {book.book.publicationDate}</p>
                    </div>
                    <div className={styles.innerBox}>
                      {book.tradeStatus === 'RENTAL_AVAILABLE' && <p className={styles.statusMessage}>대여가능</p>}
                      {book.tradeStatus === 'EXCHANGE_AVAILABLE' && <p className={styles.statusMessage}>교환가능</p>}
                      {book.tradeStatus === 'RENTAL_EXCHANGE_AVAILABLE' && <p className={styles.statusMessage}>대여, 교환가능</p>}
                      {book.tradeStatus === 'RENTED' && <p className={styles.statusMessage2}>대여중</p>}
                    </div>
                  </div>
                  <div className={styles.status}>
                    <div className={styles.statusIcon}>
                      <button
                        onClick={() => toggleWish(book.userbookid, book.likeStatus)}
                        style={{ color: 'var(--contrast-color)' }}
                        className={`${styles.heartIcon} ${book.likeStatus ? styles.liked : ''}`}
                        >
                        {book.likeStatus ? <IoHeartSharp size={'30px'} /> : <IoHeartOutline size={'30px'}/>}
                      </button>
                    </div>
                    <div className={styles.statusDong}>
                      <button className={styles.region}>
                        <FiMapPin  />{userAreaName || '로딩 중...'}
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
      {selectedBook && <BookModal book={selectedBook} onClose={closeModal} onChatOpen={handleChatOpen} />}
      </>
  )
}

export default BookList;