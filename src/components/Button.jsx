import '../styles/Button.css';

// eslint-disable-next-line react/prop-types
const Button = ({ text, type, onClick, color }) => {
  return (
    <button 
      onClick={onClick} 
      className={`Button Button_${type}`}
      style={{backgroundColor: `${color}`}}
    >
      {text}
    </button>
  )
}

export default Button