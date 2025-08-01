package dev.mvc.sms_log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import dev.mvc.sms.SMS;

@Controller
@RequestMapping("/sms_log")
public class SmsLogCont {

  @Autowired
  @Qualifier("dev.mvc.sms_log.SmsLogProc") // ✅ 구현 클래스 명시
  private SmsLogProcInter smsLogProc;

  @GetMapping("/list")
  public String list(Model model) {
    List<SmsLogVO> list = smsLogProc.list();
    model.addAttribute("list", list);
    return "/sms_log/list"; // 타임리프 템플릿 경로
  }

  @GetMapping("/read/{smslogno}")
  public String read(@PathVariable int smslogno, Model model) {
    SmsLogVO vo = smsLogProc.read(smslogno);
    model.addAttribute("vo", vo);
    return "/sms_log/read";
  }

  @PostMapping("/test_send")
  @ResponseBody
  public String testSend(@RequestParam String phone) {
    try {
      String message = "[숨숨이들] 테스트 메시지입니다.";
      String response = SMS.sendScheduleNotice(phone, "테스트 일정");

      SmsLogVO vo = new SmsLogVO();
      vo.setRecipient(phone); // ✅ recipient
      vo.setMessage(message);
      vo.setSendTime(LocalDateTime.now()); // ✅ sendTime
      vo.setStatus(response.contains("\"code\":\"success\"") ? "SUCCESS" : "FAIL");
      vo.setResponseMsg(response);
      vo.setRetryCount(0);

      int result = smsLogProc.create(vo);
      System.out.println("📦 DB 저장 결과: " + result); // 🔍 로그 확인용

      return "📨 전송 요청 완료 (" + vo.getStatus() + ")";
    } catch (Exception e) {
      return "❌ 전송 중 오류: " + e.getMessage();
    }
  }

  // 기타 필요한 create, update, delete는 필요 시 추가
}
