import React, { useState, useEffect } from "react";
import { Modal, Box, IconButton, Typography, FormControlLabel, Checkbox, Button } from '@mui/material';
import { IoMdCloseCircleOutline } from "react-icons/io";
import { getEmotionImage } from '../../util/get-emotion-image';
import styles from './BookStatusChangeModal.module.css';

const BookStatusChangeModal = ({ isOpen, onClose, registerType, qualityStatus, handleSubmitBookStatusChange }) => {
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
    switch(condition) {
      case 1: return 'VERY_GOOD';
      case 2: return 'GOOD';
      case 3: return 'NORMAL';
      case 4: return 'BAD';
      case 5: return 'VERY_BAD';
      default: return '';
    }
  };

  const getQualityText = (quality) => {
    switch(quality) {
      case 'VERY_GOOD': return { text: '매우 좋음', color: '#65C964' };
      case 'GOOD': return { text: '좋음', color: '#9ED672' };
      case 'NORMAL': return { text: '보통', color: '#FCCE18' };
      case 'BAD': return { text: '나쁨', color: '#FE8447' };
      case 'VERY_BAD': return { text: '매우 나쁨', color: '#FD565F' };
      default: return { text: '', color: '' };
    }
  };

  const onSave = () => {
    handleSubmitBookStatusChange(canRent, canExchange, quality);
  }

  return (
    <Modal
      open={isOpen}
      onClose={onClose}
      aria-labelledby="quality-status-modal-title"
      aria-describedby="quality-status-modal-description"
    >
      <Box className={styles.modalBox}>
        <Box className={styles.modalHeader}>
          <Typography variant="h6" component="h2" id="quality-status-modal-title">
            책 상태 변경
          </Typography>
          <IconButton aria-label="close" onClick={onClose}>
            <IoMdCloseCircleOutline size={30} />
          </IconButton>
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
          <Typography variant="body1" id="quality-status-modal-description">
            현재 품질 상태: {getQualityText(quality).text}
          </Typography>
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
      </Box>
    </Modal>
  )
}

export default BookStatusChangeModal;