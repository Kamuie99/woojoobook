import { useState, useEffect } from 'react'
import Header from "../components/Header"
import axiosInstance from "../util/axiosConfig";

const TestPage = () => {
  const [siList, setSiList] = useState([]);
  const [guList, setGuList] = useState([]);
  const [dongList, setDongList] = useState([]);
  const [selectedSi, setSelectedSi] = useState('');
  const [selectedGu, setSelectedGu] = useState('');
  const [selectedDong, setSelectedDong] = useState('');

  useEffect(() => {
    fetchSiList();
  }, []);

  const fetchSiList = async () => {
    try {
      const response = await axiosInstance.get('/area/si');
      setSiList(response.data.siList);
    } catch (error) {
      console.error("Error fetching si list:", error);
    }
  };

  const fetchGuList = async (siCode) => {
    try {
      const response = await axiosInstance.get(`/area/gu?siCode=${siCode}`);
      setGuList(response.data.guList);
    } catch (error) {
      console.error("Error fetching gu list:", error);
    }
  };

  const fetchDongList = async (siCode, guCode) => {
    try {
      const response = await axiosInstance.get(`/area/dong?siCode=${siCode}&guCode=${guCode}`);
      setDongList(response.data.dongList);
    } catch (error) {
      console.error("Error fetching dong list:", error);
    }
  };

  const handleSiChange = (e) => {
    const siCode = e.target.value;
    setSelectedSi(siCode);
    setSelectedGu('');
    setSelectedDong('');
    setGuList([]);
    setDongList([]);
    if (siCode) fetchGuList(siCode);
  };

  const handleGuChange = (e) => {
    const guCode = e.target.value;
    setSelectedGu(guCode);
    setSelectedDong('');
    setDongList([]);
    if (guCode) fetchDongList(selectedSi, guCode);
  };

  const handleDongChange = (e) => {
    setSelectedDong(e.target.value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Selected: ", { '지역코드': selectedDong });
    // 여기에 제출 로직을 추가하세요
  };

  return (
    <>
      <Header />
      <main>
        <form onSubmit={handleSubmit}>
          <select value={selectedSi} onChange={handleSiChange}>
            <option value="">시/도 선택</option>
            {siList.map((si) => (
              <option key={si.siCode} value={si.siCode}>
                {si.siName}
              </option>
            ))}
          </select>
          <select value={selectedGu} onChange={handleGuChange} disabled={!selectedSi}>
            <option value="">구/군 선택</option>
            {guList.map((gu) => (
              <option key={gu.guCode} value={gu.guCode}>
                {gu.guName}
              </option>
            ))}
          </select>
          <select value={selectedDong} onChange={handleDongChange} disabled={!selectedGu}>
            <option value="">동/읍/면 선택</option>
            {dongList.map((dong) => (
              <option key={dong.areaCode} value={dong.areaCode}>
                {dong.dongName}
              </option>
            ))}
          </select>
          <button type="submit">제출</button>
        </form>
      </main>
    </>
  )
}

export default TestPage;