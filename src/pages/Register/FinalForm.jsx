import { FaAngleRight } from "react-icons/fa6";

// eslint-disable-next-line react/prop-types
const FinalForm = ({ password, setPassword, passwordConfirm, setPasswordConfirm, nickname, setNickname, areaCode, setAreaCode, handleFinalSubmit }) => {
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
        <div>
          <label>닉네임</label>
          <input type="text" value={nickname} onChange={(e) => setNickname(e.target.value)} required />
        </div>
        <div>
          <label>행정코드</label>
          <input type="text" value={areaCode} onChange={(e) => setAreaCode(e.target.value)} />
        </div>
        <button className='emailButton2' type="submit"><FaAngleRight color='white' size='25px'/></button>
      </div>
    </form>
  );
};

export default FinalForm;