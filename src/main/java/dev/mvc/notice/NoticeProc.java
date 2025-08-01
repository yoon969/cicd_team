package dev.mvc.notice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mvc.memory.MemoryDAOInter;

@Component("dev.mvc.notice.NoticeProc")
public class NoticeProc implements NoticeProcInter {
  @Autowired
  private NoticeDAOInter noticeDAO;
  
  @Override
  public int create(NoticeVO vo) { 
    return noticeDAO.create(vo); 
  }

  @Override
  public List<NoticeVO> list(int page, int size) {
    int start = (page - 1) * size + 1;       // 예: page=1 → 1
    int end = page * size;                   // 예: page=1 → 8
    return noticeDAO.list(start, end);       // ✅ XML에 start, end 전달
  }

  @Override
  public NoticeVO read(int notice_id) { 
    return noticeDAO.read(notice_id); 
  }

  @Override
  public int update(NoticeVO vo) {
    return noticeDAO.update(vo);
  }

  @Override
  public int delete(int notice_id) { 
    return noticeDAO.delete(notice_id); 
  }

  @Override
  public int total() { 
    return noticeDAO.total(); 
  }
}
