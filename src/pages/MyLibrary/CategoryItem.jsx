/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import { useState } from 'react';
import { useDrag, useDrop } from 'react-dnd';
import styles from './CategoryItem.module.css';
import { useRef } from 'react';
import { PiBookmarkFill } from "react-icons/pi";
import BookModal from './BookModal';
import axiosInstance from '../../util/axiosConfig.js'; // axiosConfig 파일 import

const ItemType = 'CATEGORY';

const CategoryItem = ({category, isOwnLibrary, index, onUpdate, onDelete, moveCategory, saveCategoryOrder, onEmptyBoxClick }) => {
  const [showModal, setShowModal] = useState(false);
  const [bookDetail, setBookDetail] = useState(null);
  const ref = useRef(null);

  const [{ handlerId }, drop] = useDrop({
    accept: ItemType,
    collect: (monitor) => ({
      handlerId: monitor.getHandlerId(),
    }),
    hover: (item, monitor) => {
      if (!ref.current) {
        return;
      }

      const dragIndex = item.index;
      const hoverIndex = index;

      if (dragIndex === hoverIndex) {
        return;
      }

      const hoverBoundingRect = ref.current?.getBoundingClientRect();
      const hoverMiddleY = (hoverBoundingRect.bottom - hoverBoundingRect.top) / 2;
      const clientOffset = monitor.getClientOffset();
      const hoverClientY = clientOffset.y - hoverBoundingRect.top;

      if (dragIndex < hoverIndex && hoverClientY < hoverMiddleY) {
        return;
      }
      if (dragIndex > hoverIndex && hoverClientY > hoverMiddleY) {
        return;
      }

      moveCategory(item.category, category);
      item.index = hoverIndex;
    },
    // eslint-disable-next-line no-unused-vars
    drop: (item, monitor) => {
      saveCategoryOrder(item.category, category);
    },
  });

  const [{ isDragging }, drag] = useDrag({
    type: ItemType,
    item: { category, index },
    collect: (monitor) => ({
      isDragging: monitor.isDragging(),
    }),
  });

  const opacity = isDragging ? 0.4 : 1;
  drag(drop(ref));

  const books = (() => {
    try {
      return typeof category.books === 'string' ? JSON.parse(category.books) : category.books
    } catch (error) {
      console.log(error)
      return []
    }
  })()

  const emptyBoxVisible = isOwnLibrary && books.length < 5;

  const handleBookClick = async (book) => {
    try {
      const response = await axiosInstance.get(`/books?keyword=${book.isbn}&page=1`);
      setBookDetail(response.data.bookList[0]);
      setShowModal(true);
    } catch (error) {
      console.error('Error fetching book details:', error);
    }
  };

  return (
    <li ref={ref} style={{ opacity }} data-handler-id={handlerId} className={styles.categoryItem}>
      <div className={styles.header}>
        <div className={styles.categoryName}><PiBookmarkFill />{category.categoryName}</div>
        <div className={styles.buttonGroup}>
          {onUpdate && <button onClick={() => onUpdate(category)} className={styles.button}>수정</button>}
          {onDelete && <button onClick={() => onDelete(category)} className={styles.button}>삭제</button>}
        </div>
      </div>

      <div className={styles.bookList}>
        {books.map((book) => (
          <div key={book.isbn} className={styles.book}>
            <img 
              src={book.thumbnail} 
              alt={book.title} 
              className={styles.bookThumbnail}
              onClick={() => handleBookClick(book)}
            />
          </div>
        ))}
        {emptyBoxVisible && books.length < 5 && (
          <div className={styles.book}>            
            <div className={styles.emptyBox} onClick={onEmptyBoxClick}>
              <span>+</span>
            </div>
          </div>
        )}
      </div>
      {showModal && bookDetail && (
        <BookModal 
          book={bookDetail} 
          onClose={() => setShowModal(false)} 
        />
      )}
    </li>
  )
}

export default CategoryItem
