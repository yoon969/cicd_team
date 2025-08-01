package dev.mvc.ai_consult;

import dev.mvc.calendar.CalendarProcInter;
import dev.mvc.calendar.CalendarVO;
import dev.mvc.tool.LLMKey;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/ai_consult")
public class AiConsultCont {

  @Autowired
  @Qualifier("dev.mvc.users.UsersProc")
  private dev.mvc.users.UsersProcInter usersProc;

  @Autowired
  @Qualifier("dev.mvc.ai_consult.AiConsultProc")
  private AiConsultProcInter aiConsultProc;
  
  @Autowired
  @Qualifier("dev.mvc.calendar.CalendarProc")
  private CalendarProcInter calendarProc;

  private final RestTemplate restTemplate = new RestTemplate();

  /** AI 상담 페이지로 이동 */
  @GetMapping("/chat")
  public String chatPage(HttpSession session) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null) {
      return "redirect:/users/login_cookie_need?url=/ai_consult/chat";
    }

    return "ai_consult/chat";  // templates/ai_consult/chat.html
  }

  @PostMapping("/chat")
  @ResponseBody
  public Map<String, Object> ask(@RequestBody Map<String, String> payload, HttpSession session) {
    String question = payload.get("question");

    String url = "http://localhost:8000/chat";
    Map<String, Object> request = new HashMap<>();
    request.put("question", question);
    request.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

      // ✅ FastAPI에서 "result" 안에 들어있기 때문에 꺼내야 함
      Map<String, Object> result = (Map<String, Object>) response.getBody().get("result");

      return Map.of("result", result);  // ✅ JS와 동일 구조로 반환
    } catch (Exception e) {
      return Map.of("error", "AI 응답 실패: " + e.getMessage());
    }
  }

//  /** 최근 질문-답변 저장 */
//  @PostMapping("/save")
//  @ResponseBody
//  public ResponseEntity<String> saveQA(@RequestBody Map<String, String> map, HttpSession session) {
//    String question = map.get("question");
//    String answer = map.get("answer");
//    String symptomTags = map.getOrDefault("symptom_tags", "");
//    String sourceType = map.getOrDefault("source_type", "");
//    Integer usersno = (Integer) session.getAttribute("usersno");
//
//    if (usersno == null) {
//      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
//    }
//
//    AiConsultVO vo = new AiConsultVO();
//    vo.setQuestion(question);
//    vo.setAnswer(answer);
//    vo.setSymptom_tags(symptomTags);  // ✅ 증상 키워드
//    vo.setSource_type(sourceType);    // ✅ 출처
//    vo.setUsersno(usersno); // ✅ 세션에서 회원 번호 저장
//
//    int result = aiConsultProc.create(vo); // ✅ 저장
//
//    return result > 0 ? ResponseEntity.ok("saved") : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DB 저장 실패");
//  }
  
  /**
   * 질문-답변 저장 (중복 체크 포함)
   * 요약, 감정분석도 저장
   */
  @PostMapping("/save")
  @ResponseBody
  public ResponseEntity<String> saveQA(@RequestBody Map<String, Object> map, HttpSession session) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
    }
    
    Map<String, Object> request = new HashMap<>();
    String question = (String) map.get("question");
    String answer = (String) map.get("answer");
    String symptomTags = (String) map.getOrDefault("symptom_tags", "");
    String sourceType = (String) map.getOrDefault("source_type", "");
    request.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());
    
    String summary = (String) map.getOrDefault("summary", "");
    int emotion = 0;
    try {
      emotion = Integer.parseInt(map.getOrDefault("emotion", 0).toString());
    } catch (Exception e) {
      // 잘못된 값이 들어온 경우 디폴트값 유지
    }

    boolean exists = aiConsultProc.existsByQuestionAnswer(usersno, question, answer);
    if (exists) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 저장된 상담입니다.");
    }

    AiConsultVO vo = new AiConsultVO();
    vo.setUsersno(usersno);
    vo.setQuestion(question);
    vo.setAnswer(answer);
    vo.setSymptom_tags(symptomTags);
    vo.setSource_type(sourceType);
    vo.setSummary(summary);
    vo.setEmotion(emotion); 
    
    // 캘린더에 저장된 정보 넘기기 
    int result = aiConsultProc.create(vo);
    if (result > 0) {
      System.out.println("-> AI 상담 저장 성공");
      // 캘린더 등록용 VO 구성

      CalendarVO calendarVO = new CalendarVO();
      calendarVO.setUsersno(usersno);
      calendarVO.setTitle("[AI 상담] " + vo.getQuestion());   //  title
      calendarVO.setContent(answer);                 //  content
      calendarVO.setEmotion(emotion);                // emotion
      calendarVO.setSummary(summary);                //  summary
      
      // 필수: labeldate (예: 오늘 날짜)
      calendarVO.setLabeldate(LocalDate.now().toString()); // "2025-06-23"

      // 필수: label (달력에 보여줄 간단한 표시용 텍스트)
      calendarVO.setLabel("AI상담");

      try {
        calendarProc.create(calendarVO); // 일정 등록 시도
      } catch (Exception e) {
        e.printStackTrace();
        // 캘린더 저장 실패 시에도 일단 AI 상담 저장은 성공이므로 성공 응답
        return ResponseEntity.ok("AI 저장 성공, 캘린더 저장 실패");
      }

      return ResponseEntity.ok("saved");
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DB 저장 실패");
    }
  }

  
