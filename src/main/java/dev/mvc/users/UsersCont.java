package dev.mvc.users;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.mvc.ai_consult.AiConsultProcInter;
import dev.mvc.ai_consult.AiConsultVO;
import dev.mvc.alarm_log.AlarmLogVO;
import dev.mvc.calendar.CalendarVO;
import dev.mvc.memory.Paging;
import dev.mvc.pet.PetJoinVO;
import dev.mvc.pet.PetProcInter;
import dev.mvc.pet.PetVO;
import dev.mvc.post.PostProcInter;
import dev.mvc.post.PostVO;
import dev.mvc.reply.ReplyProcInter;
import dev.mvc.reply.ReplyVO;
import dev.mvc.sms.SMS;
import dev.mvc.species.SpeciesProcInter;
import dev.mvc.species.SpeciesVO;
import dev.mvc.species.SpeciesVOMenu;
import dev.mvc.tool.Tool;
import dev.mvc.tool.ToolBox;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/users")
@Controller
public class UsersCont {
  @Autowired
  @Qualifier("dev.mvc.users.UsersProc") // @Service("dev.mvc.users.UsersProc")
  private UsersProcInter usersProc;

  @Autowired
  @Qualifier("dev.mvc.species.SpeciesProc")
  private SpeciesProcInter speciesProc;

  @Autowired
  @Qualifier("dev.mvc.pet.PetProc")
  private PetProcInter petProc;

  @Autowired
  @Qualifier("dev.mvc.reply.ReplyProc")
  private ReplyProcInter replyProc;

  @Autowired
  @Qualifier("dev.mvc.post.PostProc")
  private PostProcInter postProc;

  @Autowired
  @Qualifier("dev.mvc.ai_consult.AiConsultProc")
  private AiConsultProcInter aiConsultProc;
  
  @Autowired
  @Qualifier("dev.mvc.calendar.CalendarProc")
  private dev.mvc.calendar.CalendarProcInter calendarProc;

  @Autowired
  @Qualifier("dev.mvc.alarm_log.AlarmLogProc")
  private dev.mvc.alarm_log.AlarmLogProcInter alarmLogProc;


  /** 페이지당 출력할 레코드 갯수, nowPage는 1부터 시작 */
  public int record_per_page = 7;

  /** 블럭당 페이지 수, 하나의 블럭은 10개의 페이지로 구성됨 */
  public int page_per_block = 10;

  /** 페이징 목록 주소, @GetMapping(value="/list_search") */
  private String list_url = "/users/list_search";

  public UsersCont() {
    System.out.println("-> UsersCont created.");
  }

