import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Header from "../../components/Header";
import axiosInstance from '../../util/axiosConfig';
import AreaSelector from '../../components/AreaSelector';

const UserUpdate = () => {
  const [nickname, setNickname] = useState('');
  const [selectedAreaName, setSelectedAreaName] = useState('');
  const [siList, setSiList] = useState([]);
  const [guList, setGuList] = useState([]);
  const [dongList, setDongList] = useState([]);
  const [siCode, setSiCode] = useState('');
  const [guCode, setGuCode] = useState('');
  const [dongCode, setDongCode] = useState('');
  const navigator = useNavigate([]);
  
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const userResponse = await axiosInstance.get('/users');
        console.log(userResponse.json());
      } catch (error) {
        console.error("Error fetching user:", error);
      }
    }
    fetchUserInfo();
  }, []);  

  useEffect(() => {
    const userAreaCode = '2638051000';
    const userSiCode = userAreaCode.slice(0, 2);
    const userGuCode = userAreaCode.slice(2, 5);
    const userDongCode = userAreaCode.slice(5, 10);
    
    const fetchAreaData = async () => {
      try {
        const siResponse = await axiosInstance.get('/area/si');
        const guResponse = await axiosInstance.get(`/area/gu?siCode=${userSiCode}`);
        const dongResponse = await axiosInstance.get(`/area/dong?siCode=${userSiCode}&guCode=${userGuCode}`);

        setSiList(siResponse.data.siList);
        setGuList(guResponse.data.guList);
        setDongList(dongResponse.data.dongList);

        setSiCode(userSiCode);
        setGuCode(userGuCode);
        setDongCode(userDongCode);

        const selectedSiName = siResponse.data.siList.find(si => si.siCode === userSiCode)?.siName;
        const selectedGuName = guResponse.data.guList.find(gu => gu.guCode === userGuCode)?.guName;
        const selectedDongName = dongResponse.data.dongList.find(dong => dong.areaCode === userAreaCode)?.dongName;
        
        setSelectedAreaName(`${selectedSiName} ${selectedGuName} ${selectedDongName}`);
      } catch (error) {
        console.error("Error fetching area data:", error);
      }
    };

    fetchAreaData();
  }, []);

  const handleAreaSelected = (areaCode, selectedAreaName) => {
    setSelectedAreaName(selectedAreaName);
    const siCode = areaCode.slice(0, 2);
    const guCode = areaCode.slice(2, 5);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    
    if (nickname.trim() === '') {
      alert('닉네임을 입력해주세요.');
      return;
    }

    if (siCode.trim() === '' || guCode.trim() === '' || dongCode.trim() === '') {
      alert('주소를 모두 선택해주세요.');
      return;
    }

    const areaCode = `${siCode}${guCode}${dongCode}`;

    try {
      const response = await axiosInstance.put('/users', {
        nickname,
        areaCode
      });

      if (response.status === 200) {
        alert('정보가 성공적으로 저장되었습니다.');
        navigator('/');
      } else {
        alert('정보 저장에 실패했습니다.');
      }
    } catch (error) {
      console.error(error);
      alert('정보 저장 중 오류가 발생했습니다.');
    }
  };

  return (
    <>
      <Header />
      <main>
        <div>
          <img src="" alt="profile" />
          <button>edit</button>
          <Link to='/password-change'>
            <button>비밀번호 변경</button>
          </Link>
        </div>
        <form onSubmit={handleSave}>
          <p>닉네임</p>
          <input type="text" value={nickname} onChange={(n) => setNickname(n.target.value)}/>
          <div>
            <label>지역</label>
            {siCode}
            {guCode}
            {dongCode}
            <AreaSelector
              onAreaSelected={handleAreaSelected}
              initialSiCode={siCode}
              initialGuCode={guCode}
              initialDongCode={dongCode}
            />
          </div>
          {selectedAreaName && <div style={{ marginTop: '20px' }}>선택된 지역: {selectedAreaName}</div>}
          <button type="submit">저장</button>
        </form>
      </main>
    </>
  )
}

export default UserUpdate;