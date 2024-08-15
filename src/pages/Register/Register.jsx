import { useState, useEffect, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import axiosInstance from '../../util/axiosConfig';
import Header2 from '../../components/Header2';
import Logo from '../../assets/logoWithLetter.webp';
import '../../styles/Register.css';
import EmailForm from './EmailForm';
import VerificationForm from './VerificationForm';
import FinalForm from './FinalForm';
import Swal from 'sweetalert2';


const Register = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [email, setEmail] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const [nickname, setNickname] = useState('');
  const [areaCode, setAreaCode] = useState('');
  const [emailError, setEmailError] = useState(' ');
  const [passwordMismatch, setPasswordMismatch] = useState(false);
  const { isLoggedIn } = useContext(AuthContext)

  const mySwal = (title,  confirmButtonText, icon, text = null) => {
    Swal.fire({
      title,
      text,
      confirmButtonText,
      icon
    })
  }

  useEffect(() => {
    if (isLoggedIn) {
      navigate('/');
    }
  }, []);

  useEffect(() => {
    setPasswordMismatch(password !== passwordConfirm);
  }, [password, passwordConfirm]);

  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    setEmailError('');
    try {
      // 이메일 중복 검사
      const checkResponse = await axiosInstance.get(`users/emails/${email}`);
      if (checkResponse.data.isDuplicated  === true) {
        setEmailError('이미 가입된 이메일입니다.');
        return;
      }
      // 중복이 없는경우 로직 실행
      await axiosInstance.post('users/emails', { email });
      setStep(2);
      } catch (error) {
      console.error(error);
      if (error.code === 'ERR_NETWORK') {
        console.error('네트워크 오류:', error.message);
        mySwal('서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.', '확인', 'error')
      } else {
        console.error('요청 오류:', error);
        mySwal('이메일 전송 중 오류가 발생했습니다.', '확인', 'error')
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
        mySwal('인증번호 오류', '확인', 'error', '잘못된 인증번호입니다. 인증번호를 확인해주세요')
      }
    }
  };

  const handleFinalSubmit = async (e) => {
    e.preventDefault();
    if (password !== passwordConfirm) {
      mySwal('변경할 비밀번호가 일치하지 않습니다.', '확인', 'warning')
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
        mySwal('회원가입이 완료', '확인', 'success', '회원가입이 완료되었습니다. 로그인 해주세요.')
        navigate('/login');
      }
    } catch (error) {
      if (error.response.data[0].defaultMessage == "크기가 8에서 2147483647 사이여야 합니다") {
        mySwal('회원가입 실패', '확인', 'warning', '비밀번호는 8자리 이상이여야 합니다.')
        return;
      }
      console.error(error);
      mySwal('회원가입 실패', '확인', 'error', '회원가입 정보를 다시 확인해주세요.')
    }
  };

  const renderStep = () => {
    switch (step) {
      case 1:
        return <EmailForm 
        email={email} 
        setEmail={setEmail} 
        handleEmailSubmit={handleEmailSubmit} 
        emailError={emailError} 
      />;
      case 2:
        return <VerificationForm 
          verificationCode={verificationCode} 
          setVerificationCode={setVerificationCode} 
          handleVerificationSubmit={handleVerificationSubmit} 
        />;
      case 3:
        return <FinalForm 
          password={password}
          setPassword={setPassword}
          passwordConfirm={passwordConfirm}
          setPasswordConfirm={setPasswordConfirm}
          passwordMismatch={passwordMismatch}
          nickname={nickname}
          setNickname={setNickname}
          areaCode={areaCode}
          setAreaCode={setAreaCode}
          handleFinalSubmit={handleFinalSubmit}
        />;
      default:
        return null;
    }
  };
  

  return (
    <>
      <Header2 />
      <main className='Register'>
        <Link to="/">
          <img src={Logo} alt="Home" style={{ cursor: 'pointer' }} />
        </Link>
        <div>
          {renderStep()}
        </div>
      </main>
    </>
  );
};

export default Register;
