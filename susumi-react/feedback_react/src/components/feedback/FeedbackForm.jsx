import React, { useRef, useState } from 'react';
import axios from 'axios';
import './feedback.css';

const FeedbackForm = ({ onSuccess }) => {
  const [content, setContent] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('/api/feedback/create', { content }, { withCredentials: true });
      alert('피드백이 등록되었습니다.');
      setContent('');
      if (onSuccess) onSuccess();  // ✅ 등록 후 목록 갱신
    } catch (err) {
      alert('등록 실패: ' + err.message);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h3>📝 피드백 남기기</h3>
      <textarea
        placeholder="불편한 점이나 건의사항을 적어주세요."
        value={content}
        onChange={(e) => setContent(e.target.value)}
        required
      />
      <br />
      <button type="submit" className="navbar-btn">등록</button>
    </form>
  );
};

export default FeedbackForm;


