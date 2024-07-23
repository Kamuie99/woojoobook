import './App.css'
import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Notfound from './pages/Notfound';
import BookRegister from './pages/BookRegister';
import Policy from './pages/Policy';
import MyActivity from './pages/MyActivity';
import MyLibrary from './pages/MyLibrary';
import Register from './pages/Register';
import UserUpdate from './pages/UserUpdate';
import PasswordChange from './pages/PasswordChange';


// 1. "/": home 페이지
// 2. "/login": login 페이지


function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<Home/>} />
        <Route path="/login" element={<Login />} />
        <Route path='/bookregister' element={<BookRegister />} />
        <Route path='/register' element={<Register />} />
        <Route path='/policy' element={<Policy/>} />
        <Route path='/myactivity' element={<MyActivity />} />
        <Route path='/mylibrary' element={<MyLibrary />} />
        <Route path='/user-update' element={<UserUpdate />} />
        <Route path='/password-change' element={<PasswordChange />} />
        <Route path="*" element={<Notfound />} />
      </Routes>
    </>
  )
}

export default App
