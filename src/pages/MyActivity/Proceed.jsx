import React, { useState, useEffect } from 'react'
import Rental from './Rental'
import Exchange from './Exchange'
import Extension from './Extension'

const Proceed = () => {
  const [activeContent, setActiveContent] = useState(() => {
    const storedContent = localStorage.getItem('proceedActiveContent');
    return storedContent || 'rental';
  });

  useEffect(() => {
    localStorage.setItem('proceedActiveContent', activeContent);
  }, [activeContent]);

  const handelSelectChange = (e) => {
    setActiveContent(e.target.value)
  }

  return (
    <div>
      <select value={activeContent} onChange={handelSelectChange}>
        <option value="rental">대여</option>
        <option value="exchange">교환</option>
        <option value="extension">연장</option>
      </select>

      {activeContent === 'rental' && <Rental />}
      {activeContent === 'exchange' && <Exchange />}
      {activeContent === 'extension' && <Extension />
      }
    </div>
  )
}

export default Proceed