package dev.mvc.calendar;

import dev.mvc.sms.SMS;
import dev.mvc.sms_log.SmsLogProcInter;
import dev.mvc.sms_log.SmsLogVO;
import dev.mvc.users.UsersProcInter;
import dev.mvc.alarm_log.AlarmLogProcInter;
import dev.mvc.alarm_log.AlarmLogVO;
import dev.mvc.calendar.CalendarVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class CalendarScheduler {

  @Autowired
  @Qualifier("dev.mvc.calendar.CalendarProc")
  private CalendarProcInter calendarProc;
  
  @Autowired
  @Qualifier("dev.mvc.alarm_log.AlarmLogProc")
  private AlarmLogProcInter alarmLogProc;

  @Autowired
  @Qualifier("dev.mvc.users.UsersProc")
  private UsersProcInter usersProc;

  @Autowired
  @Qualifier("dev.mvc.sms_log.SmsLogProc")  // ✅ 로그 저장용 주입
  private SmsLogProcInter smsLogProc;

   @Scheduled(cron = "0 0 8 * * *")  // 매일 오전 8시
//   @Scheduled(cron = "*/10 * * * * *") // 테스트용: 10초마다 실행
  public void sendTodayScheduleSMS() {
    String today = LocalDate.now().toString();  // 예: "2025-07-17"
    ArrayList<CalendarVO> todaySchedules = calendarProc.listToday(today);

    for (CalendarVO vo : todaySchedules) {
      int usersno = vo.getUsersno();
      String phone = usersProc.getPhone(usersno);
      String title = vo.getTitle();

      if (phone != null && !phone.isBlank()) {
        try {
          String response = SMS.sendScheduleNotice(phone, title);
          System.out.println("✅ SMS 전송 완료: " + phone + " - " + title);
          System.out.println("📡 응답: " + response);

          // ✅ 로그 저장
          SmsLogVO logVO = new SmsLogVO();
          logVO.setRecipient(phone);
          logVO.setMessage(title);
          logVO.setSendTime(java.time.LocalDateTime.now());
          logVO.setStatus(response.contains("\"code\":\"200\"") ? "SUCCESS" : "FAIL");
          logVO.setResponseMsg(response);
          logVO.setRetryCount(0);
          smsLogProc.create(logVO);  // ← 반드시 호출!

       // ✅ 3) 알림 로그도 함께 생성
          AlarmLogVO alarmVO = new AlarmLogVO();
          alarmVO.setUsersno(usersno);
          alarmVO.setMsg("오늘 일정: " + title);
          alarmVO.setContent("일정 알림");
          alarmVO.setUrl("/calendar/read/" + vo.getCalendarno());
          alarmVO.setType("SCHEDULE");  // 예: 댓글(REPLY), 좋아요(REPLY_LIKE)와 구분
          alarmLogProc.create(alarmVO);
        } catch (Exception e) {
          System.err.println("❌ SMS 전송 실패: " + phone + " - " + title);
          e.printStackTrace();
        }
      }
    }
  }
}

