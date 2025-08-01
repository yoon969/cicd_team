package dev.mvc.sms_log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("dev.mvc.sms_log.SmsLogProc")
@Transactional
public class SmsLogProc implements SmsLogProcInter {

  @Autowired
  private SmsLogDAOInter smsLogDAO;

  @Override
  public int create(SmsLogVO vo) {
    return smsLogDAO.create(vo);
  }

  @Override
  public List<SmsLogVO> list() {
    return smsLogDAO.list();
  }

  @Override
  public SmsLogVO read(int smslogno) {
    return smsLogDAO.read(smslogno);
  }

  @Override
  public int delete(int smslogno) {
    return smsLogDAO.delete(smslogno);
  }

  @Override
  public int update(SmsLogVO vo) {
    return smsLogDAO.update(vo);
  }
}
