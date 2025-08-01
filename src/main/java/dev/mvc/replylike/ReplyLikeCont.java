package dev.mvc.replylike;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import dev.mvc.alarm_log.AlarmLogProcInter;
import dev.mvc.alarm_log.AlarmLogVO;
import dev.mvc.reply.ReplyProcInter;
import dev.mvc.reply.ReplyVO;

@Controller
@RequestMapping("/replylike")
public class ReplyLikeCont {

  @Autowired
  @Qualifier("dev.mvc.replylike.ReplyLikeProc")
  private ReplyLikeProcInter replyLikeProc;
  
  @Autowired
  @Qualifier("dev.mvc.reply.ReplyProc")
  private ReplyProcInter replyProc;
  
  @Autowired
  @Qualifier("dev.mvc.alarm_log.AlarmLogProc")
  private AlarmLogProcInter alarmLogProc;

  @PostMapping("/toggle")
  @ResponseBody
  public Map<String, Object> toggle(@RequestBody ReplyLikeVO vo, HttpSession session) {
    Map<String, Object> result = new HashMap<>();

    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null) {
      result.put("status", "fail");
      result.put("msg", "로그인 필요");
      return result;
    }

    vo.setUsersno(usersno);

    Map<String, Object> map = new HashMap<>();
    map.put("replyno", vo.getReplyno());
    map.put("usersno", usersno);

    int liked = replyLikeProc.checkLiked(map);

    if (liked == 1) {
      replyLikeProc.delete(map);
      result.put("status", "unliked");
    } else {
      replyLikeProc.create(vo);
      result.put("status", "liked");

      // ✅ 알림 추가: 댓글 작성자에게 좋아요 알림
      ReplyVO replyVO = replyProc.read(vo.getReplyno());
   // 이 부분에서 메시지를 댓글 / 대댓글로 구분해줄 수 있어요
      if (replyVO != null && !usersno.equals(replyVO.getUsersno())) {
        AlarmLogVO alarmVO = new AlarmLogVO();
        alarmVO.setUsersno(replyVO.getUsersno()); // 댓글 작성자

        if (replyVO.getParentno() != 0) { // 대댓글인 경우
          alarmVO.setMsg("회원님의 대댓글에 ❤️ 좋아요가 눌렸습니다.");
          alarmVO.setContent("대댓글 좋아요");
          alarmVO.setType("RE_REPLY_LIKE");  // 🔸 타입 다르게 구분
        } else { // 일반 댓글
          alarmVO.setMsg("회원님의 댓글에 ❤️ 좋아요가 눌렸습니다.");
          alarmVO.setContent("댓글 좋아요");
          alarmVO.setType("REPLY_LIKE");
        }

        alarmVO.setUrl("/post/read?postno=" + replyVO.getPostno() + "#reply-" + vo.getReplyno());
        alarmLogProc.create(alarmVO);
      }
    }

    int count = replyLikeProc.countByReplyno(vo.getReplyno());
    result.put("likeCount", count);

    return result;
  }
}