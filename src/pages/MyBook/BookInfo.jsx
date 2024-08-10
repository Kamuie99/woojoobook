import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../../contexts/AuthContext";
import { getEmotionImage } from '../../util/get-emotion-image';
import { HiOutlinePencilSquare } from "react-icons/hi2";
import { IoHeartSharp, IoTrashOutline  } from "react-icons/io5";
import Swal from 'sweetalert2'
import styles from './BookInfo.module.css';
import axiosInstance from "../../util/axiosConfig";
import BookStatusChangeModal from "./BookStatusChangeModal";
import BookModal from '../MyLibrary/BookModal'

const BookInfo = ({ item, onWishChange, fetchRegisteredUserbooks }) => {
  const { sub: userId } = useContext(AuthContext);
  const [open, setOpen] = useState(false);
  const [bookInfo, setBookInfo] = useState(item);
  const [editable, setEditable] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [bookDetail, setBookDetail] = useState('');

  useEffect(() => {
    if (localStorage.getItem('MyBookContent') === 'registered') {
      setEditable(true);
    };
  }, []);

  const showAlert = (title, text, icon) => {
    Swal.fire({
      title: title,
      text: text,
      icon: icon
    });
  }
  

  const renderRegisterType = (registerType) => {
    const typeMap = {
      'RENTAL': '대여',
      'EXCHANGE': '교환',
      'RENTAL_EXCHANGE': '대여, 교환',
      'UNAVAILABLE': '거래 불가능'
    };
    return typeMap[registerType] || registerType;
  };

  const renderTradeStatus = (tradeStatus) => {
    const statusMap = {
      'RENTAL_EXCHANGE_AVAILABLE': '대여, 교환 가능',
      'RENTAL_AVAILABLE': '대여 가능',
      'RENTED': '대여중',
      'EXCHANGE_AVAILABLE': '교환 가능',
      'EXCHANGE': '교환 완료',
      'UNAVAILABLE': '거래 불가능'
    };
    return statusMap[tradeStatus] || tradeStatus;
  };

  const fetchEmotionImage = (qualityStatus) => {
    const qualityMap = {
      'VERY_GOOD': 1,
      'GOOD': 2,
      'NORMAL': 3,
      'BAD': 4,
      'VERY_BAD': 5,
    };
    return getEmotionImage(qualityMap[qualityStatus]) || null;
  }

  const openBookModal = async (book) => {
    try {
      const response = await axiosInstance.get(`books?keyword=${book.isbn}&page=1`);
      setBookDetail(response.data.bookList[0]);
      setShowModal(true);
    } catch (error) {
      console.error('Error fetching book details:', error);
    }
  };

  const openModal = () => {
    setOpen(true);
  }

  const closeModal = () => {
    setOpen(false);
  }

  const deleteUserbook = async (book) => {
    if (book.tradeStatus === 'RENTED') {
      showAlert('삭제 불가능', '대여중인 도서는 삭제할 수 없습니다.', 'error');
      return;
    }
    Swal.fire({
      title: "삭제하시겠습니까?",
      text: "삭제한 데이터는 되돌릴 수 없습니다.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "삭제",
      cancelButtonText: "취소"
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const response = await axiosInstance.put(`/userbooks/${book.id}`, {
            canRent: false, canExchange: false, quality: item.qualityStatus,
          });
          if (response.status === 200) {
            showAlert('삭제 완료', '도서가 성공적으로 삭제되었습니다.', 'success')            
            fetchRegisteredUserbooks(localStorage.getItem('tradeStatus'), true);
          } else {
            showAlert('삭제 실패', '도서 삭제에 실패하였습니다.', 'error')
          }
        } catch (error) {
          console.error(error);
          showAlert('삭제 실패', '도서 삭제에 실패하였습니다.', 'error')
        }
      }
    })
  }

  const handleSave = async (canRent, canExchange, quality) => {
    const tradeStatus = localStorage.getItem('tradeStatus');
    const newRegisterType =
      canRent && canExchange ? 'RENTAL_EXCHANGE' :
      canRent ? 'RENTAL' : canExchange ? 'EXCHANGE' : 'UNAVAILABLE';
    const newTradeStatus =
      canRent && canExchange? 'RENTAL_EXCHANGE_AVAILABLE' :
      canRent ? 'RENTAL_AVAILABLE' : canExchange ? 'EXCHANGE_AVAILABLE' : 'UNAVAILABLE';

    try {
      const response = await axiosInstance.put(`/userbooks/${item.id}`, {
        canRent, canExchange, quality,
      });
      setBookInfo((prev) => ({
        ...prev,
        qualityStatus: quality,
        registerType: newRegisterType,
        tradeStatus: newTradeStatus
      }));
      if (response.status === 200) {
        Swal.fire({
          title: "상태 변경 성공",
          text: "상태가 성공적으로 변경되었습니다.",
          icon: "success"
        })
      }
      fetchRegisteredUserbooks(tradeStatus, true);
      closeModal();
    } catch (error) {
      console.error(error);
      if (error.response.status === 400) {
        Swal.fire({
          title: "상태 변경 실패",
          text: "대여중인 책은 상태 변경이 불가능합니다.",
          icon: "error",
          confirmButtonColor: "#3085d6"
        }).then(() => {
          fetchRegisteredUserbooks(tradeStatus, true);
          closeModal();
        });
      }
    }
  }

  const giveBack = async (bookId) => {
    Swal.fire({
      title: "확실히 받납 받으셨나요?",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "네",
      cancelButtonText: "아니요"
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const response = await axiosInstance.put(`/userbooks/${bookId}/return`)
          console.log(response.data)
          if (response.status === 200) {
            showAlert('반납 완료', '성공적으로 반납 되었습니다.', 'success')
            fetchRegisteredUserbooks(localStorage.getItem('tradeStatus'), true)
          } else {
            showAlert('반납 실패', '반납에 실패하였습니다.', 'error')
          }
        } catch (error) {
          console.error(error);
          showAlert('반납 실패', '반납에 실패하였습니다.', 'error')
        }
      }
    })
  }

  const removeWish = async (bookId, wished = true) => {
    Swal.fire({
      title: "관심 등록을 취소하시겠습니까?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "관심 해제",
      cancelButtonText: "취소"
    }).then(async (result) =>{
      if (result.isConfirmed) {
        try {
          await axiosInstance.post(`/userbooks/${bookId}/wish`, {userId, wished})
          Swal.fire({
            title: "관심 등록이 해제되었습니다.",
            icon: "success"
          })
          onWishChange(bookId);
        } catch (err) {
          console.error('관심 목록 삭제 요청 실패 :', err);
        }
      }
    })
  }

  return (
    <div className={styles.item}>
      <div className={styles.imageContainer}>
        <img
          src={fetchEmotionImage(bookInfo.qualityStatus)}
          alt={bookInfo.qualityStatus}
          className={styles.qualityEmoticon}
        />
        <img
          src={item.bookInfo.thumbnail}
          alt={item.bookInfo.title}
          className={styles.thumbnail}
          style={{ width: '100px', height: '140px' }}
          onClick={() => openBookModal(item.bookInfo)}
        />
      </div>
      <div className={styles.bookInfo}>
        <h3 className={styles.bookTitle} onClick={() => openBookModal(item.bookInfo)}
        >{item.bookInfo.title}</h3>
        <h4><strong>등록 상태 |</strong> {renderRegisterType(bookInfo.registerType)}</h4>
        <h4><strong>현재 상태 |</strong> {renderTradeStatus(bookInfo.tradeStatus)}</h4>
      </div>
      <div className={styles.status}>
        {editable && bookInfo.tradeStatus === "RENTED" && (
          <div className={styles.giveBackSubmitButton} onClick={() => giveBack(item.id)}>
            반납 확인
          </div>
        )}
        {!editable && (
          <div className={styles.statusIcon}>
            <button className={styles.likedButton} onClick={() => removeWish(item.id)}>
              <IoHeartSharp size={'35px'}/>
            </button>
          </div>
        )}
        {editable && (
          <HiOutlinePencilSquare
            onClick={openModal}
            className={styles.bookStatusEdit}
            size={30}
          />
        )}
        <BookStatusChangeModal
          isOpen={open}
          onClose={closeModal}
          registerType={bookInfo.registerType}
          qualityStatus={bookInfo.qualityStatus}
          handleSave={handleSave}
        />
        {editable && bookInfo.tradeStatus !== 'RENTED' && (
          <IoTrashOutline
            onClick={() => deleteUserbook(item)}
            className={styles.deleteUserbook}
            size={30}
          />
        )}
        {showModal && bookDetail && (
          <BookModal
            book={bookDetail}
            onClose={() => setShowModal(false)}
          />
        )}
      </div>
    </div>
  );
}

export default BookInfo;