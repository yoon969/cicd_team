package dev.mvc.alarm_log;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface AlarmLogDAOInter {
  public int create(AlarmLogVO vo);
  public int getAlarmno();
  public List<AlarmLogVO> listByUsersno(int usersno);
  public int check(int alarmno);
  public int deleteByUsersno(int usersno);
  public List<AlarmLogVO> listByUsersnoAndType(Map<String, Object> map);
  public int exists(AlarmLogVO vo);
  public int calendarExists(int calno);

}
