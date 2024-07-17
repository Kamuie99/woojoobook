import './App.css'
import { Routes, Route, Link } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Notfound from './pages/Notfound';

// 1. "/": home 페이지
// 2. "/login": login 페이지


function App() {
  return (
    <>
      <div>
        <Link to={"/"}>홈으로</Link>
        <Link to={"/login"}>로그인</Link>
      </div>
      <Routes>
        <Route path="/" element={<Home/>} />
        <Route path="/login" element={<Login />} />
        <Route path="*" element={<Notfound />} />
      </Routes>
    
    </>
  )
}

export default App
