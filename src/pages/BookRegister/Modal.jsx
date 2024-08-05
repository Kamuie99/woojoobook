import ReactModal from 'react-modal';
import { IoCloseSharp } from "react-icons/io5";
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
      <button className={styles.closeButton} onClick={onRequestClose}>
        <IoCloseSharp />
      </button>
      {children}
    </ReactModal>
  );
};

export default Modal;