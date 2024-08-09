import React, { useState, useEffect, useContext } from "react";
import { AuthContext } from "../../contexts/AuthContext";
import { getEmotionImage } from '../../util/get-emotion-image';
import { HiOutlinePencilSquare } from "react-icons/hi2";
import { IoHeartSharp } from "react-icons/io5";
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
      title: "반납하시겠습니까?",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "반납",
      cancelButtonText: "취소"
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const response = await axiosInstance.put(`/userbooks/${bookId}/return`)
          console.log(response.data)
          if (response.status === 200) {
            Swal.fire({
              title: "반납 완료",
              text: "성공적으로 반납 되었습니다.",
              icon: "success"
            })
          } else {
            Swal.fire({
              title: "반납 실패",
              text: "반납에 실패하였습니다.",
              icon: "error"
            })
          }
        } catch (error) {
          console.error(error);
          Swal.fire({
            title: "반납 실패",
            text: "반납에 실패하였습니다.",
            icon: "error"
          })
        }
      }
    })
    const tradeStatus = localStorage.getItem('tradeStatus')
    console.log(tradeStatus)
    fetchRegisteredUserbooks(tradeStatus, true)
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