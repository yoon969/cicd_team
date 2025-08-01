package dev.mvc.calendar;

import java.util.ArrayList;
import java.util.Map;

import dev.mvc.calendar.CalendarVO;

public interface CalendarDAOInter {

  /**
   * 등록, 추상 메소드
   * @param calendarVO
   * @return 처리된 레코드 갯수
   */
  public int create(CalendarVO calendarVO);

  /**
   * 전체 목록
   * @return 일정 목록
   */
  public ArrayList<CalendarVO> list_all();

  /**
   * 조회
   * @param calendarno
   * @return 일정 정보
   */
  public CalendarVO read(int calendarno);

  /**
   * 조회수 증가
   * @param calendarno
   * @return 처리된 레코드 갯수
   */
  public int increaseCnt(int calendarno);

  /**
   * 글 수정
   * @param calendarVO
   * @return 처리된 레코드 갯수
   */
  public int update(CalendarVO calendarVO);

  /**
   * 삭제
   * @param calendarno
   * @return 삭제된 레코드 갯수
   */
  public int delete(int calendarno);

  /**
   * 특정 달의 조회
   * @param date 예: "2025-06"
   * @return 해당 월의 일정 목록
   */
  public ArrayList<CalendarVO> list_calendar(String date);

  /**
   * 특정 날짜의 조회
   * @param date 예: "2025-06-18"
   * @return 해당 날짜의 일정 목록
   */
  public ArrayList<CalendarVO> list_calendar_day(String date);
  
  /**
   * 요약 및 감정 분석 결과 업데이트
   * @param calendarVO
   * @return 처리된 행 수
   */
  public int updateSummaryEmotion(CalendarVO calendarVO);
  
  /**
   * 검색, 전체 레코드 갯수, 페이징 버튼 생성시 필요 ★★★★★
   * @param word 검색어
   * @return 검색된 레코드 수
   */
  public int list_search_count(Map<String, Object> map);

  /**
   * 검색, 전체 목록
   * @param map word, start_num, end_num 포함
   * @return 검색된 사용자 목록 (페이징 포함)
   */
  public ArrayList<CalendarVO> list_search_paging(Map<String, Object> map);
  
  /**
   * 유저 자기자신의 월간 일정만 출력
   * @param labeldate
   * @param usersno
   * @return
   */
  public ArrayList<CalendarVO> list_calendar_day_by_user(Map<String, Object> map);
  
  /**
   * 우선 순위 높임, 10 등 -> 1 등
   * @param int
   * @return
   */
  public int update_seqno_forward(int calendarno);

  /**
   * 우선 순위 낮춤, 1 등 -> 10 등
   * @param int
   * @return
   */
  public int update_seqno_backward(int calendarno);
  
  public ArrayList<CalendarVO> listToday(String today);

}
