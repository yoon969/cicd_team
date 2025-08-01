package dev.mvc.hospital;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.mvc.hospital_species.HospitalSpeciesProcInter;
import dev.mvc.hospital_species.HospitalSpeciesVO;
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
@RequestMapping("/hospital")
public class HospitalCont {

  @Autowired
  @Qualifier("dev.mvc.hospital.HospitalProc")
  private HospitalProcInter hospitalProc;
  
  @Autowired
  @Qualifier("dev.mvc.hospital_species.HospitalSpeciesProc")
  private HospitalSpeciesProcInter hospitalSpeciesProc;
  
  @Autowired
  @Qualifier("dev.mvc.users.UsersProc")  // ✅ 관리자 검증용
  private dev.mvc.users.UsersProcInter usersProc;
  
  private final RestTemplate restTemplate = new RestTemplate();

  /** 병원 추천 페이지 이동 */
  @GetMapping("/recommend")
  public String recommendPage(HttpSession session) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null) {
      // ❌ 로그인 안 한 경우
      return "redirect:/users/login_cookie_need?url=/hospital/recommend";
  }
    return "/hospital/recommend"; // templates/hospital/hospital_recommend.html
  }

  @PostMapping("/recommend")
  @ResponseBody
  public Map<String, Object> recommendproc(HttpSession session, @RequestBody Map<String, Object> data) {
    // FastAPI 호출
    String query = (String) data.get("query");
    String animal = (String) data.get("animal");

    String url = "http://localhost:8000/hospital";
    Map<String, Object> request = new HashMap<>();
    request.put("query", query);
    request.put("animal", animal);
    request.put("SpringBoot_FastAPI_KEY", new LLMKey().getSpringBoot_FastAPI_KEY());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
    Object resultObj = response.getBody().get("result");

    // 결과 가공
    if (resultObj instanceof List<?>) {
      return Map.of("result", resultObj);
    } else {
      return Map.of("error", "추천 결과 형식이 올바르지 않습니다.");
    }
  }


  /** 병원 저장 */
  @PostMapping("/save")
  @ResponseBody
  public String saveRecommendedHospitals(@RequestBody List<Map<String, String>> list) {
    int count = 0;
    List<String> skipped = new ArrayList<>();
    for (Map<String, String> item : list) {
      String address = item.get("address");

      // ✅ 중복 주소 검사
      if (hospitalProc.isDuplicateAddress(address) == 0) {
        HospitalVO vo = new HospitalVO();
        vo.setName(item.get("name"));
        vo.setAddress(address);
        vo.setTel(item.get("tel"));
        vo.setHomepage(item.get("homepage"));
        hospitalProc.create(vo);
        count++;
      } else {
        skipped.add(address);
        System.out.println("❌ 중복 주소로 저장 건너뜀: " + address);
      }
    }
    return count + "개 저장, " + skipped.size() + "개 중복 제외됨.";
  }

  
  @GetMapping("/map")
  public String map(HttpSession session) {
    return "hospital/map"; // templates/hospital/hospital_recommend.html
  }
  
  @GetMapping("/list_all")
  public String list(@RequestParam(value = "word", defaultValue = "") String word,
                     @RequestParam(value = "now_page", defaultValue = "1") int now_page,
                     Model model) {

    int recordsPerPage = 10;
    int offset = (now_page - 1) * recordsPerPage;

    Map<String, Object> map = new HashMap<>();
    map.put("word", word);
    map.put("offset", offset);
    map.put("limit", recordsPerPage);

    ArrayList<HospitalVO> list = hospitalProc.list_all_paging(map);
    int total = hospitalProc.count_all(map);

    String paging = hospitalProc.pagingBox(total, now_page, word, "/hospital/list_all", 5);
   
    model.addAttribute("list", list);
    model.addAttribute("paging", paging);
    model.addAttribute("word", word);
    model.addAttribute("now_page", now_page);

    return "hospital/list_all";  // templates/hospital/list.html
  }
  
  @GetMapping("/read")
  public String read(@RequestParam("hospitalno") int hospitalno, Model model) {
    HospitalVO hospitalVO = hospitalProc.read(hospitalno);
    List<HospitalSpeciesVO> speciesList = hospitalSpeciesProc.listByHospitalno(hospitalno); // 진료동물 목록 조회

    model.addAttribute("hospitalVO", hospitalVO);
    model.addAttribute("speciesList", speciesList); // 👉 모델에 추가

    return "hospital/read";  // templates/hospital/read.html
  }

}
