package dev.mvc.notice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.mvc.alarm_log.AlarmLogProcInter;
import dev.mvc.alarm_log.AlarmLogVO;
import dev.mvc.users.UsersProcInter;
import dev.mvc.users.UsersVO;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/notice")
public class NoticeCont {

  @Autowired
  @Qualifier("dev.mvc.notice.NoticeProc")
  private NoticeProcInter noticeProc;
  
  @Autowired
  @Qualifier("dev.mvc.alarm_log.AlarmLogProc")
  private AlarmLogProcInter alarmLogProc;
  

  @Autowired
  @Qualifier("dev.mvc.users.UsersProc")
  private UsersProcInter usersProc;


  @PostMapping("")
  public ResponseEntity<?> create(HttpSession session, @RequestBody NoticeVO vo) {
    String role = (String) session.getAttribute("role");
    if (!"admin".equals(role)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
    }

    int result = noticeProc.create(vo);

    // 🔔 공지사항 등록 알림 - 모든 사용자에게
    if (result == 1) {
      List<UsersVO> users = usersProc.list(); // 모든 사용자 가져오기

      for (UsersVO user : users) {
        // 알림 수신 여부가 off인 경우 스킵할 수도 있음 (옵션)
        AlarmLogVO alarm = new AlarmLogVO();
        alarm.setUsersno(user.getUsersno());
        alarm.setMsg("📢 새 공지사항이 등록되었습니다.");
        alarm.setContent("공지사항 알림");
        alarm.setType("NOTICE");
        alarm.setUrl("/notice/page"); // React notice 링크

        alarmLogProc.create(alarm);
      }
    }

    return ResponseEntity.ok(result);
  }

  @GetMapping("")
  public Map<String, Object> list(
    @RequestParam(name = "page", defaultValue = "1") int page,
    @RequestParam(name = "size", defaultValue = "8") int size
  ) {
    List<NoticeVO> data = noticeProc.list(page, size);
    int totalCount = noticeProc.total(); // 전체 개수 가져오기

    Map<String, Object> res = new HashMap<>();
    res.put("data", data);
    res.put("totalCount", totalCount);

    return res;
  }


  @GetMapping("/{id}")
  public NoticeVO read(@PathVariable("id") int id) {
    return noticeProc.read(id);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(HttpSession session, @PathVariable("id") int id, @RequestBody NoticeVO vo) {
    String role = (String) session.getAttribute("role");
    if (!"admin".equals(role)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
    }

    vo.setNotice_id(id);
    int result = noticeProc.update(vo);
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(HttpSession session, @PathVariable("id") int id) {
    String role = (String) session.getAttribute("role");
    if (!"admin".equals(role)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
    }

    int result = noticeProc.delete(id);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/total")
  public int total() {
    return noticeProc.total();
  }
  
  //NoticeCont.java 내부에 추가
  @GetMapping("/session")
  public Map<String, Object> getSessionInfo(HttpSession session) {
   Map<String, Object> res = new HashMap<>();
  
   Object role = session.getAttribute("role"); // 예: "admin", "user"
   if (role != null) {
     res.put("role", role);
   } else {
     res.put("role", "guest"); // 로그인 안 된 경우
   }
  
   return res;
  }
}

