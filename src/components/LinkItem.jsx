import { Link } from 'react-router-dom';
import '../styles/LinkItem.css';

// eslint-disable-next-line react/prop-types
const LinkItem = ({text, icon, to}) => {
  return (
    <Link to={to} className='LinkItem'>
      <div>{icon}</div>
      <div>{text}</div>
    </Link>
  )
}

export default LinkItem;