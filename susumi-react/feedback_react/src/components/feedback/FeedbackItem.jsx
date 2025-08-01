import React from 'react';
import './feedback.css';

const FeedbackItem = ({ item, isAdmin, onDelete  }) => {
  return (
    <div className="feedback-item">
      <p className="feedback-content">📝 {item.content}</p>
      <p className="feedback-date">{item.created_at}</p>

      {/* ✅ 유저 이름 출력
      <p>👤 작성자: {item.usersname}</p> */}

       {/* ✅ 관리자에게만 삭제 버튼 표시 */}
      {isAdmin && (
        <button className="btn-delete" onClick={() => onDelete(item.feedbackno)}>
          삭제
        </button>
      )}

    </div>
  );
};

export default FeedbackItem;
