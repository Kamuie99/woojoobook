import { FaAngleRight } from "react-icons/fa6";

const EmailForm = ({ email, setEmail, handleEmailSubmit, emailError }) => {
  return (
    <form onSubmit={handleEmailSubmit} className='emailForm'>
      <div className='titleBox'>
        <p>회원가입 (1/3)</p>
        <h2>이메일 하나로 우주도서를 찾아보세요!</h2>
      </div>
      <div className='input_button'>
        <input
          className='emailInput'
          type="email" 
          value={email} 
          placeholder='example@galaxybook.com'
          onChange={(e) => setEmail(e.target.value)} 
          required 
        />
        <button className='emailButton' type="submit"><FaAngleRight color='white' size='25px'/></button>
      </div>
      {emailError && <p className="error-message" style={{color: 'red'}}>{emailError}</p>}
    </form>
  );
};

export default EmailForm;