package dev.mvc.notice;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoticeDAOInter {
  public int create(NoticeVO vo);
  public List<NoticeVO> list(@Param("start") int start, @Param("end") int end);
  public NoticeVO read(int notice_id);
  public int update(NoticeVO vo);
  public int delete(int notice_id);
  public int total();
}