  @GetMapping(value = "/checkEmail", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, Integer> checkEmail(@RequestParam("email") String email) {
    int cnt = this.usersProc.checkID(email);
    return Collections.singletonMap("cnt", cnt);
  }

  /**
   * 회원 가입 폼
   * 
   * @param model
   * @param usersVO
   * @return
   */
  @GetMapping(value = "/create") // http://localhost:9091/users/create
  public String create_form(Model model, @ModelAttribute("usersVO") UsersVO usersVO) {
    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
    model.addAttribute("menu", menu);

    return "users/create"; // /template/users/create.html
  }

  @PostMapping(value = "/create")
  public String create_proc(Model model, @ModelAttribute("usersVO") UsersVO usersVO) {
    int checkID_cnt = this.usersProc.checkID(usersVO.getEmail());

    if (checkID_cnt == 0) {
      usersVO.setRole("user"); // 기본 회원 users
      int cnt = this.usersProc.create(usersVO);

      if (cnt == 1) {
        model.addAttribute("code", "create_success");
        model.addAttribute("usersname", usersVO.getUsersname());
        model.addAttribute("email", usersVO.getEmail());
      } else {
        model.addAttribute("code", "create_fail");
      }

      model.addAttribute("cnt", cnt);
    } else { // email 중복
      model.addAttribute("code", "duplicte_fail");
      model.addAttribute("cnt", 0);
    }

    return "users/msg"; // /templates/users/msg.html
  }

//  @GetMapping("/list")
//  public String list(
//      HttpSession session,
//      Model model,
//      @RequestParam(value = "now_page", defaultValue = "1") int now_page,
//      @RequestParam(value = "size", defaultValue = "10") int size
//  ) {
//      ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
//      model.addAttribute("menu", menu);
//
//      if (!this.usersProc.isAdmin(session)) {
//          return "redirect:/users/login_cookie_need?url=/users/list";
//      }
//
//      int totalRecord = usersProc.count();
//
//      int start = (now_page - 1) * size;
//      int end = now_page * size;
//      Map<String, Integer> params = new HashMap<>();
//      params.put("start", start);
//      params.put("end", end);
//
//      ArrayList<UsersVO> list = usersProc.listPaging(params);
//
//      String paramsStr = "size=" + size;
//      String paging = dev.mvc.tool.ToolBox.pagingBox(now_page, totalRecord, "/users/list", paramsStr, size);
//
//      model.addAttribute("list", list);
//      model.addAttribute("paging", paging);
//      model.addAttribute("now_page", now_page);
//      model.addAttribute("size", size);
//      model.addAttribute("totalRecord", totalRecord);
//
//      return "users/list";
//  }

  /**
   * 등록 폼 및 검색 목록 + 페이징 http://localhost:9091/users/list_search
   * http://localhost:9091/users/list_search?word=
   * http://localhost:9091/users/list_search?word=관리자&now_page=1
   * 
   * @param session
   * @param model
   * @param word     검색어
   * @param now_page 현재 페이지 번호
   * @return 사용자 목록 페이지
   */
  @GetMapping(value = "/list_search")
  public String list_search_paging(HttpSession session, Model model,
      @RequestParam(name = "word", defaultValue = "") String word,
      @RequestParam(name = "now_page", defaultValue = "1") int now_page) {

    if (this.usersProc.isAdmin(session)) {

      // 검색어 null 방지
      word = Tool.checkNull(word);

      // 사용자 목록 검색 + 페이징 처리
      ArrayList<UsersVO> list = this.usersProc.list_search_paging(word, now_page, this.record_per_page);
      model.addAttribute("list", list);

      // 상단 메뉴
      ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
      model.addAttribute("menu", menu);

      // 검색 결과 수
      int search_cnt = list.size();
      model.addAttribute("search_cnt", search_cnt);

      model.addAttribute("word", word); // 검색어 유지

      // 페이지 번호 목록 생성
      int search_count = this.usersProc.list_search_count(word);
      String paging = this.usersProc.pagingBox(now_page, word, this.list_url, search_count, this.record_per_page,
          this.page_per_block);
      model.addAttribute("paging", paging);
      model.addAttribute("now_page", now_page);

      // 일련번호 계산: 전체 수 - ((현재 페이지 - 1) * 페이지당 수)
      int no = search_count - ((now_page - 1) * this.record_per_page);
      model.addAttribute("no", no);
      System.out.println("-> no: " + no);

      return "users/list_search"; // /templates/users/list_search.html

    } else {
      return "redirect:/users/login_cookie_need?url=/users/list_search"; // 관리자 로그인 필요
    }
  }

//  /**
//   * 조회
//   * @param model
//   * @param usersno 회원 번호
//   * @return 회원 정보
//   */
//  @GetMapping(value="/read")
//  public String read(Model model,
//                            @RequestParam(name="usersno", defaultValue = "") int usersno) {
//    System.out.println("-> read usersno: " + usersno);
//    
//    UsersVO usersVO = this.usersProc.read(usersno);
//    model.addAttribute("usersVO", usersVO);
//    
//    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
//    model.addAttribute("menu", menu);
//    
//    return "users/read";  // templates/users/read.html
//  }

  /**
   * 조회
   * 
   * @param model
   * @param usersno 회원 번호
   * @return 회원 정보
   */
  @GetMapping(value = "/read")
  public String read(HttpSession session, Model model, @RequestParam(name = "usersno", defaultValue = "") int usersno) {
    // int roleno = this.usersProc.read(usersno).getRole(); // 등급 번호
    String role = (String) session.getAttribute("role"); // 등급: admin, users, ghost

    // 사용자: users && 11 ~ 20
    // if (role.equals("users") && (roleno >= 11 && roleno <= 20) && usersno ==
    // (int)session.getAttribute("usersno")) {
    // if (role.equals("users")) { // 로그인한 회원은 별다른 제약없이 다른 회원 정보 조회 가능
    if (role.equals("user") && usersno == (int) session.getAttribute("usersno")) { // 로그인 세션으로 usersno 검사
      // System.out.println("-> read usersno: " + usersno);

      UsersVO usersVO = this.usersProc.read(usersno);
      model.addAttribute("usersVO", usersVO);

      return "users/read"; // templates/users/read.html

    } else if (role.equals("admin")) {
      System.out.println("-> read usersno: " + usersno);

      UsersVO usersVO = this.usersProc.read(usersno);
      model.addAttribute("usersVO", usersVO);

      return "users/read"; // templates/users/read.html
    } else {
      return "redirect:/users/login_cookie_need"; // redirect
    }

  }

  /**
   * 수정 처리
   * 
   * @param model
   * @param usersVO
   * @return
   */
  @PostMapping(value = "/update")
  public String update_proc(Model model, @ModelAttribute("usersVO") UsersVO usersVO) {
    int cnt = this.usersProc.update(usersVO); // 수정

    if (cnt == 1) {
      model.addAttribute("code", "update_success");
      model.addAttribute("usersname", usersVO.getUsersname());
      model.addAttribute("email", usersVO.getEmail());
    } else {
      model.addAttribute("code", "update_fail");
    }

    model.addAttribute("cnt", cnt);

    return "users/msg"; // /templates/users/msg.html
  }

  /**
   * 삭제
   * 
   * @param model
   * @param usersno 회원 번호
   * @return 회원 정보
   */
  @GetMapping(value = "/delete")
  public String delete(Model model, @RequestParam(name = "usersno", defaultValue = "") Integer usersno) {
    System.out.println("-> delete usersno: " + usersno);

    UsersVO usersVO = this.usersProc.read(usersno);
    model.addAttribute("usersVO", usersVO);

    return "users/delete"; // templates/users/delete.html
  }

  /**
   * 회원 Delete process
   * 
   * @param model
   * @param usersno 삭제할 레코드 번호
   * @return
   */
  @PostMapping(value = "/delete")
  public String delete_process(Model model, @RequestParam(name = "usersno", defaultValue = "") Integer usersno) {
    int cnt = this.usersProc.delete(usersno);

    if (cnt == 1) {
      return "redirect:/users/list_search"; // @GetMapping(value="/list_search")
    } else {
      model.addAttribute("code", "delete_fail");
      return "users/msg"; // /templates/users/msg.html
    }
  }

//  /**
//   * 로그인
//   * @param model
//   * @param usersno 회원 번호
//   * @return 회원 정보
//   */
//  @GetMapping(value="/login")
//  public String login_form(Model model) {
//    return "/users/login";   // templates/users/login.html
//  }
//  
//  /**
//   * 로그인 처리
//   * @param model
//   * @param usersno 회원 번호
//   * @return 회원 정보
//   */
//  @PostMapping(value="/login")
//  public String login_proc(HttpSession session, Model model, 
//                                    @RequestParam(name="email", defaultValue = "") String email,
//                                    @RequestParam(name="passwd", defaultValue = "") String passwd) {
//    HashMap<String, Object> map = new HashMap<String, Object>();
//    map.put("email", email);
//    map.put("passwd", passwd);
//    
//    int cnt = this.usersProc.login(map);
//    System.out.println("-> login_proc cnt: " + cnt);
//    
//    model.addAttribute("cnt", cnt);
//    
//    if (cnt == 1) {
//      UsersVO usersVO = this.usersProc.readByEmail(email); // 로그인한 회원 정보를 읽어 session에 저장
//      session.setAttribute("usersno", usersVO.getUsersno());
//      session.setAttribute("email", usersVO.getEmail());
//      session.setAttribute("usersname", usersVO.getMname());
//      session.setAttribute("role", usersVO.getRole());
//      
//      return "redirect:/";
//    } else {
//      model.addAttribute("code", "login_fail");
//      return "users/msg";
//    }
//    
//  }

  /**
   * 로그아웃
   * 
   * @param model
   * @param usersno 회원 번호
   * @return 회원 정보
   */
  @GetMapping(value = "/logout")
  public String logout(HttpSession session, Model model) {
    session.invalidate(); // 모든 세션 변수 삭제
    return "redirect:/";
  }

  // ----------------------------------------------------------------------------------
  // Cookie 사용 로그인 관련 코드 시작
  // ----------------------------------------------------------------------------------
  /**
   * 로그인
   * 
   * @param model
   * @param usersno 회원 번호
   * @return 회원 정보
   */
  @GetMapping(value = "/login")
  public String login_form(Model model, HttpServletRequest request, HttpSession session) {
    // System.out.println("-> 시스템 session.email: " + session.getEmail());

    // Cookie 관련 코드---------------------------------------------------------
    Cookie[] cookies = request.getCookies();
    Cookie cookie = null;

    String ck_email = ""; // email 저장
    String ck_email_save = ""; // email 저장 여부를 체크
    String ck_passwd = ""; // passwd 저장
    String ck_passwd_save = ""; // passwd 저장 여부를 체크

    if (cookies != null) { // 쿠키가 존재한다면
      for (int i = 0; i < cookies.length; i++) {
        cookie = cookies[i]; // 쿠키 객체 추출

        if (cookie.getName().equals("ck_email")) {
          ck_email = cookie.getValue(); // email
        } else if (cookie.getName().equals("ck_email_save")) {
          ck_email_save = cookie.getValue(); // Y, N
        } else if (cookie.getName().equals("ck_passwd")) {
          ck_passwd = cookie.getValue(); // 1234
        } else if (cookie.getName().equals("ck_passwd_save")) {
          ck_passwd_save = cookie.getValue(); // Y, N
        }
      }
    }
    // ----------------------------------------------------------------------------

    // <input type='text' class="form-control" name='email' email='email'
    // th:value='${ck_email }' required="required"
    // style='wemailth: 30%;' placeholder="아이디" autofocus="autofocus">
    model.addAttribute("ck_email", ck_email);

    // <input type='checkbox' name='email_save' value='Y'
    // th:checked="${ck_email_save == 'Y'}"> 저장
    model.addAttribute("ck_email_save", ck_email_save);

    model.addAttribute("ck_passwd", ck_passwd);
    model.addAttribute("ck_passwd_save", ck_passwd_save);

//    model.addAttribute("ck_email_save", "Y");
//    model.addAttribute("ck_passwd_save", "Y");

    return "users/login_cookie"; // templates/users/login_cookie.html
  }

  /**
   * Cookie 기반 로그인 처리
   * 
   * @param session
   * @param request
   * @param response
   * @param model
   * @param email       아이디
   * @param passwd      패스워드
   * @param email_save  아이디 저장 여부
   * @param passwd_save 패스워드 저장 여부
   * @return
   */
  @PostMapping(value = "/login")
  public String login_proc(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model,
      @RequestParam(value = "email", defaultValue = "") String email,
      @RequestParam(value = "passwd", defaultValue = "") String passwd,
      @RequestParam(value = "email_save", defaultValue = "") String email_save,
      @RequestParam(value = "passwd_save", defaultValue = "") String passwd_save,
      @RequestParam(value = "url", defaultValue = "") String url) {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("email", email);
    map.put("passwd", passwd);

    System.out.println("-> email: " + email);
    System.out.println("-> passwd: " + passwd);

    int cnt = this.usersProc.login(map);
    System.out.println("-> login_proc cnt: " + cnt);

    model.addAttribute("cnt", cnt);

    if (cnt == 1) {

      UsersVO usersVO = this.usersProc.readByEmail(email);

      // 탈퇴했던 회원인지 확인
      if (usersVO.getRole().equals("ghost")) {
        model.addAttribute("code", "login_fail");
        model.addAttribute("msg", "탈퇴한 회원입니다.");
        return "users/msg";
      }

      // email를 이용하여 회원 정보 조회
      session.setAttribute("usersno", usersVO.getUsersno());
      session.setAttribute("email", usersVO.getEmail()); // 시스템 변수와 중복됨, 권장하지 않음.
      session.setAttribute("usersname", usersVO.getUsersname());
      session.setAttribute("role", usersVO.getRole());
      session.setAttribute("tel", usersVO.getTel());
      
      String today = java.time.LocalDate.now().toString();
      List<CalendarVO> todayList = calendarProc.listToday(today);

      for (CalendarVO vo : todayList) {
        if (vo.getUsersno() == usersVO.getUsersno()) {

          // ✅ 알림 객체 먼저 생성
          AlarmLogVO alarm = new AlarmLogVO();
          alarm.setUsersno(usersVO.getUsersno());
          alarm.setType("SCHEDULE");
          alarm.setContent("📅 오늘 일정: " + vo.getTitle());
          alarm.setUrl("/calendar/read/" + vo.getCalendarno());
          alarm.setChecked("N");

          // ✅ 존재 여부 체크
          int exists = alarmLogProc.exists(alarm);
          if (exists > 0) continue;  // 이미 존재하면 알림 생략

          alarmLogProc.create(alarm);  // 알림 생성
        }
      }
      
      // -------------------------------------------------------------------
      // 회원 등급 처리
      // -------------------------------------------------------------------
      if (usersVO.getRole().equals("admin")) {
        session.setAttribute("role", "admin");
      } else {
        session.setAttribute("role", "user");
      }

      System.out.println("-> role: " + session.getAttribute("role"));

      // Cookie 관련 코드---------------------------------------------------------
      // -------------------------------------------------------------------
      // email 관련 쿠기 저장
      // -------------------------------------------------------------------
      if (email_save.equals("Y")) { // email를 저장할 경우, Checkbox를 체크한 경우
        Cookie ck_email = new Cookie("ck_email", email);
        ck_email.setPath("/"); // root 폴더에 쿠키를 기록함으로 모든 경로에서 쿠기 접근 가능
        ck_email.setMaxAge(60 * 60 * 24 * 30); // 30 day, 초단위
        response.addCookie(ck_email); // email 저장
      } else { // N, email를 저장하지 않는 경우, Checkbox를 체크 해제한 경우
        Cookie ck_email = new Cookie("ck_email", "");
        ck_email.setPath("/");
        ck_email.setMaxAge(0);
        response.addCookie(ck_email); // email 저장
      }

      // email를 저장할지 선택하는 CheckBox 체크 여부
      Cookie ck_email_save = new Cookie("ck_email_save", email_save);
      ck_email_save.setPath("/");
      ck_email_save.setMaxAge(60 * 60 * 24 * 30); // 30 day
      response.addCookie(ck_email_save);
      // -------------------------------------------------------------------

      // -------------------------------------------------------------------
      // Password 관련 쿠기 저장
      // -------------------------------------------------------------------
      if (passwd_save.equals("Y")) { // 패스워드 저장할 경우
        Cookie ck_passwd = new Cookie("ck_passwd", passwd);
        ck_passwd.setPath("/");
        ck_passwd.setMaxAge(60 * 60 * 24 * 30); // 30 day
        response.addCookie(ck_passwd);
      } else { // N, 패스워드를 저장하지 않을 경우
        Cookie ck_passwd = new Cookie("ck_passwd", "");
        ck_passwd.setPath("/");
        ck_passwd.setMaxAge(0);
        response.addCookie(ck_passwd);
      }
      // passwd를 저장할지 선택하는 CheckBox 체크 여부
      Cookie ck_passwd_save = new Cookie("ck_passwd_save", passwd_save);
      ck_passwd_save.setPath("/");
      ck_passwd_save.setMaxAge(60 * 60 * 24 * 30); // 30 day
      response.addCookie(ck_passwd_save);
      // -------------------------------------------------------------------
      // ----------------------------------------------------------------------------

      if (url.length() > 0) { // 접속 요청이 있었는지 확인
        return "redirect:" + url; // redirect:/users/login_cookie_need?url=/species/list_search
      } else {
        return "redirect:/";
      }
    } else {
      model.addAttribute("code", "login_fail");
      return "users/msg";
    }
  }

  // ----------------------------------------------------------------------------------
  // Cookie 사용 로그인 관련 코드 종료
  // ----------------------------------------------------------------------------------

  /**
   * 로그인 요구에 따른 로그인 폼 출력
   * 
   * @param model
   * @param usersno 회원 번호
   * @return 회원 정보
   */
  @GetMapping(value = "/login_cookie_need")
  public String login_cookie_need(Model model, HttpServletRequest request, HttpSession session,
      @RequestParam(name = "url", defaultValue = "") String url) {

    // Cookie 관련 코드---------------------------------------------------------
    Cookie[] cookies = request.getCookies();
    Cookie cookie = null;

    String ck_email = ""; // email 저장
    String ck_email_save = ""; // email 저장 여부를 체크
    String ck_passwd = ""; // passwd 저장
    String ck_passwd_save = ""; // passwd 저장 여부를 체크

    if (cookies != null) { // 쿠키가 존재한다면
      for (int i = 0; i < cookies.length; i++) {
        cookie = cookies[i]; // 쿠키 객체 추출

        if (cookie.getName().equals("ck_email")) {
          ck_email = cookie.getValue(); // email
        } else if (cookie.getName().equals("ck_email_save")) {
          ck_email_save = cookie.getValue(); // Y, N
        } else if (cookie.getName().equals("ck_passwd")) {
          ck_passwd = cookie.getValue(); // 1234
        } else if (cookie.getName().equals("ck_passwd_save")) {
          ck_passwd_save = cookie.getValue(); // Y, N
        }
      }
    }
    // ----------------------------------------------------------------------------

    // <input type='text' class="form-control" name='email' email='email'
    // th:value='${ck_email }' required="required"
    // style='wemailth: 30%;' placeholder="아이디" autofocus="autofocus">
    model.addAttribute("ck_email", ck_email);

    // <input type='checkbox' name='email_save' value='Y'
    // th:checked="${ck_email_save == 'Y'}"> 저장
    model.addAttribute("ck_email_save", ck_email_save);

    model.addAttribute("ck_passwd", ck_passwd);
    model.addAttribute("ck_passwd_save", ck_passwd_save);

//    model.addAttribute("ck_email_save", "Y");
//    model.addAttribute("ck_passwd_save", "Y");

    model.addAttribute("url", url);

    return "users/login_cookie_need"; // templates/users/login_cookie_need.html
  }

  /**
   * 마이페이지
   * 
   * @param session
   * @param model
   * @return
   */
  @GetMapping("/mypage")
  public String mypage(HttpSession session, Model model) {
    Integer usersno = (Integer) session.getAttribute("usersno");

    if (usersno == null) {
      return "redirect:/users/login"; // 로그인 안했으면 로그인 페이지로
    }

    // 1. 사용자 정보 가져오기
    UsersVO usersVO = usersProc.read(usersno);
    model.addAttribute("usersVO", usersVO);

    // 2. 사용자 반려동물 목록 (JOIN 조회)
    ArrayList<PetJoinVO> petList = petProc.listByUsersnoJoin(usersno);
    model.addAttribute("petList", petList);

    // ✅ 3. 내가 쓴 댓글 목록 + 개수
    ArrayList<ReplyVO> myReplies = (ArrayList<ReplyVO>) replyProc.listByUsersno(usersno);
    int replyCount = replyProc.countByUsersno(usersno);
    model.addAttribute("myReplies", myReplies);
    model.addAttribute("replyCount", replyCount);

    // 4. 글 목록 + 개수
    ArrayList<PostVO> myPosts = (ArrayList<PostVO>) postProc.listByUsersno(usersno);
    int postCount = postProc.countByUsersno(usersno);
    model.addAttribute("myPosts", myPosts);
    model.addAttribute("postCount", postCount);

    // 5. AI 상담 기록 + 개수
    ArrayList<AiConsultVO> myConsults = (ArrayList<AiConsultVO>) aiConsultProc.listByUsersno(usersno);
    int aiConsultCount = aiConsultProc.countByUsersno(usersno);
    model.addAttribute("myConsults", myConsults);
    model.addAttribute("aiConsultCount", aiConsultCount);

    return "users/mypage"; // 템플릿: users/mypage.html
  }

  @GetMapping(value = "/checkName", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, Integer> checkName(@RequestParam("usersname") String usersname) {
    int cnt = this.usersProc.checkName(usersname);
    return Collections.singletonMap("cnt", cnt);
  }

  // -------------------------------------------------------------------------------------//
  // 비밀번호 찾기 기능
  // -------------------------------------------------------------------------------------//

  /**
   * 비밀번호 변경(비밀번호 찾기)
   * 
   * @return
   */
  // 📍 [1단계] 비밀번호 찾기 폼 보여주기
  @GetMapping("/find_passwd")
  public String find_passwd_form() {
    return "users/find_passwd_form"; // HTML: templates/users/find_passwd_form.html
  }

  // 📍 [2단계] 인증번호 전송 (POST: email + tel 입력 → 인증번호 생성 + 세션 저장)
  @PostMapping("/send_code")
  public String sendCode(@RequestParam("email") String email, @RequestParam("tel") String tel, HttpSession session,
      Model model) {
    Map<String, Object> map = new HashMap<>();
    map.put("email", email);
    map.put("tel", tel);

    UsersVO usersVO = usersProc.findByEmailTel(map);

    if (usersVO != null) {
      String code = String.format("%06d", new Random().nextInt(1000000));

      try {
        // SMS 발송 (하이픈 제거 후)
        String cleanTel = tel.replace("-", "");
        String smsResult = SMS.sendAuthCode(cleanTel, code);
        System.out.println("SMS 전송 응답: " + smsResult);
      } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("msg", "SMS 전송에 실패했습니다. 다시 시도해주세요.");
        return "users/find_passwd_form";
      }

      session.setAttribute("verify_code", code);
      session.setAttribute("verify_usersno", usersVO.getUsersno());

      model.addAttribute("msg", "인증번호가 전송되었습니다. (개발용: " + code + ")");
      return "users/verify_code_form";
    } else {
      model.addAttribute("msg", "일치하는 회원 정보가 없습니다.");
      return "users/find_passwd_form";
    }
  }

  // 📍 [3단계] 인증번호 확인 (POST)
  @PostMapping("/verify_code")
  public String verifyCode(@RequestParam(value = "input_code", defaultValue = "") String input_code,
      HttpSession session, Model model) {
    String savedCode = (String) session.getAttribute("verify_code");

    if (savedCode != null && savedCode.equals(input_code)) {
      model.addAttribute("msg", "인증 성공! 비밀번호를 재설정해주세요.");
      return "/users/reset_passwd_form"; // 비밀번호 재설정 화면
    } else {
      model.addAttribute("msg", "인증번호가 일치하지 않습니다.");
      return "users/verify_code_form"; // 다시 입력
    }
  }

  // 📍 [4단계] 비밀번호 재설정 처리
  @PostMapping("/reset_passwd")
  public String resetPasswd(@RequestParam(value = "new_passwd", defaultValue = "") String new_passwd,
      HttpSession session, Model model) {
    Integer usersno = (Integer) session.getAttribute("verify_usersno");

    System.out.println("세션 usersno: " + usersno);

    if (usersno != null) {
      UsersVO usersVO = new UsersVO();
      usersVO.setUsersno(usersno);
      usersVO.setPasswd(new_passwd); // 암호화는 서비스 계층에서 처리할 수도 있음

      usersProc.updatePasswd(usersVO);

      session.removeAttribute("verify_code");
      session.removeAttribute("verify_usersno");

      model.addAttribute("msg", "비밀번호가 성공적으로 변경되었습니다!");
      return "users/login_cookie"; // 로그인 화면으로 이동
    } else {
      model.addAttribute("msg", "세션이 만료되었습니다. 다시 시도해주세요.");
      return "users/find_passwd_form";
    }
  }

  // -------------------------------------------------------------------------------------//
  // 이메일 찾기 기능
  // -------------------------------------------------------------------------------------//

  // [1단계] 이메일 찾기 폼
  @GetMapping("/find_email")
  public String find_email_form() {
    return "users/find_email_form"; // HTML: 이메일 찾기 폼
  }

  // [2단계] 인증번호 전송 + SMS 발송
  @PostMapping("/send_email_code")
  public String send_email_code(@RequestParam(value = "tel", defaultValue = "") String tel, HttpSession session,
      Model model) {
    UsersVO usersVO = usersProc.findByTel(tel); // 전화번호로 사용자 조회

    if (usersVO != null) {
      // 6자리 인증번호 생성
      String code = String.format("%06d", new Random().nextInt(1000000));

      try {
        // SMS 발송 (전화번호 정제하여 하이픈 제거)
        String response = SMS.sendAuthCode(tel.replace("-", ""), code);
        System.out.println("이메일 찾기 SMS 응답: " + response);
      } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("msg", "SMS 전송에 실패했습니다.");
        return "users/find_email_form";
      }

      // 세션에 인증번호 및 전화번호 저장
      session.setAttribute("find_email_code", code);
      session.setAttribute("find_email_tel", tel);
      session.setMaxInactiveInterval(300); // 5분 유효

      model.addAttribute("msg", "인증번호가 전송되었습니다. (개발용: " + code + ")");
      return "users/verify_email_code_form";
    } else {
      model.addAttribute("msg", "해당 전화번호로 등록된 계정이 없습니다.");
      return "users/find_email_form";
    }
  }

