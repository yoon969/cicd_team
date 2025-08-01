package dev.mvc.feedback;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackCont {

  @Autowired
  @Qualifier("dev.mvc.feedback.FeedbackProc")
  FeedbackProcInter feedbackProc;

  // 피드백 등록
  @PostMapping("/create")
  public ResponseEntity<String> create(@RequestBody FeedbackVO feedbackVO, HttpSession session) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    System.out.println("✅ usersno: " + usersno);  // create 메서드에 추가
    // 세션 없을 경우 로그인 요구
    if (usersno == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
    }

    feedbackVO.setUsersno(usersno); // 세션에서 유저 번호 삽입
    feedbackProc.create(feedbackVO);
    return ResponseEntity.ok("등록 완료");
  }
  
  // 피드백 목록
  @GetMapping("/list")
  public List<FeedbackVO> list() {
    return feedbackProc.list(); // visible='Y' 필터 포함
  }
  
  // 피드백 삭제
  @DeleteMapping("/delete/{feedbackno}")
  public ResponseEntity<String> delete(@PathVariable("feedbackno") int feedbackno, HttpSession session) {
      String role = (String) session.getAttribute("role");

      if (!"admin".equals(role)) {
          return new ResponseEntity<>("관리자만 삭제할 수 있습니다.", HttpStatus.FORBIDDEN);
      }

      int cnt = this.feedbackProc.delete(feedbackno);
      if (cnt == 1) {
          return new ResponseEntity<>("삭제 성공", HttpStatus.OK);
      } else {
          return new ResponseEntity<>("삭제 실패", HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  
}
