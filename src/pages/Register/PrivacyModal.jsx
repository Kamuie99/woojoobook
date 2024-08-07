import { useState, useEffect } from 'react';
import styles from './PrivacyModal.module.css';

const PrivacyModal = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  const handleOutsideClick = (e) => {
    if (e.target.className === styles.modalOverlay) {
      onClose();
    }
  };

  return (
    <div className={styles.modalOverlay} onClick={handleOutsideClick}>
      <div className={styles.modalContent}>
        <p className={styles.contentTitle}>개인정보 수집 및 이용 동의</p>
        <p>우주도서(이하 "회사")는 P2P 도서 교환 및 대여 서비스 제공을 위해 아래와 같이 개인정보를 수집 및 이용합니다. 동의하시면 서비스 이용이 가능합니다.</p>
        
        <div className={styles.contentDiv}>
          <h2>1. 수집 목적</h2>
          <p>- 회원 식별 및 가입의사 확인</p>
          <p>- 서비스 제공 및 관리</p>
          <p>- 도서 대여 및 교환 서비스 제공</p>
          <p>- 공지사항 전달</p>
        </div>
        <div className={styles.contentDiv}>
          <h2>2. 수집 항목</h2>
          <p>- 이메일 주소</p>
          <p>- 비밀번호</p>
          <p>- 주소</p>
        </div>
        <div className={styles.contentDiv}>
          <h2>3. 보유 및 이용 기간</h2>
          <p>- 회원 탈퇴 시까지</p>
          <p>- 단, 관련 법령에 따라 일정 기간 보관이 필요한 경우 해당 기간 동안 보관됩니다.</p>
        </div>
        <div className={styles.contentDiv}>
          <h2>4. 동의 거부권 및 거부 시 불이익</h2>
          <p>- 개인정보 수집 및 이용에 대한 동의를 거부할 권리가 있습니다.</p>
          <p>- 그러나 동의를 거부할 경우 회원가입 및 서비스 이용이 불가능합니다.</p>
        </div>
        <div className={styles.contentDiv}>
          <h2>5. 개인정보의 제3자 제공</h2>
          <p>- 회원의 개인정보는 원칙적으로 외부에 제공하지 않습니다.</p>
          <p>- 단, 회원의 동의가 있거나 법령의 규정에 의한 경우는 예외로 합니다.</p>
        </div>
        <div className={styles.contentDiv}>
          <h2>6. 이용자의 권리</h2>
          <p>- 회원은 언제든지 등록되어 있는 자신의 개인정보를 조회하거나 수정할 수 있으며, 회원탈퇴를 통해 개인정보의 삭제를 요청할 수 있습니다.</p>
        </div>
        <div className={styles.contentDiv}>
          <h2>7. 개인정보 보호책임자</h2>
          <p>- 이름: E207</p>
          <p>- 연락처: e207.woojoobook@gmail.com</p>
          <p>- 개인정보 처리에 대한 상세한 내용은 '개인정보처리방침'에서 확인하실 수 있습니다.</p>
        </div>
        <div className={styles.contentDiv}>
          <h3>※ 주의사항</h3>
          <p>- 회원은 타인의 권리를 존중하고 실제로 보유한 도서만을 등록해야 합니다.</p>
          <p>- 도서의 상태를 정확히 기재하여 다른 회원들에게 정확한 정보를 제공해야 합니다.</p>
          <p>- 약속한 기간 내에 도서를 반납해야 하며, 연체 3일 경과 시 서비스 이용이 영구 정지됩니다.</p>
          <p>- 도서의 훼손이나 분실 시 책임자는 즉시 상대방에게 알리고 적절한 보상을 해야 합니다.</p>
          <p>- 회원의 포인트가 0 이하가 될 경우 서비스 이용이 영구 정지됩니다.</p>
        </div>
        <br />
        서비스 이용과 관련된 자세한 내용은 '이용약관' 및 '운영정책'을 참고해 주시기 바랍니다.
      </div>
    </div>
  );
};

export default PrivacyModal;