import React, { useRef, useEffect, useState } from 'react';
import axios from 'axios';
import FeedbackItem from './FeedbackItem';
import './feedback.css';

const FeedbackList = ({ feedbacks, onDeleteSuccess }) => {
  const isAdmin = true;

  const handleDelete = (feedbackno) => {
    if (window.confirm('정말 삭제하시겠습니까?')) {
      axios.delete(`/api/feedback/delete/${feedbackno}`, { withCredentials: true })
        .then(() => {
          alert('삭제되었습니다.');
          if (onDeleteSuccess) onDeleteSuccess(); // ✅ 삭제 후 목록 갱신
        })
        .catch(err => {
          alert('삭제 실패: ' + (err.response?.data || '오류'));
        });
    }
  };

  return (
    <div>
      <h3>📋 피드백 목록</h3>
      {feedbacks.length === 0 ? (
        <p className="feedback-empty">등록된 피드백이 없습니다.</p>
      ) : (
        feedbacks.map(item => (
          <FeedbackItem
            key={item.feedbackno}
            item={item}
            isAdmin={isAdmin}
            onDelete={handleDelete}
          />
        ))
      )}
    </div>
  );
};



export default FeedbackList;
