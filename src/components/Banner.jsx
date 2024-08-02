import '../styles/Banner.css';
import SearchBox from './SearchBox';

const Banner = () => {
  return (
    <div className='Banner'>
      <div className='bannerTitle'>
        {/* <h2><strong>우</strong>리  <strong>주</strong>변의  <strong>도서</strong>를  지금 찾아 보세요!</h2> */}
        <h2><strong>우주도서</strong>를 지금 바로 찾아 보세요!</h2>
      </div>
      <SearchBox />
    </div>
  )
}

export default Banner;