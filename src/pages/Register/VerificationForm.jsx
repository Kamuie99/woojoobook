import { FaAngleRight } from "react-icons/fa6";

const VerificationForm = ({ verificationCode, setVerificationCode, handleVerificationSubmit }) => {
  return (
    <form onSubmit={handleVerificationSubmit} className='emailForm'> 
      <div className='titleBox'>
        <p>회원가입 (2/3)</p>
        <h2>이메일로 전송된 인증번호를 입력해주세요</h2>
      </div>
      <div className='input_button'>
        <input 
          className='emailconfirmInput' 
          type="text" 
          value={verificationCode} 
          onChange={(e) => setVerificationCode(e.target.value)} 
          required 
        />
        <button className='emailButton' type="submit"><FaAngleRight color='white' size='25px'/></button>
      </div>
    </form>
  );
};

export default VerificationForm;