package dev.mvc.recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.mvc.tool.LLMKey;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/recommendation")
public class RecommendationCont {

  @Autowired
  @Qualifier("dev.mvc.users.UsersProc")  // ✅ 관리자 검증용
  private dev.mvc.users.UsersProcInter usersProc;
  
  @Autowired
  @Qualifier("dev.mvc.recommendation.RecommendationProc")
  private dev.mvc.recommendation.RecommendationProcInter RecommendationProc;
  
  private final RestTemplate restTemplate = new RestTemplate();

  /** 병원 추천 페이지 이동 */
  @GetMapping("/guide")
  public String recommendPage(HttpSession session) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null) {
      // ❌ 로그인 안 한 경우
      return "redirect:/users/login_cookie_need?url=/recommendation/guide";
  }
    return "recommendation/guide"; // templates/hospital/hospital_recommend.html
  }
//
//  @PostMapping("guide")
//  @ResponseBody
//  public Map<String, Object> adoptRecommend(@RequestBody Map<String, String> data) {
//    String url = "http://localhost:8000/guide";
//    data.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());
//
//    HttpHeaders headers = new HttpHeaders();
//    headers.setContentType(MediaType.APPLICATION_JSON);
//    HttpEntity<Map<String, String>> entity = new HttpEntity<>(data, headers);
//
//    try {
//      ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
//      return response.getBody();
//    } catch (Exception e) {
//      return Map.of("error", "FastAPI 서버 호출 실패", "detail", e.getMessage());
//    }
//  }
  @PostMapping("guide")
  @ResponseBody
  public Map<String, Object> adoptRecommend(@RequestBody Map<String, String> data, HttpSession session) {
    String url = "http://localhost:8000/guide";
    data.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, String>> entity = new HttpEntity<>(data, headers);

    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

      Map<String, Object> responseBody = response.getBody();

      // 👉 DB 저장 처리
      if (responseBody != null && responseBody.get("result") instanceof Map resultMap) {
        RecommendationVO vo = new RecommendationVO();
        vo.setUsersno((Integer) session.getAttribute("usersno"));
        vo.setAge(data.get("age"));
        vo.setExperience(data.get("experience"));
        vo.setPersonality(data.get("personality"));
        vo.setEnvironment(data.get("environment"));
        vo.setCondition(data.get("condition"));
        vo.setRecommendation((String) resultMap.get("추천"));
        vo.setDescription((String) resultMap.get("설명"));
        vo.setRealistic((String) resultMap.get("현실성"));
        vo.setCaution((String) resultMap.get("주의사항"));

        RecommendationProc.create(vo);
      }

      return responseBody;
    } catch (Exception e) {
      return Map.of("error", "FastAPI 서버 호출 실패", "detail", e.getMessage());
    }
  }
}