  // [3단계] 인증번호 확인 후 이메일 반환
  @PostMapping("/verify_email_code")
  public String verify_email_code(@RequestParam("input_code") String input_code, HttpSession session, Model model) {
    String savedCode = (String) session.getAttribute("find_email_code");
    String tel = (String) session.getAttribute("find_email_tel");

    if (savedCode != null && savedCode.equals(input_code)) {
      UsersVO usersVO = usersProc.findByTel(tel);
      if (usersVO != null) {
        session.removeAttribute("find_email_code");
        session.removeAttribute("find_email_tel");

        model.addAttribute("email", usersVO.getEmail());
        return "users/show_email"; // 이메일 출력 화면
      }
    }

    model.addAttribute("msg", "인증번호가 일치하지 않습니다.");
    return "users/verify_email_code_form";
  }

  /**
   * 탈퇴 처리
   * 
   * @param session
   * @return
   */
  @PostMapping("/withdraw")
  public String withdraw(HttpSession session) {
    Integer usersno = (Integer) session.getAttribute("usersno");

    if (usersno != null) {
      usersProc.withdraw(usersno); // role = 'ghost'로 변경
      session.invalidate(); // 세션 초기화 (로그아웃)
    }

    return "redirect:/"; // 홈으로 이동
  }

  @GetMapping("/mypost_list")
  public String mypostList(HttpSession session, Model model,
      @RequestParam(value = "keyword", defaultValue = "") String keyword,
      @RequestParam(value = "sort", defaultValue = "desc") String sort,
      @RequestParam(value = "now_page", defaultValue = "1") int now_page) {

      int usersno = (int) session.getAttribute("usersno");

      int record_per_page = 6;
      int offset = (now_page - 1) * record_per_page;

      Map<String, Object> map = new HashMap<>();
      map.put("usersno", usersno);
      map.put("keyword", keyword);
      map.put("sort", sort);
      map.put("offset", offset);
      map.put("limit", record_per_page);

      List<PostVO> list = postProc.listByUsersnoPaging(map);
      int total = postProc.countByUsersnoFiltered(map);

      String paging = Paging.generate(total, now_page, "/users/mypost_list", keyword, sort);

      model.addAttribute("list", list);
      model.addAttribute("total", total);
      model.addAttribute("keyword", keyword);
      model.addAttribute("sort", sort);
      model.addAttribute("paging", paging);

      return "post/mypost_list";  // ← Thymeleaf 뷰 이름
  }
  
