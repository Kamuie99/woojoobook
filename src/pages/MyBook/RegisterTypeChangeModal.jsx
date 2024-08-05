import React, { useState } from "react";
import styles from './RegisterTypeChangeModal.module.css'

const RegisterTypeChangeModal = ({ isOpen, onClose, bookId, registerType }) => {
  const [isRentable, setIsRentable] = useState(registerType === 'RENTAL');
  const [isExchangeable, setIsExchangeable] = useState(registerType === 'EXCHANGE');
  const getStatusClassName = () => {
    if (isRentable && isExchangeable) return styles.statusBoth;
    if (isRentable) return styles.statusRental;
    if (isExchangeable) return styles.statusExchange;
    return '';
  };
  return (
    <div className={styles.rentalexchangestate}>
    <label>
      등록 정보: 
      <span className={`${styles.statusText} ${getStatusClassName()}`}>
        {isRentable && "대여 가능"}
        {isRentable && isExchangeable && ", "}
        {isExchangeable && "교환 가능"}
      </span>
    </label>
    <div className={styles.checkboxGroup}>
      <label>
        <input 
          type="checkbox" 
          checked={isRentable} 
          onChange={(e) => setIsRentable(e.target.checked)} 
        />
        대여 가능 여부
      </label>
      <label>
        <input 
          type="checkbox" 
          checked={isExchangeable} 
          onChange={(e) => setIsExchangeable(e.target.checked)} 
        />
        교환 가능 여부
      </label>
    </div>
  </div>
  );
}

export default RegisterTypeChangeModal;