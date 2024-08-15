import { Link } from 'react-router-dom';
import '../styles/LinkItem.css';

const LinkItem = ({text, icon, to}) => {
  return (
    <Link to={to} className='LinkItem'>
      <div>{icon}</div>
      <div>{text}</div>
    </Link>
  )
}

export default LinkItem;