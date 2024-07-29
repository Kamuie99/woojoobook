import '../styles/SearchBox.css';
import { IoIosSearch } from "react-icons/io";
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSearch } from '../contexts/SearchContext';

const SearchBox = () => {
  const [inputValue, setInputValue] = useState('');
  const navigate = useNavigate();
  const { setSearchTerm } = useSearch();

  const handleSubmit = (e) => {
    e.preventDefault();
    if (inputValue.trim()) {
      setSearchTerm(inputValue);
      navigate('/booklist');
    }
  };

  return (
    <form className='SearchBox' onSubmit={handleSubmit}>
      <input 
        type="text"
        value={inputValue}
        onChange={(e) => setInputValue(e.target.value)}
        placeholder='책 제목을 입력해 주세요'
      />
      <button type="submit">{<IoIosSearch color='white' size='24' />}</button>
    </form>
  )
}

export default SearchBox;