import '../styles/Banner.css';
import SearchBox from './SearchBox';

const Banner = () => {
  return (
    <div className='Banner'>
      <div className='bannerTitle'>
        <h2><strong>우주도서</strong>를 지금 바로 찾아 보세요!</h2>
      </div>
      <SearchBox />
    </div>
  )
}

export default Banner;