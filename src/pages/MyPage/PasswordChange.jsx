import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../../util/axiosConfig';
import Header from "../../components/Header"
import { AuthContext } from '../../contexts/AuthContext';

const PasswordChange = () => {
  const { isAuthenticated } = useContext(AuthContext);
  const [curPassword, setCurPassword] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const navigate = useNavigate();

  const handlePasswordChange = async () => {
    if (!curPassword) {
      setPasswordError('현재 비밀번호를 입력하세요.');
      return;
    }
    if (!password) {
      setPasswordError('새 비밀번호를 입력하세요.');
      return;
    }
    if (password!==passwordConfirm) {
      setPasswordError('변경할 비밀번호가 일치하지 않습니다.');
      return;
    }

    try {
      const response = await axiosInstance.put('/users/password', {
        curPassword,
        password,
        passwordConfirm
      });
      if (response.status === 200) {
        alert('비밀번호가 성공적으로 변경되었습니다.');
        setCurPassword('');
        setPassword('');
        setPasswordConfirm('');
        navigate('/user-update');
      } else {
        setPasswordError('비밀번호 변경 실패.');
      }
    } catch (error) {
      console.log('비밀번호 변경 오류:', error);
      setPasswordError('');
      alert('비밀번호 변경 실패: ' + (error.response?.data?.message || error.message));
  }
};
  
  return (
    <>
      <Header />
      <main>
        <h2>비밀번호 변경</h2>
        <div>
          <label>현재 비밀번호:</label>
          <input type="password" value={curPassword} onChange={(e) => setCurPassword(e.target.value)} />
        </div>
        <div>
          <label>새 비밀번호:</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </div>
        <div>
          <label>비밀번호 확인:</label>
          <input type="password" value={passwordConfirm} onChange={(e) => setPasswordConfirm(e.target.value)} />
        </div>
        <button onClick={handlePasswordChange}>변경</button>
        {passwordError && <p>{passwordError}</p>}
      </main>
  </>
  )
}

export default PasswordChange;