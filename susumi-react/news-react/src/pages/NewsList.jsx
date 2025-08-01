import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './NewsList.css';

function NewsList() {
  const [newsList, setNewsList] = useState([]);

  useEffect(() => {
    axios.get('/api/news/list')
      .then((res) => setNewsList(res.data))
      .catch((err) => console.error('뉴스 로딩 실패:', err));
  }, []);

  return (
    <div className="news-container">
      <h2 className="news-title">📰 실시간 뉴스</h2>
      <ul className="news-list">
        {newsList.map((news, idx) => (
          <li key={idx} className="news-item">
            <h3 className="news-headline">{news.title}</h3>
            <p className="news-desc">{news.description}</p>
            <small className="news-date">{news.pubDate}</small>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default NewsList;
