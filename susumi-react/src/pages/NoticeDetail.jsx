import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import "../css/notice.css";

export default function NoticeDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const page = searchParams.get("page") || 1;

  const [notice, setNotice] = useState(null);
  const [role, setRole] = useState(""); // ✅ 관리자 권한 확인용

  // 공지 데이터 가져오기
  useEffect(() => {
    fetch(`/api/notice/${id}`)
      .then(res => res.json())
      .then(data => setNotice(data));
  }, [id]);

  // 세션에서 role 정보 받아오기
  useEffect(() => {
    fetch("/api/notice/session", { credentials: "include" })  // ✅ 쿠키 포함
      .then(res => res.json())
      .then(data => setRole(data.role));
  }, []);

  const handleDelete = () => {
    if (window.confirm("정말 삭제하시겠습니까?")) {
      fetch(`/api/notice/${id}`, {
        method: "DELETE"
      })
      .then(res => {
        if (res.ok) {
          alert("삭제되었습니다.");
          navigate(`/?page=${page}`);
        } else {
          alert("삭제 실패");
        }
      })
      .catch(err => {
        console.error(err);
        alert("오류 발생");
      });
    }
  };

  if (!notice) return <p>로딩 중...</p>;

  return (
    <div className='container_box'>
      <h2 className="notice-detail-title">{notice.title}</h2>
      <p className="notice-detail-content">{notice.content}</p>
      <small className="notice-detail-date">{notice.rdate}</small>

      <div className="notice-button-group">
        <button onClick={() => navigate(`/?page=${page}`)} className='navbar-btn'>목록으로</button>

        {/* ✅ 관리자만 수정/삭제 버튼 보이게 */}
        {role === 'admin' && (
          <>
            <button onClick={() => navigate(`/update/${id}?page=${page}`)} className='navbar-btn'>수정</button>
            <button onClick={handleDelete} className='btn-delete'>삭제</button>
          </>
        )}
      </div>
    </div>
  );
}
