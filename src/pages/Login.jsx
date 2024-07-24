import { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axiosInstance from '../util/axiosConfig';
import Header2 from "../components/Header2";
import { AuthContext } from '../contexts/AuthContext';
import '../styles/Login.css';
import Logo from '../assets/Logo.png';

const Login = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login } = useContext(AuthContext);

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      const response = await axiosInstance.post('auth', {
        email,
        password,
      });

      if (response.status === 200) {
        const authToken = response.headers['authorization'];
        if (authToken) {
          const token = authToken.split(' ')[1]; // "Bearer " 부분을 제거
          login(token);
          // alert('로그인 성공');
          navigate('/');
        } else {
          throw new Error('토큰이 응답 헤더에 없습니다.');
        }
      }
    } catch (error) {
      // console.error('로그인 오류:', error);
      // alert('로그인 실패: ' + (error.response?.data?.message || error.message));
      alert('이메일 또는 비밀번호를 확인해주세요.')
    }
  };

  return (
    <>
      <Header2 />
      <main className='Login'>
        <Link to="/">
          <img src={Logo} width='170px' alt="Home" style={{ cursor: 'pointer' }} />
        </Link>
        
        <div>
          <form onSubmit={handleSubmit}>



          <div className='titleBox'>
            <p>로그인</p>
            <h2>이메일 하나로 우주도서를 찾아보세요!</h2>
          </div>


          <div className='input_button3'>
            <div>
              <label>아이디</label>
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
            </div>
            <div>
              <label>비밀번호</label>
              <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
            </div>
            <button className='emailButton3' type="submit">로그인</button>
          </div>
          </form>
        </div>
      </main>
    </>
  )
}

export default Login;