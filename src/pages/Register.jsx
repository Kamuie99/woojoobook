import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../util/axiosConfig';
import Header2 from '../components/Header2';

const Register = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [email, setEmail] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const [nickname, setNickname] = useState('');
  const [areaCode, setAreaCode] = useState('');

  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    try {
      await axiosInstance.post('users/emails', 
        { email }
      );
      setStep(2);
    } catch (error) {
      console.error(error);
      if (error.code === 'ERR_NETWORK') {
        console.error('네트워크 오류:', error.message);
        alert('서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.');
      } else {
        console.error('요청 오류:', error);
        alert('이메일 전송 중 오류가 발생했습니다.');
      }
    }
  };

  const handleVerificationSubmit = async (e) => {
    e.preventDefault();
    try {
      await axiosInstance.put('users/emails',
        { email, verificationCode },
      );
      setStep(3);
    } catch (error) {
      if (error.response) {
        console.error(error);
        alert('인증 실패');
      }
    }
  };

  const handleFinalSubmit = async (e) => {
    e.preventDefault();
    if (password !== passwordConfirm) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }
    try {
      const response = await axiosInstance.post('users', {
        email,
        password,
        passwordConfirm,
        nickname,
        areaCode,
      }, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      if (response.status === 201) {
        alert('회원가입 성공');
        navigate('/');
      }
    } catch (error) {
      console.error(error);
      alert('회원가입 실패');
    }
  };

  const renderStep = () => {
    switch (step) {
      case 1:
        return (
          <form onSubmit={handleEmailSubmit}>
            <p>이메일
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
            </p>
            <button type="submit">인증 코드 받기</button>
          </form>
        );
      case 2:
        return (
          <form onSubmit={handleVerificationSubmit}>
            <p>인증 코드
              <input type="text" value={verificationCode} onChange={(e) => setVerificationCode(e.target.value)} required />
            </p>
            <button type="submit">인증 확인</button>
          </form>
        );
      case 3:
        return (
          <form onSubmit={handleFinalSubmit}>
            <p>비밀번호
              <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
            </p>
            <p>비밀번호 확인
              <input type="password" value={passwordConfirm} onChange={(e) => setPasswordConfirm(e.target.value)} required />
            </p>
            <p>닉네임
              <input type="text" value={nickname} onChange={(e) => setNickname(e.target.value)} required />
            </p>
            <p>행정코드
              <input type="text" value={areaCode} onChange={(e) => setAreaCode(e.target.value)} />
            </p>
            <button type="submit">회원가입</button>
          </form>
        );
      default:
        return null;
    }
  };

  return (
    <>
      <Header2 />
      <main>
        <p>회원가입</p>
        {renderStep()}
      </main>
    </>
  );
};

export default Register;
