import { useState, useEffect, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FaUserCircle } from "react-icons/fa";
import { AuthContext } from '../../contexts/AuthContext';
import Swal from 'sweetalert2';
import axiosInstance from '../../util/axiosConfig';
import Header2 from "../../components/Header2";
import AreaSelector from '../../components/AreaSelector';
import styles from './UserUpdate.module.css';
import Logo from '../../assets/logoWithLetter.webp';

const UserUpdate = () => {
  const [userId, setUserId] = useState('');
  const [nickname, setNickname] = useState('');
  const [selectedArea, setSelectedArea] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();
  const { updateUser } = useContext(AuthContext);
  const [isNicknameAvailable, setIsNicknameAvailable] = useState(true);
  const [isNicknameChecked, setIsNicknameChecked] = useState(false);
  const [originalNickname, setOriginalNickname] = useState('');

  useEffect(() => {
    fetchUserInfo();
  }, []);

  const fetchUserInfo = async () => {
    try {
      setIsLoading(true);
      const userResponse = await axiosInstance.get('/users');
      const userData = userResponse.data;
      setUserId(userData.id);
      setNickname(userData.nickname);
      setOriginalNickname(userData.nickname);

      const areaResponse = await axiosInstance.get(`/area?areaCode=${userData.areaCode}`);
      const areaData = areaResponse.data;
      setSelectedArea({
        siName: areaData.siName,
        guName: areaData.guName,
        dongName: areaData.dongName,
        areaCode: userData.areaCode
      });
    } catch (error) {
      console.error("Error fetching user info:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleAreaSelected = (selectedArea) => {
    setSelectedArea(selectedArea);
  };

  const checkNicknameAvailability = async () => {
    if (nickname === originalNickname) {
      setIsNicknameAvailable(true);
      setIsNicknameChecked(true);
      return;
    }
    
    try {
      const response = await axiosInstance.get(`/users/nicknames/${nickname}`);
      setIsNicknameAvailable(!response.data.isDuplicated);
      setIsNicknameChecked(true);
    } catch (error) {
      console.error('닉네임 중복 체크 중 오류 발생:', error);
      setIsNicknameChecked(true);
      setIsNicknameAvailable(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();

    if (nickname.trim() === '') {
      Swal.fire({
        title: '닉네임을 입력해주세요.',
        confirmButtonText: '확인',
        icon: 'warning'
      });
      return;
    }

    if (nickname !== originalNickname && (!isNicknameChecked || !isNicknameAvailable)) {
      Swal.fire({
        title: '닉네임 중복 체크를 해주세요.',
        confirmButtonText: '확인',
        icon: 'warning'
      });
      return;
    }

    if (!selectedArea || !selectedArea.areaCode) {
      Swal.fire({
        title: '주소를 선택해주세요.',
        confirmButtonText: '확인',
        icon: 'warning'
      });
      return;
    }

    try {
      const response = await axiosInstance.put('/users', {
        nickname,
        areaCode: selectedArea.areaCode
      });
      if (response.status === 200) {
        updateUser({
          nickname,
          areaCode: selectedArea.areaCode
        });
        
        Swal.fire({
          title: "회원정보 수정성공",
          text: "회원정보가 성공적으로 변경되었습니다.",
          icon: "success"
        });
        navigate(-1);
      } else {
        Swal.fire({
          title: '정보 수정에 실패했습니다. 다시 시도해 주세요.',
          confirmButtonText: '확인',
          icon: 'error'
        });
      }
    } catch (error) {
      console.error(error);
      Swal.fire({
        title: '정보 저장 중 오류가 발생했습니다.',
        confirmButtonText: '확인',
        icon: 'error'
      });
    }
  };

  return (
    <>
      <Header2 />
      <main className={styles.userUpdate}>
        <Link to="/">
          <img src={Logo} width='220px' alt="Home" style={{ cursor: 'pointer' }} />
        </Link>
        <div className={styles.titleDiv}>
          <FaUserCircle /> 회원정보 수정
        </div>
        {isLoading ? (
          <div>로딩 중...</div>
        ) : (
          <div className={styles.contentDiv}>
            <div className={styles.goChangePw}>
              <label htmlFor="">비밀번호 변경</label>
              <Link to='/passwordchange'>
                <button>비밀번호 변경 페이지로 이동</button>
              </Link>
            </div>
            <form onSubmit={handleSave}>
              <div className={styles.changeNick}>
                <label>닉네임 변경</label>
                <div className={styles.nickBox}>
                  <input 
                    type="text" 
                    value={nickname} 
                    onChange={(e) => {
                      setNickname(e.target.value);
                      if (e.target.value !== originalNickname) {
                        setIsNicknameChecked(false);
                      } else {
                        setIsNicknameChecked(true);
                        setIsNicknameAvailable(true);
                      }
                    }} 
                  />
                  <button type="button" onClick={checkNicknameAvailability} disabled={!nickname || nickname === originalNickname}>
                    중복 체크
                  </button>
                </div>
              </div>
              {!nickname && (
                <div style={{color: 'red', fontSize: '0.8em', marginTop: '5px'}}>
                  닉네임을 입력해주세요.
                </div>
              )}
              {nickname && !isNicknameChecked && nickname !== originalNickname && (
                <div style={{color: 'red', fontSize: '0.8em', marginTop: '5px'}}>
                  닉네임 중복 체크를 해주세요.
                </div>
              )}
              {isNicknameChecked && nickname !== originalNickname && (
                <div style={{color: isNicknameAvailable ? 'green' : 'red', fontSize: '0.8em', marginTop: '5px'}}>
                  {isNicknameAvailable ? '사용 가능한 닉네임입니다.' : '이미 사용 중인 닉네임입니다.'}
                </div>
              )}
              <div className={styles.areaBox}>
                <label>지역 변경</label>
                <AreaSelector
                  onAreaSelected={handleAreaSelected}
                  initialArea={selectedArea}
                />
              </div>
              <button className={styles.submitButton} type="submit">저장</button>
            </form>
          </div>
        )}
      </main>
    </>
  );
};

export default UserUpdate;