//  @GetMapping("/list")
//  public String list(HttpSession session, Model model) {
//    Integer usersno = (Integer) session.getAttribute("usersno");
//    if (usersno == null) {
//      return "redirect:/users/login_cookie_need?url=/ai_consult/list";
//    }
//
//    ArrayList<AiConsultVO> list = aiConsultProc.list_by_usersno(usersno);
//    model.addAttribute("list", list);
//    return "ai_consult/list"; // templates/ai_consult/list.html
//  }
//
  @GetMapping("/delete")
  public String delete(@RequestParam("consultno") int consultno) {
    aiConsultProc.delete(consultno);
    return "redirect:/ai_consult/list";
  }
  
  @GetMapping("/list")
  public String list(HttpSession session,
                     @RequestParam(value = "word", defaultValue = "") String word,
                     @RequestParam(value = "now_page", defaultValue = "1") int now_page,
                     Model model) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null) {
      return "redirect:/users/login_cookie_need?url=/ai_consult/list";
    }

    // ✅ 페이징 설정
    int recordsPerPage = 5; // 페이지당 게시글 수
    int offset = (now_page - 1) * recordsPerPage;

    Map<String, Object> map = new HashMap<>();
    map.put("usersno", usersno);
    map.put("word", word);
    map.put("offset", offset);
    map.put("limit", recordsPerPage);

    ArrayList<AiConsultVO> list = aiConsultProc.list_by_usersno_paging(map);
    int total = aiConsultProc.count_by_usersno(map);

    // ✅ 페이징 블록 5개씩
    String paging = aiConsultProc.pagingBox(usersno, total, now_page, word, "/ai_consult/list", 5);

    model.addAttribute("list", list);
    model.addAttribute("paging", paging);
    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);

    return "ai_consult/list";
  }
  
  
  /** 
   * 요약 및 감정 분석
   * @param session
   * @param data
   * @return
   */
  @PostMapping("/summary")
  @ResponseBody
  public Map<String, Object> summaryMemo(HttpSession session, @RequestBody Map<String, String> data) {
    // 로그인 여부 확인
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null) {
      return Map.of("error", "로그인 필요");
    }

    String content = data.get("content");

    String url = "http://localhost:8000/ai_consult/summary";
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
        return Map.of(
          "summary", result.get("summary"),
          "emotion", result.get("emotion")
        );
      } else {
        return Map.of("error", "FastAPI 응답 데이터가 비어 있습니다.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return Map.of("error", "FastAPI 호출 중 오류 발생");
    }
  }

