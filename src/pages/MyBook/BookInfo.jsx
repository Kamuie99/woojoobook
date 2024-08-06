import React, { useState, useEffect } from "react";
import { getEmotionImage } from '../../util/get-emotion-image';
import { HiOutlinePencilSquare } from "react-icons/hi2";
import { CiHeart } from "react-icons/ci";
import styles from './BookInfo.module.css';
import axiosInstance from "../../util/axiosConfig";
import BookStatusChangeModal from "./BookStatusChangeModal";

const BookInfo = ({ item }) => {
  const [open, setOpen] = useState(false);
  const [bookInfo, setBookInfo] = useState(item);
  const [editable, setEditable] = useState(false);

  useEffect(() => {
    if (localStorage.getItem('myBookActiveContent') === 'registered') {
      setEditable(true);
    };
  }, []);

  const renderRegisterType = (registerType) => {
    const typeMap = {
      'RENTAL': '대여',
      'EXCHANGE': '교환',
      'RENTAL_EXCHANGE': '대여, 교환'
    };
    return typeMap[registerType] || registerType;
  };

  const renderTradeStatus = (tradeStatus) => {
    const statusMap = {
      'RENTAL_AVAILABLE': '대여 가능',
      'EXCHANGE_AVAILABLE': '교환 가능',
      'RENTAL_EXCHANGE_AVAILABLE': '대여, 교환 가능'
    };
    return statusMap[tradeStatus] || tradeStatus;
  };

  const fetchEmotionImage = (qualityStatus) => {
    switch (qualityStatus) {
      case 'VERY_GOOD':
        return getEmotionImage(1);
      case 'GOOD':
        return getEmotionImage(2);
      case 'NORMAL':
        return getEmotionImage(3);
      case 'BAD':
        return getEmotionImage(4);
      case 'VERY_BAD':
        return getEmotionImage(5);
      default:
        return null;
    }
  }

  const toggleModal = () => {
    setOpen(prev => !prev);
  }

  const closeModal = () => {
    setOpen(false);
  }

  const handleSubmitBookStatusChange = async (canRent, canExchange, quality) => {
    const newRegisterType =
      canRent && canExchange ? 'RENTAL_EXCHANGE' :
      canRent ? 'RENTAL' : canExchange ? 'EXCHANGE' : '';
    const newTradeStatus =
      canRent && canExchange? 'RENTAL_EXCHANGE_AVAILABLE' :
      canRent ? 'RENTAL_AVAILABLE' : canExchange ? 'EXCHANGE_AVAILABLE' : '';

    try {
      await axiosInstance.put(`/userbooks/${item.id}`, {
        canRent, canExchange, quality
      });
      setBookInfo((prev) => ({
        ...prev,
        qualityStatus: quality,
        registerType: newRegisterType,
        tradeStatus: newTradeStatus
      }));
      closeModal();
    } catch (error) {
      console.error(error);
    }
  }

  const giveBack = async () => {
    console.log(1)
    // TODO: rentalId가 필요함
    try {
      // const response = await axiosInstance.put(`/rentals/${rentalId}/return`)
      const response = await axiosInstance.get(`/rentals`, {
        params: {
          page: 0,
          size: 10
        }
      });
      console.log(response.data)
    } catch (error) {
      console.error(error);
    }
  }

  return (
    <div className={styles.bookInfo}>
      <div className={styles.imageContainer}>
        <img
          src={fetchEmotionImage(bookInfo.qualityStatus)}
          alt={bookInfo.qualityStatus}
          className={styles.qualityEmoticon}
        />
        <img
          src={item.bookInfo.thumbnail}
          alt={item.bookInfo.title}
          style={{ width: '100px', height: '140px' }}
        />
      </div>
      <div className={styles.bookInfoDetail}>
        <h3>{item.bookInfo.title}</h3>
        <h4><strong>등록 상태 |</strong> {renderRegisterType(bookInfo.registerType)}</h4>
        <h4><strong>현재 상태 |</strong> {renderTradeStatus(bookInfo.tradeStatus)}</h4>
        {editable && (
          <div className={styles.giveBackSubmitButton} onClick={() => giveBack()}>
            반납 확인
          </div>
        )}
        {!editable && (
          <div className={styles.statusIcon}>
            <button className={styles.wishButton}><CiHeart size={'35px'}/></button>
          </div>
        )}
        {editable && (
          <HiOutlinePencilSquare
            onClick={toggleModal}
            className={styles.bookStatusEdit}
            size={30}
          />
        )}
        <BookStatusChangeModal
          isOpen={open}
          onClose={closeModal}
          registerType={bookInfo.registerType}
          qualityStatus={bookInfo.qualityStatus}
          handleSubmitBookStatusChange={handleSubmitBookStatusChange}
        />
      </div>
    </div>
  );
}

export default BookInfo;