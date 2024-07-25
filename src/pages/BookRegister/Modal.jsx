import ReactModal from 'react-modal';
import styles from './Modal.module.css';

ReactModal.setAppElement('#root');

// eslint-disable-next-line react/prop-types
const Modal = ({ isOpen, onRequestClose, contentLabel, children }) => {
  return (
    <ReactModal
      isOpen={isOpen}
      onRequestClose={onRequestClose}
      contentLabel={contentLabel}
      className={styles.modal}
      overlayClassName={styles.overlay}
    >
      {children}
      <button className={styles.closeButton} onClick={onRequestClose}>Close</button>
    </ReactModal>
  );
};

export default Modal;