import { FaAngleRight } from "react-icons/fa6";
import { useState, useEffect } from 'react';
import AreaSelector from "../../components/AreaSelector";

// eslint-disable-next-line react/prop-types
const FinalForm = ({ password, setPassword, passwordConfirm, setPasswordConfirm, passwordMismatch, nickname, setNickname, setAreaCode, handleFinalSubmit }) => {
  const [selectedAreaName, setSelectedAreaName] = useState('');
  const [isFormValid, setIsFormValid] = useState(false);

  const handleAreaSelected = (selectedAreaCode, selectedAreaName) => {
    setAreaCode(selectedAreaCode);
    setSelectedAreaName(selectedAreaName);
  };

  useEffect(() => {
    const isValid = password !== '' &&
                    passwordConfirm !== '' &&
                    !passwordMismatch &&
                    nickname !== '' &&
                    selectedAreaName !== '';
    setIsFormValid(isValid);
  }, [password, passwordConfirm, passwordMismatch, nickname, selectedAreaName]);
  
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
          {passwordMismatch && passwordConfirm !== '' && <span style={{color: 'red', fontSize: '0.8em', marginLeft: '10px'}}>비밀번호가 일치하지 않습니다.</span>}
        </div>
        <div>
          <label>닉네임</label>
          <input type="text" value={nickname} onChange={(e) => setNickname(e.target.value)} required />
        </div>
        <div className="chooseAreaContainer">
          <div className="AreaContainerInner">
            <label>지역</label>
            <AreaSelector onAreaSelected={handleAreaSelected} />
          </div>
          {selectedAreaName ? (
            <div style={{ marginTop: '20px' }}>선택된 지역: {selectedAreaName}</div>
          ) : (
            <div style={{ color: 'red', fontSize: '0.8em', marginTop: '10px' }}>지역을 선택해주세요.</div>
          )}
        </div>
        {isFormValid && (
          <button className='emailButton2' type="submit">
            <FaAngleRight color='white' size='25px'/>
          </button>
        )}
      </div>
    </form>
  );
};

export default FinalForm;