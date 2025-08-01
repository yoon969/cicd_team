package dev.mvc.calendar;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CalendarVO {
  
  /** 일정 번호 (PK) */
  private int calendarno;

  /** 출력 날짜 (yyyy-MM-dd) */
  private String labeldate = "";

  /** 달력에 표시할 라벨 */
  private String label = "";

  /** 제목 */
  private String title = "";

  /** 내용 */
  private String content = "";

  /** 조회수 */
  private int cnt;

  /** 출력 순서 */
  private int seqno = 1;

  /** 등록일 */
  private String regdate = "";

  /** 등록한 회원 번호 (FK - users 테이블) */
  private int usersno;

  /** 요약 내용 */
  private String summary = "";

  /** 감정 분석 결과 (예: 1~5 점수 등) */
  private int emotion;

}
