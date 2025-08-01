package dev.mvc.alarm_log;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/alarm")
public class AlarmLogCont {

  @Autowired
  @Qualifier("dev.mvc.alarm_log.AlarmLogProc")
  private dev.mvc.alarm_log.AlarmLogProcInter alarmLogProc;

//1. 알림 목록 (전체 또는 유형별)
  @GetMapping("/list")
  public String list(@RequestParam(value = "type", required = false) String type, HttpSession session, Model model) {

    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null) {
      return "redirect:/users/login_cookie_need?url=/alarm/list";
    }

    List<AlarmLogVO> list;

    if (type == null || type.isEmpty()) {
      list = alarmLogProc.listByUsersno(usersno); // 전체 알림
    } else {
      list = alarmLogProc.listByUsersnoAndType(usersno, type); // 타입별 알림
    }

    model.addAttribute("list", list);
    model.addAttribute("selectedType", type); // 탭 강조용
    return "alarm/list";
  }

  // 2. 개별 알림 읽음 처리 후 이동
  @GetMapping("/check/{alarmno}")
  public String check(@PathVariable("alarmno") int alarmno, @RequestParam("url") String url) {
    alarmLogProc.check(alarmno);
    return "redirect:" + url;
  }

  // 3. 모든 알림 읽음 처리
  @PostMapping("/check_all")
  public String checkAll(HttpSession session) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno != null) {
      List<AlarmLogVO> list = alarmLogProc.listByUsersno(usersno);
      for (AlarmLogVO vo : list) {
        alarmLogProc.check(vo.getAlarmno());
      }
    }
    return "redirect:/alarm/list";
  }

  // 4. 전체 삭제
  @PostMapping("/delete_all")
  public String deleteAll(HttpSession session) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno != null) {
      alarmLogProc.deleteByUsersno(usersno);
    }
    return "redirect:/alarm/list";
  }

  // 5. 안읽은 알림 수 조회 (Ajax 배지용)
  @ResponseBody
  @GetMapping("/unread_count")
  public int unreadCount(HttpSession session) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null)
      return 0;

    List<AlarmLogVO> list = alarmLogProc.listByUsersno(usersno);
    return (int) list.stream().filter(vo -> "N".equals(vo.getChecked())).count();
  }
}
