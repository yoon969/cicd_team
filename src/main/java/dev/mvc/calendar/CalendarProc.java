package dev.mvc.calendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import dev.mvc.alarm_log.AlarmLogDAOInter;
import dev.mvc.alarm_log.AlarmLogProcInter;
import dev.mvc.alarm_log.AlarmLogVO;
import dev.mvc.calendar.CalendarVO;
import dev.mvc.reply.ReplyDAOInter;

@Component("dev.mvc.calendar.CalendarProc")
public class CalendarProc implements CalendarProcInter{
  @Autowired // ContentsDAOInter interface를 구현한 클래스의 객체를 만들어 자동으로 할당해라.
  private CalendarDAOInter calendarDAO;
  
  @Autowired // ContentsDAOInter interface를 구현한 클래스의 객체를 만들어 자동으로 할당해라.
  private AlarmLogDAOInter alarmLogDAO;
  
  @Autowired
  @Qualifier("dev.mvc.alarm_log.AlarmLogProc")
  private AlarmLogProcInter alarmLogProc;
  
  @Override
  public ArrayList<CalendarVO> list_all() {
    ArrayList<CalendarVO> list = this.calendarDAO.list_all();
    
    return list;
  }

  @Override
  public CalendarVO read(int calendarno) {
    CalendarVO calendarVO = this.calendarDAO.read(calendarno);
    return calendarVO;
  }

  @Override
  public int increaseCnt(int calendarno) {
    int cnt = this.calendarDAO.increaseCnt(calendarno);
    return cnt;
  }

  @Override
  public int update(CalendarVO calendarVO) {
    int cnt = this.calendarDAO.update(calendarVO);
    return cnt;
  }

  @Override
  public int delete(int calendarno) {
    int cnt = this.calendarDAO.delete(calendarno);
    return cnt;
  }

  @Override
  public ArrayList<CalendarVO> list_calendar(String date) {
    ArrayList<CalendarVO> list = this.calendarDAO.list_calendar(date);
    return list;
  }

  @Override
  public ArrayList<CalendarVO> list_calendar_day(String date) {
    ArrayList<CalendarVO> list = this.calendarDAO.list_calendar_day(date);
    return list;
  }
  
  /** 
   * 검색어 기준 전체 레코드 수 반환 
   */
  @Override
  public ArrayList<CalendarVO> list_search_paging(String word, int now_page, int record_per_page, int usersno) {
    int start_num = ((now_page - 1) * record_per_page) + 1;
    int end_num = start_num + record_per_page - 1;

    Map<String, Object> map = new HashMap<>();
    map.put("word", word);
    map.put("start_num", start_num);
    map.put("end_num", end_num);
    map.put("usersno", usersno); // 관리자일 경우 0

    return this.calendarDAO.list_search_paging(map);
  }

  @Override
  public int list_search_count(String word, int usersno) {
    Map<String, Object> map = new HashMap<>();
    map.put("word", word);
    map.put("usersno", usersno);
    return this.calendarDAO.list_search_count(map);
  }


  /**
   * 페이징 박스
   * HTML 여기서 처리
   */
  @Override
  public String pagingBox(int now_page, String word, String list_url, int search_count,
                          int record_per_page, int page_per_block) {
    int total_page = (int)(Math.ceil((double)search_count / record_per_page));
    int total_grp = (int)(Math.ceil((double)total_page / page_per_block));
    int now_grp = (int)(Math.ceil((double)now_page / page_per_block));

    int start_page = ((now_grp - 1) * page_per_block) + 1;
    int end_page = now_grp * page_per_block;

    StringBuilder str = new StringBuilder();
    str.append("<div class='paging'>"); //  paging

    // ◀ 이전 화살표
    if (now_page > 1) {
      int prev_page = now_page - 1;
      str.append("<span class='arrow' onclick=\"location.href='")
         .append(list_url).append("?word=").append(word)
         .append("&now_page=").append(prev_page)
         .append("'\">&#x2039;</span> ");
    }

    // ✅ 페이지 번호
    for (int i = start_page; i <= end_page && i <= total_page; i++) {
      if (now_page == i) {
        str.append("<a class='btn-page current'>").append(i).append("</a> ");
      } else {
        str.append("<a class='btn-page' href='")
           .append(list_url).append("?word=").append(word)
           .append("&now_page=").append(i)
           .append("'>").append(i).append("</a> ");
      }
    }

    // ▶ 다음 화살표
    if (now_page < total_page) {
      int next_page = now_page + 1;
      str.append("<span class='arrow' onclick=\"location.href='")
         .append(list_url).append("?word=").append(word)
         .append("&now_page=").append(next_page)
         .append("'\">&#x203A;</span> ");
    }

    str.append("</div>"); // ✅ 끝
    return str.toString();
  }
  
  @Override
  public ArrayList<CalendarVO> list_calendar_day_by_user(Map<String, Object> map) {
    return this.calendarDAO.list_calendar_day_by_user(map);
  }
  
  /**
   * 우선순위 높임
   */
  @Override
  public int update_seqno_forward(int calendarno) {
    int cnt = this.calendarDAO.update_seqno_forward(calendarno);
    return cnt;
  }
  /**
   * 우선순위 낮춤
   */
  @Override
  public int update_seqno_backward(int calendarno) {
    int cnt = this.calendarDAO.update_seqno_backward(calendarno);
    return cnt;
  }
  
  @Override
  public ArrayList<CalendarVO> listToday(String today) {
    return this.calendarDAO.listToday(today);
  }

  @Override
  public int create(CalendarVO calendarVO) {
    int cnt = this.calendarDAO.create(calendarVO);

//    System.out.println("✅ 일정 등록 결과: " + cnt);
//    System.out.println("📌 일정 타입: " + calendarVO.getCalendarType());
//    System.out.println("📌 등록된 calendarno: " + calendarVO.getCalendarno());
//    if (cnt > 0 && "SCHEDULE".equals(calendarVO.getCalendarType())) {
//      AlarmLogVO alarm = new AlarmLogVO();
//      alarm.setUsersno(calendarVO.getUsersno());
//      alarm.setType("SCHEDULE");
//      alarm.setContent("오늘 일정: " + calendarVO.getTitle());
//      alarm.setUrl("/calendar/read/" + calendarVO.getCalendarno());
//      alarm.setChecked("N");
//
//      int result = alarmLogDAO.create(alarm);
//      System.out.println("🔔 알림 저장 결과: " + result);
//    }
    return cnt;
  }

}



