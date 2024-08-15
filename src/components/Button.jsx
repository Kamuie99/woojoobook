import '../styles/Button.css';

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