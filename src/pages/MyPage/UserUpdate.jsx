import { useState, useEffect, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FaUserCircle } from "react-icons/fa";
import { AuthContext } from '../../contexts/AuthContext';
import Swal from 'sweetalert2';
import axiosInstance from '../../util/axiosConfig';
import Header from "../../components/Header";
import AreaSelector from '../../components/AreaSelector';
import styles from './UserUpdate.module.css';

const UserUpdate = () => {
  const [userId, setUserId] = useState('');
  const [nickname, setNickname] = useState('');
  const [selectedArea, setSelectedArea] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();
  const { updateUser } = useContext(AuthContext);

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
        navigate(`/${userId}/mypage`);
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
      <Header />
      <main>
        <div className={styles.titleDiv}>
          <FaUserCircle /> 회원정보 수정
        </div>
        {isLoading ? (
          <div>로딩 중...</div>
        ) : (
          <div className={styles.contentDiv}>
            <div>
              <Link to='/passwordchange'>
                <button>비밀번호 변경</button>
              </Link>
            </div>
            <form onSubmit={handleSave}>
              <p>닉네임</p>
              <input type="text" value={nickname} onChange={(e) => setNickname(e.target.value)} />
              <div>
                <label>지역</label>
                <AreaSelector
                  onAreaSelected={handleAreaSelected}
                  initialArea={selectedArea}
                />
              </div>
              <button type="submit">저장</button>
            </form>
          </div>
        )}
      </main>
    </>
  );
};

export default UserUpdate;
