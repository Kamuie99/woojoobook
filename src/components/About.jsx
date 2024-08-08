/* eslint-disable react/prop-types */
import { useState, useEffect } from 'react';
import styles from '../styles/About.module.css';
import axiosInstance from './../util/axiosConfig';

const AnimatedNumber = ({ end, duration }) => {
  const [count, setCount] = useState(0);

  useEffect(() => {
    let start = 0;
    const increment = end / (duration / 16); // 60 FPS에 맞춰 증가량 계산

    const timer = setInterval(() => {
      start += increment;
      if (start >= end) {
        clearInterval(timer);
        setCount(end);
      } else {
        setCount(Math.floor(start));
      }
    }, 16);

    return () => clearInterval(timer);
  }, [end, duration]);

  return <span>{count.toLocaleString()}</span>;
};

const About = () => {
  const [bookCount, setBookCount] = useState(0);

  useEffect(() => {
    const fetchBookCount = async () => {
      try {
        const response = await axiosInstance.get('/userbooks/count');
        setBookCount(response.data.count);
      } catch (error) {
        console.error('Failed to fetch book count:', error);
      }
    };

    fetchBookCount();
  }, []);

  return (
    <div className={`${styles.section} ${styles.sec01}`}>
      <div className={styles.grid_wrap}>
        <div className={styles.grid_l}>
          <div className={styles.block}>
            <h5>우주도서</h5>
            <p className={styles.txt} style={{ height: '80px' }}>
              우주도서는 우리 주변 도서의 약자로
              <br />
              사용자의 생활권 내의 다양한 책들을
              <br />
              원하시는 시간에 누구나 거래할 수 있는 서비스입니다.
            </p>
            <div className={`${styles.count_wrap} ${styles.right_wrap}`}>
              <h6>
                <span>현재 등록된</span>
                우주 도서 현황
              </h6>
              <p>
                <AnimatedNumber end={bookCount} duration={1000} />
                개
              </p>
              <button>우주도서 보러가기</button>
            </div>
          </div>
        </div>
        <div className={styles.grid_r}>
          <div className={styles.block}>
            <h5>우주인</h5>
            <p className={styles.txt} style={{ height: '80px' }}>
              시간과 장소에 구애받지 않고
              <br />
              어디에서든 언제든지 원하는대로
              <br />
              다양한 우주인들과 교류할 수 있습니다.
            </p>
            <div className={styles.count_wrap}>
              <h6>
                <span>우주도서</span>
                이용자 현황
              </h6>
              <p>
                <AnimatedNumber end={2710} duration={2000} />
                명
              </p>
              <button>다른 유저와 소통</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default About;