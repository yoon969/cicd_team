package dev.mvc.memory;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dev.mvc.sms.MMS;
import dev.mvc.sms.SMS;
import dev.mvc.tool.LLMKey;
import dev.mvc.tool.Tool;
import dev.mvc.users.UsersProcInter;
import dev.mvc.users.UsersVO;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/memory")
public class MemoryCont {

  private final RestTemplate restTemplate = new RestTemplate();

  @Autowired
  @Qualifier("dev.mvc.users.UsersProc")  // @Service("dev.mvc.member.MemberProc")
  private UsersProcInter usersProc;
  
  @Autowired
  @Qualifier("dev.mvc.memory.MemoryProc")  // @Service("dev.mvc.member.MemberProc")
  private MemoryProcInter memoryProc;
  
  @GetMapping("/comment")
  public String recommendPage(HttpSession session, Model model) {
    return "memory/comment";
  }
      
  @GetMapping("/create")
  public String illustrate(HttpSession session, Model model) {
      Integer usersno = (Integer) session.getAttribute("usersno");

      if (usersno == null) {
          // ❌ 로그인 안 한 경우
          return "redirect:/users/login_cookie_need?url=/memory/create";
      }

//      if (!usersProc.isAdmin(session)) {
//          // ❌ 로그인했지만 관리자 아님
//          model.addAttribute("code", "access_denied");
//          model.addAttribute("msg", "이 기능은 관리자만 사용할 수 있습니다.");
//          model.addAttribute("cnt", 0);  // 다시 시도 버튼
//          return "users/message";
//      }

      // ✅ 관리자만 접근 허용
      return "memory/create";
  }
  
  @PostMapping("/illustrate")
  @ResponseBody
  public Map<String, String> illustrateproc(HttpSession session, @RequestParam("file") MultipartFile file) {
//    if (!this.usersProc.isAdmin(session)) {
//      return Map.of("error", "로그인이 필요합니다.", "redirect", "/users/login_cookie_need?url=/memory/create");
//    }

    try {
      byte[] imageBytes = file.getBytes();
      String originalFileName = file.getOriginalFilename(); // 예: photo.jpg

      // ✅ 확장자 추출 (.jpg 등)
      // String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
      String ext = ".jpg"; // Gabia 요구사항에 맞춰 강제 지정
      
      // ✅ UUID를 이용한 고유 파일명 생성
      String uuid = UUID.randomUUID().toString();
      String savedFileName = uuid + ext; // 예: a1b2c3d4-xxxx.jpg

      // ✅ FastAPI 호출
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      String base64 = Base64.getEncoder().encodeToString(imageBytes);
      Map<String, String> body = Map.of("image_base64", base64);
      HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

      String fastapiUrl = "http://localhost:8000/memory";
      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<Map> response = restTemplate.postForEntity(fastapiUrl, request, Map.class);

      Map<String, String> result = response.getBody();
      String generatedImageUrl = result.get("image_url");

      if (generatedImageUrl == null) {
        return Map.of("error", "FastAPI 처리 실패");
      }

      // ✅ 원본 이미지 저장
      String uploadDir = "C:/kd/deploy/team1/memory/upload";
      File dir = new File(uploadDir);
      if (!dir.exists()) dir.mkdirs();

      String savedFilePath = uploadDir + File.separator + savedFileName;
      file.transferTo(new File(savedFilePath)); 

      // ✅ DB 저장
      int usersno = (Integer) session.getAttribute("usersno");

      MemoryVO vo = new MemoryVO();
      vo.setImage_url("/memory/upload/" + savedFileName);         // 고유 파일명 저장
      vo.setGenerated_image_url(generatedImageUrl);        // FastAPI 생성 이미지
      vo.setUsersno(usersno);

      memoryProc.create(vo);

      return Map.of("image_url", generatedImageUrl);

    } catch (Exception e) {
      e.printStackTrace();
      return Map.of("error", "이미지 처리 실패");
    }
  }