  @GetMapping("/myreply_list")
  public String myreplyList(HttpSession session, Model model,
      @RequestParam(value = "keyword", defaultValue = "") String keyword,
      @RequestParam(value = "sort", defaultValue = "desc") String sort,
      @RequestParam(value = "now_page", defaultValue = "1") int now_page) {

      int usersno = (int) session.getAttribute("usersno");
      int record_per_page = 6;
      int offset = (now_page - 1) * record_per_page;

      Map<String, Object> map = new HashMap<>();
      map.put("usersno", usersno);
      map.put("keyword", keyword);
      map.put("sort", sort);
      map.put("offset", offset);
      map.put("limit", record_per_page);

      List<ReplyVO> list = replyProc.listByUsersnoPaging(map);
      int total = replyProc.countByUsersnoFiltered(map);

      String paging = Paging.generate(total, now_page, "/users/myreply_list", keyword, sort);

      model.addAttribute("list", list);
      model.addAttribute("total", total);
      model.addAttribute("keyword", keyword);
      model.addAttribute("sort", sort);
      model.addAttribute("paging", paging);

      return "reply/myreply_list";
  }
  
  @GetMapping("/myconsult_list")
  public String myConsultList(HttpSession session, Model model,
    @RequestParam(value = "keyword", defaultValue = "") String keyword,
    @RequestParam(value = "sort", defaultValue = "desc") String sort,
    @RequestParam(value = "now_page", defaultValue = "1") int now_page) {

    int usersno = (int) session.getAttribute("usersno");
    int record_per_page = 6;
    int offset = (now_page - 1) * record_per_page;

    Map<String, Object> map = new HashMap<>();
    map.put("usersno", usersno);
    map.put("word", keyword);
    map.put("sort", sort);
    map.put("offset", offset);
    map.put("limit", record_per_page);

    List<AiConsultVO> list = aiConsultProc.listByUsersnoPaging(map);
    int total = aiConsultProc.countByUsersnoFiltered(map);
    String paging = Paging.generate(total, now_page, "/users/myconsult_list", keyword, sort);

    model.addAttribute("list", list);
    model.addAttribute("total", total);
    model.addAttribute("keyword", keyword);
    model.addAttribute("sort", sort);
    model.addAttribute("paging", paging);

    return "ai_consult/myconsult_list"; // 타임리프 파일 이름
  }



