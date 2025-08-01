package dev.mvc.alarm_log;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface AlarmLogProcInter {
  public int create(AlarmLogVO vo);
  public List<AlarmLogVO> listByUsersno(int usersno);
  public int check(int alarmno);
  public int deleteByUsersno(int usersno);
  public List<AlarmLogVO> listByUsersnoAndType(int usersno, String type);
  public int exists(AlarmLogVO vo);
  public int calendarExists(int calno);
}
