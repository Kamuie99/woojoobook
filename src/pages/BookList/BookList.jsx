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
import './BookList.css';

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

  const fetchBooks = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = { keyword: searchTerm };
      if (selectedAreaCode) {
        params.areaCodeList = selectedAreaCode;
      }
      console.log(selectedAreaCode)
      const response = await axiosInstance.get('/userbooks', { params });
      console.log("API 응답:", response.data.content);
      setBooks(response.data.content);
      return response.data.content;
    } catch (err) {
      setError('힝 실패했음...')
      console.error('Error fetching Books: ', err)
    } finally {
      setLoading(false);
    }
  }, [searchTerm, selectedAreaCode]);

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
  }, [fetchBooks]);

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
    setBooks([]); // 지역이 변경되면 기존 책 목록을 초기화
    const areaName = await fetchAreaName(areaCode);
    setUserAreaName(areaName);
  };

  const openModal = (book) => {
    setSelectedBook(book);
  };

  const closeModal = () => {
    setSelectedBook(null);
    fetchBooks(); // 모달이 닫힐 때 책 목록을 다시 불러옴
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
      <div className="titleAndSearch">
        <div className="titleDiv">
          <LuBookPlus /> 우주 도서 ({userAreaName || '지역 정보 로딩 중...'})
        </div>
      </div>
      <div className="areaSelectorContainer">
        <div>
          <AreaSelector onAreaSelected={handleAreaSelected} />
        </div>
        <div className="bookSearchBox">
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
      <main className="BookListMain">
        {loading && <p>로딩중...</p>}
        {error && <p>{error}</p>}
        {!loading && !error && (
          books.length > 0 ? (
            <div className="booksList">
              {books.map((book) => (
                <div key={book.id} className="bookItem">
                  <div className="bookImageContainer">
                    <img 
                      src={getQualityEmoticon(book.qualityStatus)} 
                      alt={book.qualityStatus} 
                      className="qualityEmoticon"
                    />
                    <img 
                      src={book.bookInfo.thumbnail} 
                      alt={book.bookInfo.title} 
                      style={{ width: '100px' }} 
                    />
                  </div>
                  <div className="bookDescription" onClick={() => openModal(book)}>
                    <h3>{book.bookInfo.title}</h3>
                    <div className="liInnerBox">
                      <p><strong>저자 |</strong> {truncateAuthor(book.bookInfo.author)}</p>
                      <p><strong>책권자 |</strong> {book.ownerInfo.nickname}</p>
                      <p>책 ID {book.id}</p>
                    </div>
                    <div className="liInnerBox">
                      <p><strong>출판사 |</strong> {book.bookInfo.publisher}</p>
                      <p><strong>출판일 |</strong> {book.bookInfo.publicationDate}</p>
                    </div>
                    <div className="liInnerBox">
                      {book.tradeStatus === 'RENTAL_AVAILABLE' && <p className="statusMessage">대여가능</p>}
                      {book.tradeStatus === 'EXCHANGE_AVAILABLE' && <p className="statusMessage">교환가능</p>}
                      {book.tradeStatus === 'RENTAL_EXCHANGE_AVAILABLE' && <p className="statusMessage">대여, 교환가능</p>}
                    </div>
                  </div>
                  <div className="bookStatus">
                    <div className="bookStatusIcon">
                      <button><CiHeart size={'35px'}/></button>
                    </div>
                    <div className="bookStatusDong">
                      <button className="bookregion">
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
      {selectedBook && <BookModal book={selectedBook} onClose={closeModal} />}
    </>
  )
}

export default BookList;
