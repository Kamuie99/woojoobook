import { useState, useEffect, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import { jwtDecode } from 'jwt-decode';
import { FaUserCircle } from "react-icons/fa";
import Swal from 'sweetalert2';
import axiosInstance from '../../util/axiosConfig';
import Header from '../../components/Header'
import astronaut from '../../assets/astronaut.png';
import styles from './MyPage.module.css';

const MyPage = () => {
  const [selectedAreaName, setSelectedAreaName] = useState('');
  const [fetchedUserId, setFetchedUserId] = useState('');
  const [nickname, setNickname] = useState('');
  const [userPoint, setUserPoint] = useState('');
  const [userExperience, setUserExperience] = useState('');
  const { isLoggedIn, logout, sub, token } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await axiosInstance.get('/users');
        const personalResponse = await axiosInstance.get(`/users/personal`);
        const userData = response.data;
        const personalData = personalResponse.data;
        const userAreaCode = response.data.areaCode;
        
        setNickname(userData.nickname);
        setFetchedUserId(userData.id);
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

  const isTokenValid = (token) => {
    try {
      const decodedToken = jwtDecode(token);
      const currentTime = Date.now() / 1000;
      return decodedToken.exp > currentTime;
    } catch (error) {
      return false;
    }
  }

  const deleteUser = async (inputPassword) => {
    const decodedToken = jwtDecode(token);
    if (!isLoggedIn || decodedToken?.sub != fetchedUserId) {
      return Swal.fire({
        title: "Error!",
        text: "인증에 실패했습니다. 다시 로그인해 주세요.",
        icon: "error"
      });
    }

    if (!isTokenValid(token)) {
      logout();
      navigate('/login');
      return Swal.fire({
        title: "Error!",
        text: "세션이 만료되었습니다. 다시 로그인해 주세요.",
        icon: "error"
      });
    }

    try {
      await axiosInstance.delete('/users', {
        data : { password: inputPassword }
      });
      logout();
      navigate('/');
      Swal.fire({
        title: "Deleted!",
        text: "회원 탈퇴가 완료되었습니다",
        icon: "success"
      });
    } catch (error) {
      console.log(error)
      Swal.fire({
        title: "Error!",
        text: "회원 탈퇴 중 문제가 발생했습니다. 다시 시도해 주세요.",
        icon: "error"
      })
    }
  }

  const openModal = () => {
    Swal.fire({
      title: "비밀번호를 입력해주세요.",
      html: `<p style="color: red; font-weight: bold;">탈퇴한 회원 정보는 되돌릴 수 없습니다.</p>
             <input type="password" id="password" class="swal2-input" placeholder="비밀번호를 입력해주세요">`,
      showCancelButton: true,
      confirmButtonText: "탈퇴",
      confirmButtonColor: "#d33",
      cancelButtonText: "취소",
      focusConfirm: false,
      preConfirm: () => {
        const password = Swal.getPopup().querySelector('#password').value;
        if (!password) {
          Swal.showValidationMessage('비밀번호를 입력해 주세요.');
        }
        return { password: password };
      },
      didOpen: (popup) => {
        const input = popup.querySelector('#password');
        input.focus();
        input.addEventListener('keypress', function(e) {
          if (e.key === 'Enter') {
            e.preventDefault();
            Swal.clickConfirm();
          }
        })
      }
    }).then((result) => {
      if (result.isConfirmed) {
        deleteUser(result.value.password);
      }
    });
  };
  return (
    <>
      <Header />
      <main>
        <div className={styles.titleDiv}>
          <FaUserCircle /> 마이페이지
        </div>
        <div className={styles.MyPage}>
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
        </div>
      </main> 
    </>
  )
}

export default MyPage