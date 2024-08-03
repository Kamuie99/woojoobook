import './App.css'
import { Routes, Route, useLocation } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Notfound from './pages/Notfound';
import BookRegister from './pages/BookRegister/BookRegister';
import Policy from './pages/Policy';
import MyBook from './pages/MyBook';
import MyActivity from './pages/MyActivity/MyActivity';
import MyLibrary from './pages/MyLibrary/MyLibrary';
import Register from './pages/Register/Register';
import MyPage from './pages/MyPage/MyPage'
import UserUpdate from './pages/MyPage/UserUpdate';
import PasswordChange from './pages/MyPage/PasswordChange';
import ProtectedRoute from './util/ProtectedRoute';
import Chatting from './components/Chatting';
import BookList from './pages/BookList/BookList';
import TestPage from './pages/TestPage';


// 1. "/": home 페이지
// 2. "/login": login 페이지


function App() {
  const location = useLocation();
  const excludedPaths = ['/login', '/register'];
  return (
    <>
      <Routes>
        <Route path="/" element={<Home/>} />
        <Route path="/login" element={<Login />} />
        <Route path='/register' element={<Register />} />


        <Route element={<ProtectedRoute />}>        
          <Route path='/bookregister' element={<BookRegister />} />
          <Route path='/policy' element={<Policy/>} />
          <Route path='/:userId/mybook' element={<MyBook/>} />
          <Route path='/:userId/myactivity' element={<MyActivity />} />
          <Route path='/:userId/mylibrary' element={<MyLibrary />} />
          <Route path='/:userId/mypage' element={<MyPage />} />
          <Route path='/userupdate' element={<UserUpdate />} />
          <Route path='/passwordchange' element={<PasswordChange />} />
          <Route path='/booklist' element={<BookList />} />
          <Route path='/test' element={<TestPage />} />
        </Route>
          
        <Route path="*" element={<Notfound />} />
      </Routes>
      {!excludedPaths.includes(location.pathname) && <Chatting />}
    </>
  )
}

export default App
