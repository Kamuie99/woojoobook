import React, { useState } from 'react';
import BookSearch from './BookSearch';
import BookList from './BookList';
import Swal from 'sweetalert2';
import styles from './CategoryForm.module.css';

const CategoryForm = ({ initialCategory, onSubmit, action }) => {
  const [categoryName, setCategoryName] = useState(initialCategory?.categoryName || '');
  const [selectedBooks, setSelectedBooks] = useState(
    initialCategory ? JSON.parse(initialCategory.books) : []
  );

  const handleBookSelect = (book) => {
    if (selectedBooks.length < 5 && !selectedBooks.some(selectedBook => selectedBook.isbn === book.isbn)) {
      const { isbn, thumbnail, title, author } = book;
      setSelectedBooks(prevBooks => [...prevBooks, { isbn, thumbnail, title, author }]);
    }
  };

  const handleBookRemove = (isbn) => {
    setSelectedBooks(prevBooks => prevBooks.filter(book => book.isbn !== isbn));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (selectedBooks.length === 0) {
      Swal.fire({
        title: '최소 1권 이상의 도서를 선택해주세요.',
        confirmButtonText: '확인',
        icon: 'warning'
      })
      return;
    }
    onSubmit({ categoryName, books: JSON.stringify(selectedBooks) });
  };

  return (
    <form onSubmit={handleSubmit} className={styles.form}>
      <div className={styles.inputGroup}>
        <label htmlFor="categoryName" className={styles.label}>카테고리 이름:</label>
        <input
          type="text"
          id="categoryName"
          value={categoryName}
          onChange={(e) => setCategoryName(e.target.value)}
          required
          className={styles.input}
        />
      </div>

      <BookSearch onSelect={handleBookSelect} />

      <div className={styles.selectedBooks}>
        <h3>선택된 책 (최대 5권)</h3>
        <BookList books={selectedBooks} onRemove={handleBookRemove} />
      </div>

      <button type='submit' className={styles.submitButton}>{action}</button>
    </form>
  );
};

export default CategoryForm;
