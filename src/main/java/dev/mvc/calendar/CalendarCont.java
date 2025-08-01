package dev.mvc.calendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.mvc.species.SpeciesProcInter;
import dev.mvc.species.SpeciesVO;
import dev.mvc.species.SpeciesVOMenu;
import dev.mvc.tool.LLMKey;
import dev.mvc.tool.Tool;
import dev.mvc.post.PostVO;
import dev.mvc.users.UsersProcInter;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping(value = "/calendar")
public class CalendarCont {
  @Autowired
  @Qualifier("dev.mvc.users.UsersProc") // @Service("dev.mvc.users.UsersProc")
  private UsersProcInter usersProc;

  @Autowired
  @Qualifier("dev.mvc.species.SpeciesProc") // @Component("dev.mvc.species.SpeciesProc")
  private SpeciesProcInter speciesProc;

  @Autowired
  @Qualifier("dev.mvc.calendar.CalendarProc") // @Component("dev.mvc.calendar.CalendarProc")
  private CalendarProcInter calendarProc;

  /** 페이지당 출력할 레코드 갯수, nowPage는 1부터 시작 */
  public int record_per_page = 7;

  /** 블럭당 페이지 수, 하나의 블럭은 10개의 페이지로 구성됨 */
  public int page_per_block = 10;

  /** 페이징 목록 주소, @GetMapping(value="/list_search") */
  private String list_url = "/calendar/list_search";

  public CalendarCont() {
    System.out.println("-> CalendarCont created.");
  }

  /**
   * POST 요청시 새로고침 방지, POST 요청 처리 완료 → redirect → url → GET → forward -> html 데이터
   * 전송
   * 
   * @return
   */
  @GetMapping(value = "/post2get")
  public String post2get(Model model, @RequestParam(name = "url", defaultValue = "") String url) {
    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
    model.addAttribute("menu", menu);

    return url; // forward, /templates/...
  }

  // http://localhost:9093/calendar/create
  @GetMapping(value = "/create")
  public String create(Model model) {
    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
    model.addAttribute("menu", menu);

    return "calendar/create"; // /templates/calendar/create.html

  }

  /**
   * 등록 처리, http://localhost:9093/calendar/create
   * 
   * @return
   */
  @PostMapping(value = "/create")
  public String create_proc(HttpSession session, Model model, @ModelAttribute("calendarVO") CalendarVO calendarVO) {

    int usersno = (int) session.getAttribute("usersno");
    calendarVO.setUsersno(usersno);

//    //  type 설정 안 되어 있다면 기본값으로 설정
//    if (calendarVO.getCalendarType() == null || calendarVO.getCalendarType().trim().isEmpty()) {
//      calendarVO.setCalendarType("일반"); // 또는 "알람"
//    }

    int cnt = this.calendarProc.create(calendarVO);

    if (cnt == 1) {
      return "redirect:/calendar/list_search";
    } else {
      model.addAttribute("code", "create_fail");
    }

    model.addAttribute("cnt", cnt);

    return "calendar/msg";
  }

  @PostMapping(value = "/create", consumes = "application/json")
  @ResponseBody
  public Map<String, Object> create_json(@RequestBody CalendarVO calendarVO, HttpSession session) {
    Map<String, Object> response = new HashMap<>();

    try {
      Integer usersno = (Integer) session.getAttribute("usersno");
      if (usersno == null) {
        response.put("code", "unauthorized");
        return response;
      }

      calendarVO.setUsersno(usersno);

      int cnt = calendarProc.create(calendarVO);

      response.put("code", cnt == 1 ? "success" : "fail");
    } catch (Exception e) {
      e.printStackTrace();
      response.put("code", "error");
      response.put("msg", e.getMessage());
    }

    return response;
  }

//  /**
//   * 목록
//   * 
//   * @param model
//   * @return
//   */
//  // http://localhost:9093/species/list_all
//  @GetMapping(value = "/list_all")
//  public String list_all(Model model) {
//    ArrayList<CalendarVO> list = this.calendarProc.list_all();
//    model.addAttribute("list", list);
//
//    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
//    model.addAttribute("menu", menu);
//
//    return "calendar/list_all"; // /templates/calendar/list_all.html
//  }

