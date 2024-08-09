import { FaAngleRight } from "react-icons/fa6";
import { useState, useEffect } from 'react';
import AreaSelector from "../../components/AreaSelector";
import PrivacyModal from "./PrivacyModal";
import axiosInstance from '../../util/axiosConfig';

// eslint-disable-next-line react/prop-types
const FinalForm = ({ password, setPassword, passwordConfirm, setPasswordConfirm, passwordMismatch, nickname, setNickname, setAreaCode, handleFinalSubmit }) => {
  const [selectedAreaName, setSelectedAreaName] = useState('');
  const [isFormValid, setIsFormValid] = useState(false);
  const [privacyAgreed, setPrivacyAgreed] = useState(false);
  const [showPrivacyModal, setShowPrivacyModal] = useState(false);
  const [siSelected, setSiSelected] = useState(false);
  const [isNicknameAvailable, setIsNicknameAvailable] = useState(false);
  const [isNicknameChecked, setIsNicknameChecked] = useState(false);

  const handleAreaSelected = (selectedArea) => {
    if (selectedArea) {
      setAreaCode(selectedArea.areaCode);
      setSelectedAreaName(`${selectedArea.siName} ${selectedArea.guName} ${selectedArea.dongName}`);
      setSiSelected(true);
    } else {
      setAreaCode('');
      setSelectedAreaName('');
      setSiSelected(false);
    }
  };

  useEffect(() => {
    const isValid = password !== '' &&
                    passwordConfirm !== '' &&
                    !passwordMismatch &&
                    nickname !== '' &&
                    !isNicknameAvailable &&
                    isNicknameChecked &&
                    selectedAreaName !== '' &&
                    privacyAgreed;
    setIsFormValid(isValid);
  }, [password, passwordConfirm, passwordMismatch, nickname, isNicknameAvailable, isNicknameChecked, selectedAreaName, privacyAgreed]);

  const handleShowPrivacy = async () => {
    setShowPrivacyModal(true)
  };

  const checkNicknameAvailability = async () => {
    try {
      const response = await axiosInstance.get(`/users/nicknames/${nickname}`);
      console.log('서버 응답:', response.data);
      console.log(nickname)
      setIsNicknameAvailable(response.data.isDuplicated);
      setIsNicknameChecked(true);
    } catch (error) {
      console.error('닉네임 중복 체크 중 오류 발생:', error);
      setIsNicknameChecked(true);
      setIsNicknameAvailable(false);
    }
  };

  return (
    <form onSubmit={handleFinalSubmit}>
      <div className='titleBox'>
        <p>회원가입 (3/3)</p>
        <h2>거의 다 됐습니다!</h2>
      </div>
      <div className='input_button2'>
        <div>
          <label>비밀번호</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </div>
        <div>
          <label>비밀번호 확인</label>
          <input type="password" value={passwordConfirm} onChange={(e) => setPasswordConfirm(e.target.value)} required />
        </div>
        {passwordMismatch && passwordConfirm !== '' && (
          <div style={{color: 'red', fontSize: '0.8em', marginTop: '5px'}}>
            비밀번호가 일치하지 않습니다.
          </div>
        )}
        <div>
          <label>닉네임</label>
          <div className="nicknameContainer">
            <input 
              type="text" 
              value={nickname} 
              onChange={(e) => {
                setNickname(e.target.value);
                setIsNicknameChecked(false);
              }}
              className="nicknameInput"
              required 
            />
            <button type="button" onClick={checkNicknameAvailability} disabled={!nickname}>
              중복 체크
            </button>
          </div>
        </div>
        {isNicknameChecked && (
          <div style={{color: isNicknameAvailable ? 'red' : 'green', fontSize: '0.8em', marginTop: '5px'}}>
            {!isNicknameAvailable ? '사용 가능한 닉네임입니다.' : '이미 사용 중인 닉네임입니다.'}
          </div>
        )}
        <div className="chooseAreaContainer">
          <div className="AreaContainerInner">
            <label>지역</label>
            <AreaSelector onAreaSelected={handleAreaSelected} />
          </div>
          {siSelected ? (
            selectedAreaName ? (
              <div style={{ marginTop: '20px' }}>선택된 지역: {selectedAreaName}</div>
            ) : (
              <div style={{ color: 'red', fontSize: '0.8em', marginTop: '10px' }}>지역을 선택해주세요.</div>
            )
          ) : null}
        </div>
        <div className="privacy-agreement">
          <input
            type="checkbox"
            id="privacyCheck"
            className="privacyCheck"
            checked={privacyAgreed}
            onChange={(e) => setPrivacyAgreed(e.target.checked)}
            required
          />
          <label htmlFor="privacyCheck">(필수)개인정보 수집·이용에 동의합니다.</label>
          <button type="button" onClick={handleShowPrivacy}>내용보기</button>
        </div>
        {isFormValid && (
          <button className='emailButton2' type="submit">
            <FaAngleRight color='white' size='25px'/>
          </button>
        )}
      </div>
      <PrivacyModal isOpen={showPrivacyModal} onClose={() => setShowPrivacyModal(false)} />
    </form>
  );
};

export default FinalForm;