//AI 상담 상세 보기 (read)
@GetMapping("/read")
public String read(@RequestParam("consultno") int consultno, Model model, HttpSession session) {
   // 로그인 체크 (원하면)
   Integer usersno = (Integer) session.getAttribute("usersno");
   if (usersno == null) {
       // 비로그인 시 로그인 페이지로 리다이렉트, 로그인 후 read로 다시 이동
       return "redirect:/users/login_cookie_need?url=/ai_consult/read?consultno=" + consultno;
   }

   // 상세 VO 조회
   AiConsultVO vo = aiConsultProc.read(consultno);

   // 없는 번호면 목록으로 리다이렉트(예외 처리)
   if (vo == null) {
       return "redirect:/ai_consult/list";
   }

   // 본인 소유만 접근 가능하게 (권한 체크, 필요시)
   if (vo.getUsersno() != usersno) {
       // 남의 기록 접근 방지
       return "redirect:/ai_consult/list";
   }

   model.addAttribute("vo", vo);
   return "ai_consult/read";  // templates/ai_consult/read.html
}

@PostMapping("/similar")
@ResponseBody
public List<AiConsultVO> findSimilar(@RequestBody Map<String, Integer> map, HttpSession session) {
    int consultno = map.get("consultno");
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null) return List.of();

    AiConsultVO target = aiConsultProc.read(consultno);
    List<AiConsultVO> recent = aiConsultProc.listRecent();

    List<Map<String, Object>> history = new ArrayList<>();
    for (AiConsultVO vo : recent) {
        Map<String, Object> m = new HashMap<>();
        m.put("consultno", vo.getConsultno());
        m.put("question", vo.getQuestion());
        m.put("symptom_tags", vo.getSymptom_tags());
        history.add(m);
    }

    Map<String, Object> req = new HashMap<>();
    req.put("question", target.getQuestion());
    req.put("symptom_tags", target.getSymptom_tags());
    req.put("history", history);
    req.put("consultno", target.getConsultno());  // 🔹 현재 글 번호 전달
    req.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());

    String url = "http://localhost:8000/find_similar_ai";

    try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(req, headers);
        ResponseEntity<Map> response = new RestTemplate().postForEntity(url, entity, Map.class);
        List<Map<String, Object>> similars = (List<Map<String, Object>>) response.getBody().get("similars");

        List<Integer> consultnos = new ArrayList<>();
        Map<Integer, Double> simMap = new HashMap<>();
        for (Map<String, Object> s : similars) {
            Integer no = (Integer) s.get("consultno");
            Double sim = ((Number) s.get("similarity")).doubleValue();
            consultnos.add(no);
            simMap.put(no, sim);
        }

        List<AiConsultVO> result = aiConsultProc.listByConsultnos(consultnos);

        for (AiConsultVO vo : result) {
            vo.setSimilarity(simMap.get(vo.getConsultno()));
        }
        
     // 🔽 유사도 높은 순으로 직접 정렬
        result.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));
        return result;

    } catch (Exception e) {
        e.printStackTrace();
        return List.of();
    }
}

@GetMapping("/list_all")
public String list_all(@RequestParam(value = "word", defaultValue = "") String word,
                       @RequestParam(value = "now_page", defaultValue = "1") int now_page,
                       Model model) {
  
  int recordsPerPage = 5;
  int offset = (now_page - 1) * recordsPerPage;

  // 🔍 검색 + 페이징용 map
  Map<String, Object> map = new HashMap<>();
  map.put("word", word);
  map.put("offset", offset);
  map.put("limit", recordsPerPage);

  ArrayList<AiConsultVO> list = aiConsultProc.list_all_paging(map); // 페이징 + 검색
  int total = aiConsultProc.count_all(map); // 검색어 포함한 전체 건수

  String paging = aiConsultProc.pagingBox(0, total, now_page, word, "/ai_consult/list_all", 5); // ✅ usersno = 0

  model.addAttribute("list", list);
  model.addAttribute("paging", paging);
  model.addAttribute("word", word);
  model.addAttribute("now_page", now_page);

  return "ai_consult/list_all"; // 페이지 파일명
}



}