  /**
   * 검색 + 페이징된 일정 목록 http://localhost:9093/calendar/list_search
   */
  @GetMapping(value = "/list_search")
  public String list_search_paging(@RequestParam(name = "word", defaultValue = "") String word,
      @RequestParam(name = "now_page", defaultValue = "1") int now_page, HttpSession session, Model model) {
    // 로그인 확인
    if (session.getAttribute("usersno") == null || session.getAttribute("role") == null) {
      return "redirect:/users/login_cookie_need?url=/calendar/list_search";
    }

    int usersno = (int) session.getAttribute("usersno");
    String role = (String) session.getAttribute("role");

    word = Tool.checkNull(word);

    // ✅ 관리자면 전체, 아니면 본인만
    int effectiveUsersno = role.equals("admin") ? 0 : usersno;

    ArrayList<CalendarVO> list = calendarProc.list_search_paging(word, now_page, record_per_page, effectiveUsersno);
    model.addAttribute("list", list);

    int search_count = calendarProc.list_search_count(word, effectiveUsersno);
    model.addAttribute("search_cnt", search_count);
    model.addAttribute("word", word);

    String paging = calendarProc.pagingBox(now_page, word, list_url, search_count, record_per_page, page_per_block);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);

    int no = search_count - ((now_page - 1) * record_per_page);
    model.addAttribute("no", no);

