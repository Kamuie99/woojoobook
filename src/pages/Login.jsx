import { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axiosInstance from '../util/axiosConfig';
import Header2 from "../components/Header2";
import { AuthContext } from '../contexts/AuthContext';
import Swal from 'sweetalert2';
import '../styles/Login.css';
// import Logo from '../assets/Logo.png';
import Logo from '../assets/logoWithLetter.webp';

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
          navigate('/');
        } else {
          throw new Error('토큰이 응답 헤더에 없습니다.');
        }
      }
    } catch (error) {
      Swal.fire({
        title: '로그인 실패',
        text: '이메일 또는 비밀번호를 확인해주세요.',
        confirmButtonText: '확인',
        icon: 'error',
      });
    }
  };

  return (
    <>
      <Header2 />
      <main className='Login'>
        <Link to="/">
          <img src={Logo} width='220px' alt="Home" style={{ cursor: 'pointer' }} />
        </Link>
        <div>
          <form onSubmit={handleSubmit}>
          <div className='titleBox'>
            <p>로그인</p>
            <h2>이메일 하나로 우주도서를 찾아보세요!</h2>
          </div>
          <div className='input_button3'>
            <div>
              <label htmlFor='email'>이메일</label>
              <input 
                type="email" 
                id="email"
                value={email} 
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div>
              <label htmlFor='password'>비밀번호</label>
              <input 
                type="password" 
                id="password"
                value={password} 
                onChange={(e) => setPassword(e.target.value)}
                required
              />
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