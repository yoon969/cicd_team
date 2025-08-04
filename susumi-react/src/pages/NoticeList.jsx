import { useEffect, useState } from "react";
import axios from "axios";
import { Link, useSearchParams, useNavigate } from "react-router-dom";

export default function NoticeList() {
  const [list, setList] = useState([]);
  const [totalCount, setTotalCount] = useState(0);
  const [role, setRole] = useState(""); // ✅ 관리자 여부 저장
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();

  const pageSize = 8;
  const blockSize = 5;
  const page = Number(searchParams.get("page")) || 1;

  const totalPages = Math.ceil(totalCount / pageSize);
  const currentBlock = Math.floor((page - 1) / blockSize);
  const startPage = currentBlock * blockSize + 1;
  const endPage = Math.min(startPage + blockSize - 1, totalPages);

  const fetchList = () => {
    axios.get("/api/notice", {
      params: {
        page,
        size: pageSize
      }
    })
    .then(res => {
      setList(res.data.data);
      setTotalCount(res.data.totalCount);
    })
    .catch(err => console.error(err));
  };

  const fetchSession = () => {
    axios.get("/api/notice/session") // ✅ 세션에서 role 정보 받아오기
      .then(res => {
        setRole(res.data.role); // 예: "admin", "user"
      })
      .catch(err => console.error("세션 정보 가져오기 실패", err));
  };

  useEffect(() => {
    fetchList();
  }, [page]);

  useEffect(() => {
    fetchSession();
  }, []);

  return (
    <div className="container_main2">
      <div className="notice-wrapper">
        <div className="notice-header">
          <h2 className="notice-title">📢 공지사항</h2>

          {/* ✅ 관리자만 등록 버튼 보이게 */}
          {role === 'admin' && (
            <Link className="notice-create-btn" to={`/create?page=${page}`}>
              공지사항 등록
            </Link>
          )}
        </div>

        <div className="notice-grid">
          {list.map(n => (
            <Link
              key={n.notice_id}
              to={`/${n.notice_id}?page=${page}`}
              className="notice-card-link"
            >
              <div className="notice-card">
                <div className="notice-card-title">{n.title}</div>
                <div className="notice-card-content">{n.content}</div>
                <div className="notice-card-footer">{n.rdate}</div>
              </div>
            </Link>
          ))}
        </div>

        <div className="paging">
          {startPage > 1 && (
            <span className="arrow" onClick={() => setSearchParams({ page: startPage - 1 })}>&lt;</span>
          )}

          {Array.from({ length: endPage - startPage + 1 }).map((_, i) => {
            const pageNum = startPage + i;
            return (
              <span
                key={pageNum}
                className={`btn-page ${page === pageNum ? 'current' : ''}`}
                onClick={() => setSearchParams({ page: pageNum })}
              >
                {pageNum}
              </span>
            );
          })}

          {endPage < totalPages && (
            <span className="arrow" onClick={() => setSearchParams({ page: endPage + 1 })}>&gt;</span>
          )}
        </div>
      </div>
    </div>
  );
}
