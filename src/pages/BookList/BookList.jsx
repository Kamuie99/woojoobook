import { useState, useEffect } from "react";
import Header from "../../components/Header"
import { useSearch } from '../../contexts/SearchContext';
import axiosInstance from './../../util/axiosConfig';
import './BookList.css';
import { LuBookPlus } from "react-icons/lu";
import { CiHeart } from "react-icons/ci";
import { getEmotionImage } from '../../util/get-emotion-image';

const BookList = () => {
  const { searchTerm: initialSearchTerm  } = useSearch();
  const [searchTerm, setSearchTerm] = useState(initialSearchTerm);
  const [inputValue, setInputValue] = useState(initialSearchTerm || '');
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [areaNames, setAreaNames] = useState({});

  useEffect(() => {
    const fetchBooks = async() => {
      setLoading(true);
      setError(null);
      try {
        const response = await axiosInstance.get('/userbooks', {
          params: {keyword: searchTerm}
        });
        console.log("API 응답:", response.data.content);
        setBooks(response.data.content);
        fetchAreaNames(response.data.content);
      } catch (err) {
        setError('힝 실패했음...')
        console.error('Error fetching Books: ', err)
      } finally {
        setLoading(false);
      }
    };

    fetchBooks();
  }, [searchTerm]);

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

  return (
    <>
      <Header />
      <div className="titleAndSearch">
        <div className="titleDiv">
          <LuBookPlus /> 대여 가능한 책 목록
        </div>
        <div className="bookSearchBox">
          <input
            type="text"
            value={inputValue}
            onChange={handleInputChange}
            onKeyPress={handleKeyPress}
            placeholder="책 제목 또는 저자를 검색"
            style={{ marginRight: '10px', padding: '5px' }}
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
                  <div className="bookDescription">
                    <h3>{book.bookInfo.title}</h3>
                    <div className="liInnerBox">
                      <p><strong>저자 |</strong> {book.bookInfo.author}</p>
                      <p><strong>책권자 |</strong> {book.ownerInfo.nickname}</p>
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
                      <button>
                        {areaNames[book.areaCode] || '로딩 중...'}
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
    </>
  )
}

export default BookList;