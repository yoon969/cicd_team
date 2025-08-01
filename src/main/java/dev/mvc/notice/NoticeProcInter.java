package dev.mvc.notice;

import java.util.List;

public interface NoticeProcInter {
  public int create(NoticeVO vo);
  public List<NoticeVO> list(int page, int size);
  public NoticeVO read(int notice_id);
  public int update(NoticeVO vo);
  public int delete(int notice_id);
  public int total();
}
