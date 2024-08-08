import { useState, useEffect } from 'react'
import Rental from './Rental'
import Exchange from './Exchange'
import Extension from './Extension'
import styles from './Proceed.module.css';

const Proceed = () => {
  const [activeContent, setActiveContent] = useState(() => {
    const storedContent = localStorage.getItem('proceedActiveContent');
    return storedContent || 'rental';
  });

  useEffect(() => {
    localStorage.setItem('proceedActiveContent', activeContent);
  }, [activeContent]);

  const handleSelectChange = (e) => {
    setActiveContent(e.target.value)
  }

  return (
    <div className={styles.bigContainer}>
      <div className={styles.selectBox}>
        <select value={activeContent} onChange={handleSelectChange}>
          <option value="rental">대여</option>
          <option value="exchange">교환</option>
          <option value="extension">연장</option>
        </select>
      </div>
      {activeContent === 'rental' && <Rental />}
      {activeContent === 'exchange' && <Exchange />}
      {activeContent === 'extension' && <Extension />}
    </div>
  )
}

export default Proceed