  /**
   * 패스워드 수정 폼
   * 
   * @param model
   * @param usersno
   * @return
   */
  @GetMapping(value = "/passwd_update")
  public String passwd_update_form(HttpSession session, Model model) {
    if (this.usersProc.isUsers(session)) {
      int usersno = (int) session.getAttribute("usersno"); // session에서 가져오기

      UsersVO usersVO = this.usersProc.read(usersno);
      model.addAttribute("usersVO", usersVO);

      return "users/passwd_update";

    } else {
      return "redirect:/users/login_cookie_need"; // redirect
    }
  }

  /**
   * 현재 패스워드 확인
   * 
   * @param session
   * @param current_passwd
   * @return 1: 일치, 0: 불일치
   */
  @PostMapping(value = "/passwd_check")
  @ResponseBody
  public String passwd_check(HttpSession session, @RequestBody String json_src) {
    System.out.println("-> json_src: " + json_src); // json_src: {"current_passwd":"1234"}

    JSONObject src = new JSONObject(json_src); // String -> JSON

    String current_passwd = (String) src.get("current_passwd"); // 값 가져오기
    System.out.println("-> current_passwd: " + current_passwd);

    try {
      Thread.sleep(3000);
    } catch (Exception e) {

    }

    int usersno = (int) session.getAttribute("usersno"); // session에서 가져오기
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("usersno", usersno);
    map.put("passwd", current_passwd);

    int cnt = this.usersProc.passwd_check(map);

    JSONObject json = new JSONObject();
    json.put("cnt", cnt); // 1: 현재 패스워드 일치
    System.out.println(json.toString());

    return json.toString();
  }

