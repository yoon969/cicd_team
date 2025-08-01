package dev.mvc.calendar;

import java.util.ArrayList;
import java.util.Map;

import dev.mvc.calendar.CalendarVO;

public interface CalendarProcInter {

  /**
   * 등록
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
   * 검색, 전체 레코드 갯수, 페이징 버튼 생성시 필요 ★★★★★
   * @param word 검색어
   * @return 검색된 레코드 수
   */
  public int list_search_count(String word, int usersno);
  /**
   * 검색 + 페이징 목록
   * select id="list_search_paging" resultType="dev.mvc.calendar.CalendarVO" parameterType="Map"
   * @param word 검색어
   * @param now_page 현재 페이지 (1부터 시작) ★
   * @param record_per_page 페이지당 출력할 레코드 수
   * @return 검색된 사용자 목록
   */
  public ArrayList<CalendarVO> list_search_paging(String word, int now_page, int record_per_page, int usersno);
  /**
   * SPAN태그를 이용한 박스 모델의 지원, 1 페이지부터 시작
   * 현재 페이지: 11 / 22 [이전] 11 12 13 14 15 16 17 18 19 20 [다음]
   *
   * @param now_page 현재 페이지
   * @param word 검색어
   * @param list_url 페이지 버튼 클릭 시 이동할 주소, 예: /calendar/list_search
   * @param search_count 검색 레코드 수
   * @param record_per_page 페이지당 레코드 수
   * @param page_per_block 블럭당 페이지 수
   * @return 페이징 HTML 문자열
   */
  public String pagingBox(int now_page, String word, String list_url, int search_count,
                          int record_per_page, int page_per_block);
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
