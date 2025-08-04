import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import "../css/notice.css";

export default function NoticeUpdate() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const page = searchParams.get("page") || 1;

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [role, setRole] = useState("");

  // 기존 데이터 불러오기
  useEffect(() => {
    fetch(`/api/notice/${id}`)
      .then(res => res.json())
      .then(data => {
        setTitle(data.title);
        setContent(data.content);
      });
  }, [id]);

  // 관리자 확인
  useEffect(() => {
    fetch("/api/notice/session", { credentials: "include" })
      .then(res => res.json())
      .then(data => setRole(data.role));
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!title.trim() || !content.trim()) {
      alert("제목과 내용을 모두 입력해주세요.");
      return;
    }

    fetch(`/api/notice/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ title, content })
    })
    .then(res => {
      if (res.ok) {
        alert("수정되었습니다.");
        navigate(`/${id}?page=${page}`);
      } else {
        alert("수정 실패");
      }
    })
    .catch(err => {
      console.error(err);
      alert("오류 발생");
    });
  };

  if (role !== "admin") return <p>관리자만 접근할 수 있습니다.</p>;

  return (
    <div className='container_box notice-form'>
      <h2 className='form-title'>공지사항 수정</h2>
      <input
        className="form-input"
        value={title}
        onChange={e => setTitle(e.target.value)}
        placeholder="제목"
      /><br />
      <textarea
        className="form-textarea"
        value={content}
        onChange={e => setContent(e.target.value)}
        placeholder="내용"
      /><br />
      <div className="notice-button-group">
        <button onClick={handleSubmit} className='navbar-btn'>수정</button>
        <button type="button" onClick={() => navigate(`/notice/page/${id}?page=${page}`)} className='navbar-btn'>취소</button>
      </div>
    </div>
  );
}