    return "calendar/list_search";
  }

  /**
   * 조회 http://localhost:9093/calendar/read/1
   * 
   * @return
   */
  @GetMapping(path = "/read/{calendarno}")
  public String read(Model model,
                     @PathVariable("calendarno") int calendarno,
                     RedirectAttributes redirectAttributes) {

      ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
      model.addAttribute("menu", menu);

      CalendarVO calendarVO = this.calendarProc.read(calendarno);

      if (calendarVO == null) {
          redirectAttributes.addFlashAttribute("popupMsg", "❌ 해당 일정이 존재하지 않습니다.");
          return "redirect:/alarm/list";  // 알림 페이지로 이동
      }

      this.calendarProc.increaseCnt(calendarno);
      model.addAttribute("calendarVO", calendarVO);

      return "calendar/read";
  }

  /**
   * 수정 폼 http:// localhost:9093/calendar/update?calendarno=1
   *
   */
  @GetMapping(value = "/update")
  public String update_text(HttpSession session, Model model, @RequestParam(name = "calendarno") int calendarno,
      RedirectAttributes ra) {

    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
    model.addAttribute("menu", menu);

    CalendarVO calendarVO = this.calendarProc.read(calendarno);
    model.addAttribute("calendarVO", calendarVO);

    return "calendar/update"; // /templates/calendar/update.html
  }

  /**
   * 수정 처리 http://localhost:9093/calendar/update?calendarno=1
   * 
   * @return
   */
  @PostMapping(value = "/update")
  public String update(HttpSession session, Model model, @ModelAttribute("calendarVO") CalendarVO calendarVO,
      RedirectAttributes ra) {

    this.calendarProc.update(calendarVO); // 글수정

    return "redirect:/calendar/read/" + calendarVO.getCalendarno(); // @GetMapping(value = "/read")
  }

  /**
   * 삭제 폼 http://localhost:9093/calendar/delete?calendarno=1
   */
  @GetMapping(value = "/delete")
  public String delete(HttpSession session, Model model,
      @RequestParam(name = "calendarno", defaultValue = "0") int calendarno, RedirectAttributes ra) {

    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
    model.addAttribute("menu", menu);

    CalendarVO calendarVO = this.calendarProc.read(calendarno);
    model.addAttribute("calendarVO", calendarVO);

    return "calendar/delete"; // /templates/calendar/delete.html
  }

  /**
   * 삭제 처리 http://localhost:9093/calendar/delete?calendarno=1
   * 
   * @return
   */
  @PostMapping(value = "/delete")
  public String delete_proc(HttpSession session, Model model,
      @RequestParam(name = "calendarno", defaultValue = "0") int calendarno, RedirectAttributes ra) {

    this.calendarProc.delete(calendarno);

    return "redirect:/calendar/list_search";
  }

  /**
   * 삭제처리 Ajax 전용
   * 
   * @param calendarno
   * @return
   */
  @GetMapping("/delete_proc")
  @ResponseBody
  public String delete_proc_ajax(@RequestParam("calendarno") int calendarno) {

    int cnt = this.calendarProc.delete(calendarno);

    return (cnt == 1) ? "success" : "fail";
  }

  /**
   * 특정 날짜의 목록 현재 월: http://localhost:9093/calendar/list_calendar 이전 월:
   * http://localhost:9093/calendar/list_calendar?year=2024&month=12 다음 월:
   * http://localhost:9093/calendar/list_calendar?year=2024&month=1
   * 
   * @param model
   * @return
   */
  @GetMapping(value = "/list_calendar")
  public String list_calendar(Model model, @RequestParam(name = "year", defaultValue = "0") int year,
      @RequestParam(name = "month", defaultValue = "0") int month) {

    if (year == 0) {
      // 현재 날짜를 가져옴
      LocalDate today = LocalDate.now();

      // 년도와 월 추출
      year = today.getYear();
      month = today.getMonthValue();
    }

    String month_str = String.format("%02d", month); // 두 자리 형식으로
//    System.out.println("-> month: " + month_str);

    String date = year + "-" + month;
//    System.out.println("-> date: " + date);

//    ArrayList<CalendarVO> list = this.calendarProc.list_calendar(date);
//    model.addAttribute("list", list);

    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
    model.addAttribute("menu", menu);

    model.addAttribute("year", year);
    model.addAttribute("month", month - 1); // javascript는 1월이 0임.

    return "calendar/list_calendar"; // /templates/calendar/list_calendar.html
  }

  /**
   * 특정 날짜의 목록
   * 
   * @param model
   * @return
   */
  // http://localhost:9093/calendar/list_calendar_day?labeldate=2025-05-01
  @GetMapping(value = "/list_calendar_day")
  @ResponseBody
  public String list_calendar_day(HttpSession session, Model model,
      @RequestParam(name = "labeldate", defaultValue = "") String labeldate) {

    String role = (String) session.getAttribute("role");
    Integer usersno = (Integer) session.getAttribute("usersno");

    ArrayList<CalendarVO> list;

    if ("admin".equals(role)) {
      list = this.calendarProc.list_calendar_day(labeldate); // 전체 일정
    } else {
      Map<String, Object> map = new HashMap<>();
      map.put("labeldate", labeldate);
      map.put("usersno", usersno);

      list = this.calendarProc.list_calendar_day_by_user(map);
    }

    model.addAttribute("list", list);

    JSONArray schedule_list = new JSONArray();

    for (CalendarVO calendarVO : list) {
      JSONObject schedual = new JSONObject();
      schedual.put("calendarno", calendarVO.getCalendarno());
      schedual.put("labeldate", calendarVO.getLabeldate());

      String title = calendarVO.getTitle();
      if (title == null || title.trim().isEmpty()) {
        title = "제목 없음";
      }
      schedual.put("title", title);
      schedual.put("seqno", calendarVO.getSeqno());
//      schedual.put("type", calendarVO.getCalendarType());

      schedule_list.put(schedual);
    }

    return schedule_list.toString();
  }

  @PostMapping("/summary")
  @ResponseBody
  public Map<String, Object> summaryMemo(HttpSession session, @RequestBody Map<String, String> data) {
    // 로그인 여부 확인
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null) {
      return Map.of("error", "로그인 필요");
    }

    String content = data.get("content");

    String url = "http://localhost:8000/calendar/summary";
    Map<String, Object> request = new HashMap<>();
    request.put("content", content);
    request.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

    RestTemplate restTemplate = new RestTemplate();
    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
      Map<String, Object> result = response.getBody();

      if (result != null && result.get("summary") != null && result.get("emotion") != null) {
        return Map.of("summary", result.get("summary"), "emotion", result.get("emotion"));
      } else {
        return Map.of("error", "FastAPI 응답 데이터가 비어 있습니다.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return Map.of("error", "FastAPI 호출 중 오류 발생");
    }
  }

  /**
   * 우선 순위 높임, 10 등 -> 1 등 http://localhost:9091/calendar/update_seqno_forward/1
   */
  @GetMapping(value = "/update_seqno_forward/{calendarno}")
  public String update_seqno_forward(Model model, @PathVariable("calendarno") Integer calendarno) {

    this.calendarProc.update_seqno_forward(calendarno);

    return "redirect:/calendar/list_search"; // @GetMapping(value="/list_search")
  }

  /**
   * 우선 순위 낮춤, 1 등 -> 10 등 http://localhost:9091/calendar/update_seqno_forward/1
   */
  @GetMapping(value = "/update_seqno_backward/{calendarno}")
  public String update_seqno_backward(Model model, @PathVariable("calendarno") Integer calendarno) {

    this.calendarProc.update_seqno_backward(calendarno);

    return "redirect:/calendar/list_search"; // @GetMapping(value="/list_search")
  }

}
