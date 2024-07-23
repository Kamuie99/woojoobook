import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Header from "../components/Header";
import axiosInstance from '../util/axiosConfig';

const siList = [
  { code: '01', name: '서울' },
  { code: '02', name: '부산' }
];

const guList = {
  '01': [
    { code: '011', name: '강남구' },
    { code: '012', name: '강서구' }
  ],
  '02': [
    { code: '021', name: '해운대구' },
    { code: '022', name: '수영구' }
  ]
};

const dongList = {
  '011': [
    { code: '01101', name: '삼성동' },
    { code: '01102', name: '역삼동' }
  ],
  '012': [
    { code: '01201', name: '화곡동' },
    { code: '01202', name: '염창동' }
  ],
  '021': [
    { code: '02101', name: '중동' },
    { code: '02102', name: '좌동' }
  ],
  '022': [
    { code: '02201', name: '광안동' },
    { code: '02202', name: '민락동' }
  ]
};

const UserUpdate = () => {
  const [nickname, setNickname] = useState('');
  const [areaCode, setAreaCode] = useState('');
  const [siCode, setSiCode] = useState('');
  const [guCode, setGuCode] = useState('');
  const [dongCode, setDongCode] = useState('');
  // const [siList, setSiList] = useState([]);
  // const [guList, setGuList] = useState({});
  // const [dongList, setDongList] = useState({});
  const [guOptions, setGuOptions] = useState([]);
  const [dongOptions, setDongOptions] = useState([]);
  const navigator = useNavigate([]);

  // useEffect(() => {
  //   axiosInstance.get('/users')
  //   .then(response => {
  //     setNickname(response.data.nickname);
  //     setAreaCode(response.data.areaCode);

  //     if (response.data.areaCode === 10) {
  //       const si = response.data.areaCode.substring(0, 2);
  //       const gu = response.data.areaCode.substring(2, 5);
  //       const dong = response.data.areaCode.substring(5, 10);
  //       setSiCode(si);
  //       setGuCode(gu);
  //       setDongCode(dong);
  //     }
  //   })
  //   .catch(e => console.log(e));

  //   axiosInstance.get('/si')
  //     .then(response => setSiList(response.data))
  //     .catch(e => console.log(e));
    
  //   axiosInstance.get('/api/gu')
  //     .then(response => {
  //       const guData = response.data.reduce((acc, curr) => {
  //         const { siCode, code, name } = curr;
  //         if (!acc[siCode]) {
  //           acc[siCode] = [];
  //         }
  //         acc[siCode].push({ code, name });
  //         return acc;
  //       }, {});
  //       setGuList(guData);
  //     })
  //     .catch(error => console.error(error));

  //   axiosInstance.get('/api/dong')
  //     .then(response => {
  //       const dongData = response.data.reduce((acc, curr) => {
  //         const { guCode, code, name } = curr;
  //         if (!acc[guCode]) {
  //           acc[guCode] = [];
  //         }
  //         acc[guCode].push({ code, name });
  //         return acc;
  //       }, {});
  //       setDongList(dongData);
  //     })
  //     .catch(error => console.error(error));
  // }, []);

  useEffect(() => {
    if (siCode) {
      setGuOptions(guList[siCode] || []);
      setDongOptions([]);
    }
  }, [siCode]);

  useEffect(() => {
    if (guCode) {
      setDongOptions(dongList[guCode] || []);
    }
  }, [guCode]);
  
  const handleSave = async (e) => {
    e.preventDefault();
    
    if (nickname.trim() === '') {
      alert('닉네임을 입력해주세요.');
      return;
    }

    if (
      siCode.trim() === '' ||
      guCode.trim() === '' ||
      dongCode.trim() === ''
    ) {
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
  }

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
          <p>주소</p>
          <p>
            <select
              value={siCode}
              onChange={(e) => {
                setSiCode(e.target.value);
                setGuCode('');
                setDongCode('');
              }}
            >
              <option value="">시</option>
              {siList.map(si => <option key={si.code} value={si.code}>{si.name}</option>)}
            </select>
            <select
              value={guCode}
              onChange={(e) => {
                setGuCode(e.target.value);
                setDongCode('');
              }}
              disabled={!siCode}
            >
              <option value="">구</option>
              {guOptions.map(gu => <option key={gu.code} value={gu.code}>{gu.name}</option>)}
            </select>
            <select
              value={dongCode}
              onChange={(e) => {
                setDongCode(e.target.value);
              }}
              disabled={!guCode}
            >
              <option value="">동</option>
              {dongOptions.map(dong => <option key={dong.code} value={dong.code}>{dong.name}</option>)}
            </select>
          </p>
          <button type="submit">저장</button>
        </form>
      </main>
    </>
  )
}

export default UserUpdate;