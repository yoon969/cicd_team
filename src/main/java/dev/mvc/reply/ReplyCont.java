package dev.mvc.reply;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import dev.mvc.alarm_log.AlarmLogVO;
import dev.mvc.alarm_log.AlarmLogProcInter;
import dev.mvc.post.PostVO;
import dev.mvc.post.PostProcInter;
import dev.mvc.replylike.ReplyLikeProcInter;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/reply")
public class ReplyCont {

  @Autowired
  @Qualifier("dev.mvc.reply.ReplyProc")
  private ReplyProcInter replyProc;

  @Autowired
  @Qualifier("dev.mvc.alarm_log.AlarmLogProc")
  private dev.mvc.alarm_log.AlarmLogProcInter alarmLogProc;

  @Autowired
  @Qualifier("dev.mvc.post.PostProc")
  private dev.mvc.post.PostProcInter postProc; // 게시글 작성자 정보 조회용

  @Autowired
  @Qualifier("dev.mvc.replylike.ReplyLikeProc")
  private ReplyLikeProcInter replyLikeProc;

  /** 댓글 등록 (일반/대댓글 공통) */
  @PostMapping("/create_ajax")
  @ResponseBody
  public Map<String, Object> createAjax(@RequestBody ReplyVO replyVO, HttpSession session) {
    Map<String, Object> result = new HashMap<>();

    try {
      Integer usersno = (Integer) session.getAttribute("usersno");
      if (usersno == null) {
        result.put("status", "fail");
        result.put("msg", "로그인 필요");
        return result;
      }

      replyVO.setUsersno(usersno);
      int cnt = replyProc.create(replyVO);

      if (cnt == 1) {
        result.put("status", "success");

        // 🔔 알림 생성
        int postno = replyVO.getPostno();
        PostVO postVO = postProc.read(postno); // 게시글 정보
        int postWriter = postVO.getUsersno(); // 게시글 작성자 번호

        // ✅ 일반 댓글 알림
        if (postWriter != usersno) { // 자기 글에 자기 댓글은 알림 X
          AlarmLogVO postAlarm = new AlarmLogVO();
          postAlarm.setUsersno(postWriter); // 게시글 작성자에게 알림
          postAlarm.setMsg("회원님의 게시글에 💬 댓글이 달렸습니다.");
          postAlarm.setContent("댓글 알림");
          postAlarm.setUrl("/post/read?postno=" + postno + "#reply-" + replyVO.getReplyno());
          postAlarm.setType("REPLY_CREATE");
          alarmLogProc.create(postAlarm);
        }

        // ✅ 대댓글 알림
        if (replyVO.getParentno() != 0) { // 대댓글인 경우
          ReplyVO parentReply = replyProc.read(replyVO.getParentno());
          if (parentReply != null && parentReply.getUsersno() != usersno) { // 자기 댓글에 답글은 제외
            AlarmLogVO replyAlarm = new AlarmLogVO();
            replyAlarm.setUsersno(parentReply.getUsersno()); // 댓글 작성자에게 알림
            replyAlarm.setMsg("회원님의 댓글에 💬 답글이 달렸습니다.");
            replyAlarm.setContent("대댓글 알림");
            replyAlarm.setUrl("/post/read?postno=" + postno + "#reply-" + parentReply.getReplyno());
            replyAlarm.setType("REPLY_REPLY");
            alarmLogProc.create(replyAlarm);
          }
        }

      } else {
        result.put("status", "fail");
        result.put("msg", "DB 저장 실패");
      }

    } catch (Exception e) {
      e.printStackTrace();
      result.put("status", "fail");
      result.put("msg", "서버 오류");
    }

    return result;
  }

  /** 댓글 수정 */
  @PostMapping("/update")
  @ResponseBody
  public String updateReply(@RequestBody ReplyVO replyVO) {
    try {
      int result = replyProc.update(replyVO);
      return result == 1 ? "success" : "fail";
    } catch (Exception e) {
      e.printStackTrace();
      return "fail";
    }
  }

  /** 댓글 삭제 */
  @PostMapping("/delete")
  @ResponseBody
  public String delete(@RequestParam("replyno") int replyno, @RequestParam("postno") int postno, HttpSession session) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    String role = (String) session.getAttribute("role");

    ReplyVO replyVO = replyProc.read(replyno);
    if (replyVO == null)
      return "0";

    if (usersno == null || (!usersno.equals(replyVO.getUsersno()) && (role == null || !role.trim().equals("admin")))) {
      return "-1"; // 권한 없음
    }

    replyLikeProc.deleteByReplyno(replyno);
    replyProc.delete(replyno);

    return "1";
  }

  /** 댓글 수정 (권한 검사 포함 버전) */
  @PostMapping("/update_proc")
  @ResponseBody
  public String updateProc(@RequestBody ReplyVO vo, HttpSession session) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    String role = (String) session.getAttribute("role");

    ReplyVO reply = replyProc.read(vo.getReplyno());
    if (reply == null
        || (usersno == null || (!usersno.equals(reply.getUsersno()) && (role == null || !role.equals("admin"))))) {
      return "-1";
    }

    replyProc.update(vo);
    return "1";
  }

  /** 댓글 목록 JSON (좋아요 포함된 VO 반환) */
  @GetMapping("/list_json/{postno}")
  @ResponseBody
  public List<ReplyVO> list(@PathVariable("postno") int postno, HttpSession session) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null)
      usersno = -1;

    Map<String, Object> map = new HashMap<>();
    map.put("postno", postno);
    map.put("usersno", usersno);

    return replyProc.listWithLikeVO(map); // 대댓글 + 좋아요 + 본인 여부 포함된 목록
  }
}
