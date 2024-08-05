import { useState } from 'react'
import Header from "../components/Header"
import axiosInstance from "../util/axiosConfig";

const TestPage = () => {
  const [rentalId, setRentalId] = useState(null);
  const [error, setError] = useState(null);

  const handleRentalOffer = async () => {
    try {
      const response = await axiosInstance.post('/userbooks/1/rentals/offer');
      setRentalId(response.data.rentalId);
      setError(null);
    } catch (err) {
      setError('Error making rental offer: ' + err.message);
      setRentalId(null);
    }
  };

  return (
    <>
      <Header />
      <div>
        <h1>교환 요청 테스트</h1>
        <button onClick={handleRentalOffer}>렌탈 신청</button>
        {rentalId && <p>Rental offer successful! Rental ID: {rentalId}</p>}
        {error && <p style={{color: 'red'}}>{error}</p>}
      </div>
    </>
  )
}

export default TestPage;