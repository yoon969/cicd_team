package dev.mvc.sms_log;

import java.util.List;

public interface SmsLogProcInter {
  public int create(SmsLogVO vo);
  public List<SmsLogVO> list();
  public SmsLogVO read(int smslogno);
  public int delete(int smslogno);
  public int update(SmsLogVO vo);
}
