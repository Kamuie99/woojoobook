import React, { useState, useEffect } from "react";
import { Modal, Box, Typography, FormControlLabel, Checkbox, Button } from '@mui/material';
import { getEmotionImage } from '../../util/get-emotion-image';
import Swal from 'sweetalert2'
import styles from './BookStatusChangeModal.module.css';

const BookStatusChangeModal = ({ isOpen, onClose, registerType, qualityStatus, handleSave }) => {
  const [canRent, setCanRent] = useState(registerType === 'RENTAL');
  const [canExchange, setCanExchange] = useState(registerType === 'EXCHANGE');
  const [quality, setQuality] = useState(qualityStatus);

  useEffect(() => {
    if (isOpen) {
      setCanRent(registerType === 'RENTAL' || registerType === 'RENTAL_EXCHANGE');
      setCanExchange(registerType === 'EXCHANGE' || registerType === 'RENTAL_EXCHANGE');
      setQuality(qualityStatus);
    }
  }, [isOpen, registerType, qualityStatus]);

  const getQualityFromCondition = (condition) => {
    const qualityMap = {
      1: 'VERY_GOOD',
      2: 'GOOD',
      3: 'NORMAL',
      4: 'BAD',
      5: 'VERY_BAD'
    }
    return qualityMap[condition] || '';
  };
  
  const getQualityText = (quality) => {
    const qualityMap = {
      'VERY_GOOD': { text: '매우 좋음', color: '#65C964' },
      'GOOD': { text: '좋음', color: '#9ED672' },
      'NORMAL': { text: '보통', color: '#FCCE18' },
      'BAD': { text: '나쁨', color: '#FE8447' },
      'VERY_BAD': { text: '매우 나쁨', color: '#FD565F' }
    }
    return qualityMap[quality] || { text: '', color: '' };
  };

  const onSave = () => {
    if (!canRent && !canExchange) {
      Swal.fire({
        title: '대여, 교환 여부가 선택되지 않았습니다.',
        confirmButtonText: '확인',
        icon: 'info'
      })
      return;
    }
    Swal.fire({
      title: "상태를 변경하시겠습니까?",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "변경하기",
      cancelButtonText: "취소하기",
      customClass: {
        swalPopup: styles.swalPopup
      }
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await handleSave(canRent, canExchange, quality);
        } catch (err) {
          console.error(err);
        }
      } 
    })
  }

  return (
    <Modal
      open={isOpen}
      onClose={onClose}
      aria-labelledby="quality-status-modal-title"
      aria-describedby="quality-status-modal-description"
      style={{zIndex: 1000}}
    >
      <Box className={styles.modalBox}>
        <Box className={styles.modalHeader}>
          <Typography variant="h5" component="h2" id="quality-status-modal-title">
            책 상태 변경하기
          </Typography>
        </Box>
        <Box className={styles.modalContent}>
          <Box className={styles.checkboxGroup}>
            <FormControlLabel
              control={<Checkbox
                checked={canRent}
                onChange={(e) => setCanRent(e.target.checked)}
              />}
              label="대여 가능 여부"
            />
            <FormControlLabel
              control={<Checkbox
                checked={canExchange}
                onChange={(e) => setCanExchange(e.target.checked)}
              />}
              label="교환 가능 여부"
            />
          </Box>
          <p className={styles.qualityStatus}>현재 품질 상태: {getQualityText(quality).text}</p>
          <Box className={styles.emotionImages}>
            {[1, 2, 3, 4, 5].map((id) => (
              <img
                key={id}
                src={getEmotionImage(id)}
                alt={`Condition ${id}`}
                onClick={() => setQuality(getQualityFromCondition(id))}
                className={quality === getQualityFromCondition(id) ? styles.selected : ''}
              />
            ))}
          </Box>
        </Box>
        <Box className={styles.submitButton}>
          <Button onClick={onSave} variant="contained" color="primary">
            저장
          </Button>
          <Button onClick={onClose} variant="outlined" color="secondary">
            취소
          </Button>
        </Box>
        <div className={styles.footer}></div>
      </Box>
    </Modal>
  )
}

export default BookStatusChangeModal;
