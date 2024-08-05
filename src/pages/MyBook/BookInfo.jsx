import React, { useState, useEffect } from "react";
import { HiOutlinePencilSquare } from "react-icons/hi2";
import { getEmotionImage } from '../../util/get-emotion-image';
import styles from './BookInfo.module.css';
import axiosInstance from "../../util/axiosConfig";
import BookStatusChangeModal from "./BookStatusChangeModal";

const BookInfo = ({ item }) => {
  const [open, setOpen] = useState(false);
  const [qualityStatus, setQualityStatus] = useState(item.qualityStatus);
  const [registerType, setRegisterType] = useState(item.registerType);
  const [tradeStatus, setTradeStatus] = useState(item.tradeStatus);
  const [bookInfo, setBookInfo] = useState(item);

  const renderRegisterType = (registerType) => {
    switch (registerType) {
      case 'RENTAL':
        return '대여';
      case 'EXCHANGE':
        return '교환';
      case 'RENTAL_EXCHANGE':
        return '대여, 교환';
      default:
        return registerType;
    }
  };

  const renderTradeStatus = (tradeStatus) => {
    switch (tradeStatus) {
      case 'RENTAL_AVAILABLE':
        return '대여 가능'
      case 'EXCHANGE_AVAILABLE':
        return '교환 가능'
      case 'RENTAL_EXCHANGE_AVAILABLE':
        return '대여, 교환 가능'
      default:
        return tradeStatus;
    }
  }

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
      canRent ? 'RENTAL_AVAILABLE' :
      canExchange ? 'EXCHANGE_AVAILABLE' : 'RENTAL_EXCHANGE_AVAILABLE';

    try {
      const response = await axiosInstance.put(`/userbooks/${item.id}`, {
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

  return (
    <div className={styles.bookInfo}>
      <img src={item.bookInfo.thumbnail} alt=""/>
      <div className={styles.bookInfoDetail}>
        <div>{item.bookInfo.title}</div>
        <div>등록 목적: {renderRegisterType(bookInfo.registerType)}</div>
        <div>상태: {renderTradeStatus(bookInfo.tradeStatus)}</div>
        <div>반납 확인</div>
        <div className={styles.bookStatus}>
          <img src={fetchEmotionImage(bookInfo.qualityStatus)} alt=""/>
        </div>
        <HiOutlinePencilSquare
          onClick={toggleModal}
          className={styles.bookStatusEdit}
          size={30}
        />
        <BookStatusChangeModal
          isOpen={open}
          onClose={closeModal}
          registerType={bookInfo.registerType}
          qualityStatus={bookInfo.qualityStatus}
          setQualityStatus={setQualityStatus}
          handleSubmitBookStatusChange={handleSubmitBookStatusChange}
        />
      </div>
    </div>
  );
}

export default BookInfo;