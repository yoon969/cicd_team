import React, { useState, useEffect } from 'react';
import FeedbackForm from './components/feedback/FeedbackForm';
import FeedbackList from './components/feedback/FeedbackList';
import axios from 'axios';

function App() {
  const [feedbacks, setFeedbacks] = useState([]);

  const fetchList = () => {
    axios.get('/api/feedback/list', { withCredentials: true })
      .then(res => setFeedbacks(res.data))
      .catch(err => console.error('목록 불러오기 실패:', err));
  };

  useEffect(() => {
    fetchList();
  }, []);

  return (
    <div className="App" style={{ padding: '2rem' }}>
      <h2 style={{ textAlign: "center" }}>🐹 숨숨이들 피드백</h2>

      <div className="feedback-wrapper">
        <div className="feedback-form-box">
          <FeedbackForm onSuccess={fetchList} />
        </div>
        <div className="feedback-list-box">
          <FeedbackList feedbacks={feedbacks} onDeleteSuccess={fetchList} />
        </div>
      </div>
    </div>
  );
}

export default App;
