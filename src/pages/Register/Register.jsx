import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axiosInstance from '../../util/axiosConfig';
import Header2 from '../../components/Header2';
import Logo from '../../assets/Logo.png';
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

  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    setEmailError(''); // 에러 메시지 초기화
    try {
      // 이메일 중복 검사
      const checkResponse = await axiosInstance.get(`users/emails/${email}`);
      console.log(checkResponse)
      if (checkResponse.data.isDuplicated  === true) {
        setEmailError('이미 가입된 이메일입니다.');
        return;
      }
      console.log('중복아님')

      // 중복이 없는경우 로직 실행
      await axiosInstance.post('users/emails', { email });
      setStep(2);
      } catch (error) {
      console.error(error);
      if (error.code === 'ERR_NETWORK') {
        console.error('네트워크 오류:', error.message);
        Swal.fire({
          title: '서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.',
          confirmButtonText: '확인',
          icon: 'error'
        })
      } else {
        console.error('요청 오류:', error);
        Swal.fire({
          title: '이메일 전송 중 오류가 발생했습니다.',
          confirmButtonText: '확인',
          icon: 'error'
        })
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
        Swal.fire({
          title: '잘못된 인증번호를 입력하셨습니다. 다시 입력해주세요.',
          confirmButtonText: '확인',
          icon: 'error'
        })
      }
    }
  };

  const handleFinalSubmit = async (e) => {
    e.preventDefault();
    if (password !== passwordConfirm) {
      Swal.fire({
        title: '비밀번호가 일치하지 않습니다.',
        confirmButtonText: '확인',
        icon: 'warning'
      })
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
        Swal.fire({
          title: '회원가입이 완료되었습니다. 로그인 해주세요.',
          confirmButtonText: '확인',
          icon: 'success'
        })
        navigate('/login');
      }
    } catch (error) {
      console.error(error);
      Swal.fire({
        title: '회원가입에 실패했습니다. 잠시 후에 다시 시도해주세요.',
        confirmButtonText: '확인',
        icon: 'error'
      })
    }
  };

  const handlePasswordConfirmChange = (e) => {
    const confirmValue = e.target.value;
    setPasswordConfirm(confirmValue);
    setPasswordMismatch(password !== confirmValue);
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
          setPasswordConfirm={handlePasswordConfirmChange}
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
          <img src={Logo} width='170px' alt="Home" style={{ cursor: 'pointer' }} />
        </Link>
        <div>
          {renderStep()}
        </div>
      </main>
    </>
  );
};

export default Register;
