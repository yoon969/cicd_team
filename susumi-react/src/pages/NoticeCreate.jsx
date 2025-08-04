// NoticeCreate.jsx
import React, { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import "../css/notice.css";

export default function NoticeCreate() {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const navigate = useNavigate();
  const [searchParams] = useSearchParams(); // ✅ page 받기
  const page = searchParams.get("page") || 1;

  const handleSubmit = () => {
    fetch('/api/notice', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, content }),
    })
    .then((res) => {
      if (res.ok) {
        navigate(`/?page=${page}`); // ✅ 등록 후 해당 페이지로 이동
      } else {
        alert("등록 실패");
      }
    });
  };

  return (
    <div className='container_box notice-form'>
      <h2 className='form-title'>공지 등록</h2>
      <input
        className="form-input"
        placeholder="제목"
        value={title}
        onChange={e => setTitle(e.target.value)}
      /><br />
      <textarea
        className="form-textarea"
        placeholder="내용"
        value={content}
        onChange={e => setContent(e.target.value)}
      /><br />
      <div className="notice-button-group">
        <button onClick={() => navigate(`/notice/page?page=${page}`)} className='navbar-btn'>목록으로</button>
        <button className="navbar-btn" onClick={handleSubmit}>등록</button>
      </div>
    </div>
  );
}
