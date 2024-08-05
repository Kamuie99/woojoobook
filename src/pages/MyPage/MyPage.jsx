import { useState, useEffect, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Header from '../../components/Header'
import axiosInstance from '../../util/axiosConfig';
import { AuthContext } from '../../contexts/AuthContext';
import { jwtDecode } from 'jwt-decode';
import styles from './MyPage.module.css';
import Swal from 'sweetalert2';
import astronaut from '../../assets/astronaut.png';

const MyPage = () => {
  const [selectedAreaName, setSelectedAreaName] = useState('');
  const [fetchedUserId, setFetchedUserId] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [userPoint, setUserPoint] = useState('');
  const [userExperience, setUserExperience] = useState('');
  const { isLoggedIn, logout, sub, token } = useContext(AuthContext);
  const navigator = useNavigate([]);

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await axiosInstance.get('/users');
        const personalResponse = await axiosInstance.get(`/users/personal`);
        const userData = response.data;
        const personalData = personalResponse.data;

        const userAreaCode = response.data.areaCode;
        
        setNickname(userData.nickname);
        setFetchedUserId(userData.userId);
        setUserPoint(personalData.point);
        setUserExperience(personalData.experience);
        fetchUserAreaName(userAreaCode);

      } catch (error) {
          console.error("Error fetching user: ", error);
      }
    };
    fetchUserInfo();
  }, []);

  const fetchUserAreaName = async (areaCode) => {
    try {
      const response = await axiosInstance.get(`/area?areaCode=${areaCode}`)
      const userAreaName = response.data.siName +' '+ response.data.guName +' '+ response.data.dongName;
      setSelectedAreaName(userAreaName);
    } catch (error) {
      console.error('Error fetching user area name:', error);
    }
  };

  const deleteUser = async () => {
    if (isLoggedIn && 
      jwtDecode(token)?.sub != fetchedUserId) {
      return alert('잘못된 요청입니다.')
    }
    if (!password) {
      return alert('비밀번호를 입력해 주세요.');
    }
    try {
      await axiosInstance.delete('/users', { data: { password } });
      logout();
      navigator.push('/');
      Swal.fire({
        title: "Deleted!",
        text: "Your account has been deleted.",
        icon: "success"
      });
    } catch (error) {
      console.error("Error deleting user: ", error);
    }
  }

  const openModal = () => {
    Swal.fire({
      title: "현재 비밀번호를 한번 더 입력해주세요.",
      html: `<p style="color: red; font-weight: bold;">탈퇴한 회원 정보는 되돌릴 수 없습니다.</p>
             <input type="password" id="password" class="swal2-input" placeholder="비밀번호를 입력해주세요">`,
      showCancelButton: true,
      confirmButtonText: "탈퇴",
      confirmButtonColor: "#d33",
      cancelButtonText: "취소",
      preConfirm: () => {
        const password = Swal.getPopup().querySelector('#password').value;
        if (!password) {
          Swal.showValidationMessage('비밀번호를 입력해 주세요.');
        }
        return { password: password };
      }
    }).then((result) => {
      if (result.isConfirmed) {
        setPassword(result.value.password);
        deleteUser();
      }
    });
  };
  return (
    <>
      <Header />
      <main className={styles.MyPage}>
        <div className={styles.main_container}>
          <div className={styles.main_container_update}>
            <div className={styles.to_mylibrary}>
              <Link to={`/${sub}/mylibrary`}>내 서재 바로가기</Link>
            </div>
            <div className={styles.update_info}>
              <Link to='/userupdate'>회원정보 수정</Link>
            </div>
            <div className={styles.change_password}>
              <Link to='/passwordchange'>비밀번호 변경</Link>
            </div>
          </div>
          <div className={styles.main_container_info}>
            <div className={styles.info_container}>
              <p>닉네임: {nickname}</p>
              <p>지역: {selectedAreaName}</p>
            </div>
            <div className={styles.user_grade}>
              <p style={{ marginRight: '15px' }}>나의 등급 : 지구인</p>
              <img src={astronaut} alt="" width="60px"/>
            </div>
          </div>
          <div className={styles.main_container_point}>
            <div className={styles.user_point}>
              내 포인트 : {userPoint}
            </div>
          </div>
          <div className={styles.main_container_exp}>
            <div className={styles.user_exp}>
              경험치 : {userExperience}
            </div>
          </div>
          <div className={styles.main_container_delete}>
            <div className={styles.delete_container}>
              <p className={styles.user_delete_button} onClick={openModal}>회원탈퇴</p>
            </div>
          </div>
        </div>
      </main> 
    </>
  )
}

export default MyPage
