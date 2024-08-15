import { Link } from 'react-router-dom';
import '../styles/LinkItem.css';

const LinkItem = ({text, icon, to, onClick}) => {
  return (
    <>
    {to ? (
      <Link to={to} className='LinkItem'>
        <div>{icon}</div>
        <div>{text}</div>
      </Link>
    ) : (
      <div onClick={onClick} className='LinkItem'>
        <div>{icon}</div>
        <div>{text}</div>
      </div>
    )
    }
    </>
  )
}

export default LinkItem;