  /**
   * 패스워드 변경(마이 페이지 전용)
   * 
   * @param session
   * @param model
   * @param current_passwd 현재 패스워드
   * @param passwd         새로운 패스워드
   * @return
   */
  @PostMapping(value = "/passwd_update_proc")
  public String update_passwd_proc(HttpSession session, Model model,
      @RequestParam(value = "current_passwd", defaultValue = "") String current_passwd,
      @RequestParam(value = "passwd", defaultValue = "") String passwd) {
    if (this.usersProc.isUsers(session)) {
      int usersno = (int) session.getAttribute("usersno"); // session에서 가져오기
      HashMap<String, Object> map = new HashMap<String, Object>();
      map.put("usersno", usersno);
      map.put("passwd", current_passwd);

      int cnt = this.usersProc.passwd_check(map);

      if (cnt == 0) { // 패스워드 불일치
        model.addAttribute("code", "passwd_not_equal");
        model.addAttribute("cnt", 0);

      } else { // 패스워드 일치
        map = new HashMap<String, Object>();
        map.put("usersno", usersno);
        map.put("passwd", passwd); // 새로운 패스워드

        int passwd_change_cnt = this.usersProc.passwd_update(map);

        if (passwd_change_cnt == 1) {
          model.addAttribute("code", "passwd_change_success");
          model.addAttribute("cnt", 1);
        } else {
          model.addAttribute("code", "passwd_change_fail");
          model.addAttribute("cnt", 0);
        }
      }

      return "users/msg"; // /templates/users/msg.html
    } else {
      return "redirect:/users/login_cookie_need"; // redirect

    }
  }

  @GetMapping("/users/social/success")
  public String socialLoginSuccess(HttpSession session) {
    if (session.getAttribute("usersno") == null) {
      return "redirect:/users/login?error";
    }

    return "redirect:/"; // 또는 "/dashboard" 등 원하시는 메인 화면
  }
}
