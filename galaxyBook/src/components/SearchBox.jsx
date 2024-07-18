import '../styles/SearchBox.css';
import { IoIosSearch } from "react-icons/io";

const SearchBox = () => {
  return (
    <div className='SearchBox'>
      <input type="text" />
      <button type="submit">{<IoIosSearch color='white' size='24' />}</button>
    </div>
  )
}

export default SearchBox;