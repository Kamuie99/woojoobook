import { useState, useEffect, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import { jwtDecode } from 'jwt-decode';
import { FaUserCircle } from "react-icons/fa";
import Swal from 'sweetalert2';
import axiosInstance from '../../util/axiosConfig';
import Header from '../../components/Header'
import a1 from '../../assets/a1.png';
import a2 from '../../assets/a2.png';
import a3 from '../../assets/a3.png';
import a4 from '../../assets/a4.png';
import a5 from '../../assets/a5.png';
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

  const mySwal = (title, confirmButtonText, icon, text = null) => {
    Swal.fire({
      title,
      text,
      confirmButtonText,
      icon
    })
  }

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
      return mySwal('인증 실패', '확인', 'error', '인증에 실패했습니다. 다시 로그인해 주세요.')
    }

    if (!isTokenValid(token)) {
      logout();
      navigate('/login');
      return mySwal('세션 만료', '확인', 'error', '세션이 만료되었습니다. 다시 로그인해 주세요.')
    }

    try {
      await axiosInstance.delete('/users', {
        data : { password: inputPassword }
      });
      logout();
      navigate('/');
      mySwal('회원 탈퇴가 완료되었습니다.', '확인', 'success', '회원 탈퇴가 완료되었습니다.');
    } catch (error) {
      console.error(error)
      mySwal('오류', '확인', 'error', '회원 탈퇴 중 문제가 발생했습니다. 다시 시도해 주세요.')
    }
  }

  const openModal = () => {
    Swal.fire({
      title: '비밀번호를 입력해주세요.',
      html: `<p style="color: red; font-weight: bold;">탈퇴한 회원 정보는 되돌릴 수 없습니다.</p>
             <input type="password" id="password" class="swal2-input" placeholder="비밀번호를 입력해주세요">`,
      showCancelButton: true,
      confirmButtonText: '탈퇴',
      cancelButtonText: '취소',
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

  const checkUserGrade = (userExperience) => {
    if (userExperience >= 100000) {
      return {text: '우주 대가', grade: a5};
    } else if (userExperience >= 36000) {
      return {text: '별빛 연구원', grade: a4};
    } else if (userExperience >= 12000) {
      return {text: '행성 항해사', grade: a3};
    } else if (userExperience >= 5000) {
      return {text: '달 탐험가', grade: a2};
    } else {
      return {text: '지구인', grade: a1};
    }
  }

  const userGradeDescription = (grade) => {
    const gradeMap = {
      '지구인': {textEng: 'Earthling', description: '도서 탐험의 첫걸음을 내딛은 단계입니다. 다양한 책들을 접하며 독서의 세계로 발을 들여놓습니다.'},
      '달 탐험가': {textEng: 'Lunar Explorer', description: '독서의 세계에서 한 단계 더 나아가, 새로운 지식을 탐구하고 책과 친밀해지는 단계입니다.'},
      '행성 항해사': {textEng: 'Planet Navigator', description: '여러 책들을 깊이 있게 읽으며, 도서의 광대한 세계를 항해하는 단계입니다. 독서에 대한 이해가 더욱 깊어집니다.'},
      '별빛 연구원': {textEng: 'Starlight Researcher', description: '독서의 깊이를 더하며, 책의 본질과 가치를 탐구하는 단계입니다. 독서에서 발견하는 통찰이 더욱 빛납니다.'},
      '우주 대가': {textEng: 'Master of the Cosmos', description: '도서의 우주를 완벽히 이해한 궁극의 독자입니다. 책을 통해 지식을 넓히고, 문학과 지식의 대가로 자리 잡습니다.'},
    }
    return gradeMap[grade];
  }

  const handleLibraryClick = () => {
    navigate('/myLibrary', { state: { userId: sub }});
  }

  return (
    <>
      <Header />
      <main>
        <div className={styles.titleDiv}>
          <FaUserCircle /> 마이페이지
        </div>
        <div className={styles.MyPage}>
          <div className={styles.mainContainer}>
            <div className={styles.mainContainerUpdate}>
              <div className={styles.toMylibrary}
                onClick={handleLibraryClick}
              >
                나의 서재 바로가기
              </div>
              <div className={styles.updateInfo}>
                <Link to='/userupdate'>회원정보 수정</Link>
              </div>
              <div className={styles.changePassword}>
                <Link to='/passwordchange'>비밀번호 변경</Link>
              </div>
            </div>
            <div className={styles.mainContainerInfo}>
              <div className={styles.infoContainer}>
                <p>닉네임: {nickname}</p>
                <p>지역: {selectedAreaName}</p>
              </div>
              <div className={styles.userGrade}>
                <p style={{ marginRight: '15px' }}>나의 등급 : {checkUserGrade(userExperience).text}</p>
                <img src={checkUserGrade(userExperience).grade} alt="" width="60px"/>
                <div className={styles.userGradeDescription}>
                  <strong className={styles.userGradeDescriptionTitle}>
                    {checkUserGrade(userExperience).text} | {userGradeDescription(checkUserGrade(userExperience).text).textEng}
                  </strong>
                  <hr />
                  <p className={styles.userGradeDescriptionDetail}>
                    {userGradeDescription(checkUserGrade(userExperience).text).description}
                  </p>
                </div>
              </div>
            </div>
            <div className={styles.mainContainerPoint}>
              <div className={styles.userPoint}>
                내 포인트 : {userPoint}
              </div>
            </div>
            <div className={styles.mainContainerExp}>
              <div className={styles.userExp}>
                경험치 : {userExperience}
              </div>
            </div>
            <div className={styles.mainContainerDelete}>
              <div className={styles.deleteContainer}>
                <p className={styles.userDeleteButton} onClick={openModal}>회원탈퇴</p>
              </div>
            </div>
          </div>
        </div>
      </main> 
    </>
  )
}

export default MyPage
