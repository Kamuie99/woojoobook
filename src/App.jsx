import './App.css'
import { Routes, Route, useLocation } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Notfound from './pages/Notfound';
import BookRegister from './pages/BookRegister/BookRegister';
import Policy from './pages/Policy';
import MyActivity from './pages/MyActivity';
import MyLibrary from './pages/MyLibrary';
import Register from './pages/Register/Register';
import UserUpdate from './pages/UserUpdate';
import PasswordChange from './pages/PasswordChange';
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
          <Route path='/myactivity' element={<MyActivity />} />
          <Route path='/mylibrary' element={<MyLibrary />} />
          <Route path='/user-update' element={<UserUpdate />} />
          <Route path='/password-change' element={<PasswordChange />} />
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
