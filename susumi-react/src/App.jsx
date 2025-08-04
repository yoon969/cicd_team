import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import NoticeList from "./pages/NoticeList";
import NoticeDetail from "./pages/NoticeDetail";
import NoticeCreate from "./pages/NoticeCreate";
import NoticeUpdate from "./pages/NoticeUpdate";


export default function App() {
  return (
    <BrowserRouter basename="/notice/page">
      <Routes>
        <Route path="/" element={<NoticeList />} />
        <Route path="/create" element={<NoticeCreate />} />
        <Route path="/:id" element={<NoticeDetail />} />
        <Route path="*" element={<Navigate to="/" />} />
        <Route path="/update/:id" element={<NoticeUpdate />} />
      </Routes>
    </BrowserRouter>
  );
}
