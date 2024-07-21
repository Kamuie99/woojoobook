import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../util/axiosConfig';
import Header2 from "../components/Header2";
import { AuthContext } from '../contexts/AuthContext';

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
          alert('로그인 성공');
          navigate('/');
        } else {
          throw new Error('토큰이 응답 헤더에 없습니다.');
        }
      }
    } catch (error) {
      console.error('로그인 오류:', error);
      alert('로그인 실패: ' + (error.response?.data?.message || error.message));
    }
  };

  return (
    <>
      <Header2 />
      <main>
        <p>로그인</p>
        <form onSubmit={handleSubmit}>
          <p>
            이메일
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
          </p>
          <p>
            비밀번호
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
          </p>
          <button type="submit">로그인</button>
        </form>
      </main>
    </>
  )
}

export default Login;