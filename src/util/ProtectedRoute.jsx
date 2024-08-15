import { useContext } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';
import Swal from "sweetalert2";

const ProtectedRoute = () => {
  const { isLoggedIn, isLoading } = useContext(AuthContext);
  const navigate = useNavigate();

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!isLoggedIn) {
    Swal.fire({
      title: '권한이 없습니다.',
      text: '로그인이 필요한 기능입니다',
      icon: 'error',
      confirmButtonText: '로그인',
      allowOutsideClick: false,
      allowEscapeKey: false
    }).then((result) => {
      if (result.isConfirmed) {
        navigate('/login');
      }
    });
    
    return null;
  }

  return <Outlet />;
};

export default ProtectedRoute;