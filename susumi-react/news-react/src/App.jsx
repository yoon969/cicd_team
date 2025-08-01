import { Routes, Route } from 'react-router-dom';
import NewsList from './pages/NewsList';

function App() {
  return (
    <Routes>
      <Route path="/" element={<NewsList />} /> {/* ✅ 경로는 "/"만 */}
    </Routes>
  );
}

export default App;
