import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../../util/axiosConfig';
import Header from "../../components/Header";
import Swal from 'sweetalert2';
import { FaUserCircle } from "react-icons/fa";
import { AuthContext } from '../../contexts/AuthContext';
import styles from './PasswordChange.module.css';

const PasswordChange = () => {
  const { user } = useContext(AuthContext);
  const [curPassword, setCurPassword] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const navigate = useNavigate();

  const handlePasswordChange = async () => {
    if (!curPassword) {
      Swal.fire({
        title: '현재 비밀번호를 입력하세요.',
        confirmButtonText: '확인',
        icon: 'error'
      });
      return;
    }
    if (!password) {
      Swal.fire({
        title: '새 비밀번호를 입력하세요.',
        confirmButtonText: '확인',
        icon: 'error'
      });
      return;
    }
    if (password !== passwordConfirm) {
      Swal.fire({
        title: '변경할 비밀번호가 일치하지 않습니다.',
        confirmButtonText: '확인',
        icon: 'error'
      });
      return;
    }

    try {
      const response = await axiosInstance.put('/users/password', {
        curPassword,
        password,
        passwordConfirm
      });
      if (response.status === 200) {
        Swal.fire({
          title: '비밀번호가 성공적으로 변경되었습니다.',
          confirmButtonText: '확인',
          icon: 'success'
        });
        setCurPassword('');
        setPassword('');
        setPasswordConfirm('');
        navigate(`/${user.id}/mypage`);
      } else {
        Swal.fire({
          title: '비밀번호 변경을 실패했습니다.',
          confirmButtonText: '확인',
          icon: 'error'
        });
      }
    } catch (error) {
      console.log('비밀번호 변경 오류:', error);
      Swal.fire({
        title: '비밀번호 변경을 실패했습니다.: ' + (error.response?.data?.message || error.message),
        confirmButtonText: '확인',
        icon: 'error'
      });
    }
  };

  return (
    <>
      <Header />
      <main>
        <div className={styles.titleDiv}>
          <FaUserCircle /> 비밀번호 변경
        </div>
        <div className={styles.contentDiv}>
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
        </div>
      </main>
    </>
  );
}

export default PasswordChange;