  @RequestMapping("/read")
  public String read(Model model, @RequestParam("memoryno") int memoryno) {
    MemoryVO vo = memoryProc.read(memoryno);
    model.addAttribute("memory", vo);
    return "memory/read";  // thymeleaf에서 read.html 출력
  }

  @RequestMapping("/list")
  public String list(HttpSession session, Model model,
                     @RequestParam(name = "now_page", defaultValue = "1") int now_page) {

    
    // 세션에서 로그인한 사용자 번호 가져오기
    Integer usersno = (Integer) session.getAttribute("usersno");
    
    int record_per_page = 8;  // 4*2 구성
    int begin_of_page = (now_page - 1) * record_per_page;

    Map<String, Object> map = new HashMap<>();
    map.put("start", begin_of_page);
    map.put("cnt", record_per_page);
    map.put("usersno", usersno); // ✅ 사용자 조건 추가
    
    List<MemoryVO> list = memoryProc.list_by_paging(map);
    int total = memoryProc.total_by_usersno(usersno); // ✅ 해당 사용자의 총 개수

    String paging = Tool.pagingBox("memory/list", now_page, total, record_per_page, "green");

    model.addAttribute("list", list);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);

    return "memory/list";
  }
 
  @PostMapping("/delete")
  public String delete(@RequestParam("memoryno") int memoryno,
                       @RequestParam(name = "now_page", defaultValue = "1") int now_page) {
    memoryProc.delete(memoryno);
    return "redirect:/memory/list?now_page=" + now_page;
  }
  
  /**
   * SMS로 일러스트 전송(MMS)
   * @param payload
   * @param session
   * @return
   */
  @PostMapping("/send_sms")
  @ResponseBody
  public Map<String, String> sendSMS(HttpSession session, @RequestParam("imageUrl") String imageUrl) {
    try {    
      // ✅ 경로 점검: /storage/xxx.jpg 같은 경로가 들어옴
      String baseDir = "C:/kd/ws_java/team1_v2sbm3c/src/main/resources";
      String fullPath = baseDir + imageUrl; // /storage/cat05.jpg

      File file = new File(fullPath);
      if (!file.exists()) {
        return Map.of("status", "fail", "msg", "파일이 존재하지 않습니다.", "error", "경로: " + fullPath);
      }

      String tel = (String) session.getAttribute("tel");
      if (tel == null) {
        return Map.of("status", "fail", "msg", "전화번호 없음", "error", "세션 tel 누락");
      }
      
      String msg = "당신의 추억 일러스트가 도착했어요!\n숨숨이들에서 확인해보세요!";
      String response = MMS.sendImage(tel, fullPath, msg);

      return Map.of("status", "success", "msg", "전송 완료", "response", response);

    } catch (Exception e) {
      e.printStackTrace();
      return Map.of("status", "fail", "msg", "전송 실패", "error", e.getMessage());
    }
  }
  
//  MMS 테스트용 코드
//  @PostMapping("/send_test")
//  @ResponseBody
//  public Map<String, String> sendTestMMS() {
//    try {
//      String testPath = "C:/kd/ws_java/team1_v2sbm3c/src/main/resources/static/upload/test.jpg"; // ✅ 테스트용 JPG 파일 절대경로
//      String msg = "숨숨이 테스트 이미지 전송입니다.";
//      String tel = "01043202269";  // ✅ 수신번호 (본인 번호)
//
//      String result = MMS.sendImage(tel, testPath, msg);
//
//      return Map.of("status", "success", "result", result);
//
//    } catch (Exception e) {
//      e.printStackTrace();
//      return Map.of("status", "fail", "msg", e.getMessage());
//    }
//  }
//  
//  @GetMapping("/send_test")
//  @ResponseBody
//  public Map<String, String> sendTestMMSGet() {
//    return sendTestMMS(); // POST 메서드 재사용
//  }
  
}
