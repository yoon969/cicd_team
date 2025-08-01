package dev.mvc.alarm_log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dev.mvc.alarm_log.AlarmLogProc")
public class AlarmLogProc implements AlarmLogProcInter {
  @Autowired
  private AlarmLogDAOInter alarmLogDAO;

  @Override
  public int create(AlarmLogVO alarmLogvo) {
    alarmLogvo.setAlarmno(alarmLogDAO.getAlarmno());
    return alarmLogDAO.create(alarmLogvo);
  }

  @Override
  public List<AlarmLogVO> listByUsersno(int usersno) {
    return alarmLogDAO.listByUsersno(usersno);
  }

  @Override
  public int check(int alarmno) {
    return alarmLogDAO.check(alarmno);
  }

  @Override
  public int deleteByUsersno(int usersno) {
    return alarmLogDAO.deleteByUsersno(usersno);
  }

  @Override
  public List<AlarmLogVO> listByUsersnoAndType(int usersno, String type) {
    Map<String, Object> map = new HashMap<>();
    map.put("usersno", usersno);
    map.put("type", type);
    return this.alarmLogDAO.listByUsersnoAndType(map);
  }

  @Override
  public int exists(AlarmLogVO vo) {
    return alarmLogDAO.exists(vo);
  }

  //구현체
  @Override
  public int calendarExists(int calno) {
    return alarmLogDAO.calendarExists(calno);
  }

}
