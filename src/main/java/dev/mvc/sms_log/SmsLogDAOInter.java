package dev.mvc.sms_log;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper  // ✅ 이거 꼭 추가!
public interface SmsLogDAOInter {
  public int create(SmsLogVO vo);
  public List<SmsLogVO> list();
  public SmsLogVO read(int smslogno);
  public int delete(int smslogno);
  public int update(SmsLogVO vo);
}
