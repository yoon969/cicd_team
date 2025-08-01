package dev.mvc.post;

import dev.mvc.alarm_log.AlarmLogProc;
import dev.mvc.alarm_log.AlarmLogProcInter;
import dev.mvc.alarm_log.AlarmLogVO;
import dev.mvc.postgood.PostgoodProcInter;
import dev.mvc.postgood.PostgoodVO;
import dev.mvc.reply.ReplyProcInter;
import dev.mvc.replylike.ReplyLikeProcInter;
import dev.mvc.species.SpeciesProcInter;
import dev.mvc.species.SpeciesVO;
import dev.mvc.tool.Tool;
import dev.mvc.tool.ToolBox;
import dev.mvc.users.UsersProcInter;
import dev.mvc.users.UsersVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/post")
public class PostCont {

  @Autowired
  @Qualifier("dev.mvc.users.UsersProc")
  private UsersProcInter usersProc;

  @Autowired
  @Qualifier("dev.mvc.replylike.ReplyLikeProc")
  private ReplyLikeProcInter replyLikeProc;
  
  @Autowired
  @Qualifier("dev.mvc.alarm_log.AlarmLogProc")
  private AlarmLogProcInter alarmLogProc;

  @Autowired
  @Qualifier("dev.mvc.reply.ReplyProc")
  private ReplyProcInter replyProc;

  @Autowired
  @Qualifier("dev.mvc.species.SpeciesProc")
  private SpeciesProcInter speciesProc;

  @Autowired
  @Qualifier("dev.mvc.post.PostProc")
  private PostProcInter postProc;

  @Autowired
  @Qualifier("dev.mvc.postgood.PostgoodProc")
  private PostgoodProcInter postgoodProc;

  // ✅ 모든 메서드에서 로그인 체크 공통 적용

  private boolean checkLogin(HttpSession session) {
    return session.getAttribute("usersno") != null;
  }

//새로운 버전 추가
  private boolean checkLogin(HttpSession session, HttpServletRequest request) {
    if (session.getAttribute("usersno") == null) {
      String targetUrl = request.getRequestURI()
          + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
      session.setAttribute("targetUrl", targetUrl); // 💡 세션에 저장
      return false;
    }
    return true;
  }

  @GetMapping("/create")
  public String createForm(@RequestParam("grp") String grp, HttpSession session, Model model, RedirectAttributes ra) {
    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login";
    }
    Integer usersno = (Integer) session.getAttribute("usersno");
    UsersVO usersVO = usersProc.read(usersno);
    List<SpeciesVO> speciesList = speciesProc.list_by_grp_y(grp);

    model.addAttribute("speciesList", speciesList);
    model.addAttribute("grp", grp);
    model.addAttribute("postVO", new PostVO());

    return "post/create";
  }

  @PostMapping("/create")
  public String create(HttpSession session, PostVO postVO, RedirectAttributes ra, Model model) {
    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login";
    }

    int usersno = (int) session.getAttribute("usersno");
    postVO.setUsersno(usersno);
    postVO.setPostno(postProc.getPostSeq());

    MultipartFile mf = postVO.getFile1MF();
    if (!mf.isEmpty()) {
      String filename = mf.getOriginalFilename();
      String saved = Tool.saveFile(mf, Post.getUploadDir());
      postVO.setFile1(filename);
      postVO.setFile1saved(saved);
      postVO.setSize1(mf.getSize());
    }

    System.out.println("🔍 추출된 키워드 확인: " + postVO.getWord());

    postProc.create(postVO);
    SpeciesVO speciesVO = speciesProc.read(postVO.getSpeciesno());
    postVO.setGrp(speciesVO.getGrp());
    ra.addAttribute("grp", postVO.getGrp());

    return "redirect:/post/list_by_grp";
  }

  @GetMapping("/read")
  public String read(@RequestParam("postno") int postno, @RequestParam(name = "word", required = false) String word,
      @RequestParam(name = "now_page", defaultValue = "1") int nowPage, HttpServletRequest request, Model model,
      HttpSession session, RedirectAttributes ra) {

    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login";
    }

    Integer usersno = (Integer) session.getAttribute("usersno");
    model.addAttribute("usersno", usersno);

    postProc.increaseCnt(postno);

    PostVO postVO = postProc.read(postno);
    SpeciesVO speciesVO = speciesProc.read(postVO.getSpeciesno());

    model.addAttribute("postVO", postVO);
    model.addAttribute("speciesVO", speciesVO);
    model.addAttribute("nowPage", nowPage);

    int hartCnt = 0;
    if (usersno != null) {
      Map<String, Object> map = new HashMap<>();
      map.put("postno", postno);
      map.put("usersno", usersno);
      hartCnt = postgoodProc.hartCnt(map);
    }
    model.addAttribute("hartCnt", hartCnt);

    // ✅ 추가 필요
    model.addAttribute("recom", postVO.getRecom());

    try {
      // FastAPI 서버에 POST 요청
      String url = "http://localhost:8000/extract_keywords";
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Type", "application/json");
      con.setDoOutput(true);

      String jsonInputString = "{\"content\": \"" + postVO.getContent().replace("\"", "\\\"") + "\"}";
      try (OutputStream os = con.getOutputStream()) {
        byte[] input = jsonInputString.getBytes("utf-8");
        os.write(input, 0, input.length);
      }

      int responseCode = con.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
          StringBuilder response = new StringBuilder();
          String responseLine;
          while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
          }

          String jsonResponse = response.toString();
          ObjectMapper objectMapper = new ObjectMapper();
          Map<String, Object> resultMap = objectMapper.readValue(jsonResponse, Map.class);
          List<String> keywords = (List<String>) resultMap.get("keywords");

          // 해시태그 형태로 변환
          List<String> hashtags = keywords.stream().map(k -> "#" + k.trim()).collect(Collectors.toList());

          model.addAttribute("keywords", hashtags);

        }
      } else {
        model.addAttribute("keywords", Arrays.asList("키워드 추출 실패"));
      }
    } catch (Exception e) {
      model.addAttribute("keywords", Arrays.asList("GPT 오류: " + e.getMessage()));
    }

    if (word != null && !word.isEmpty()) {
      model.addAttribute("word", word);
      model.addAttribute("fromSearch", true);
    } else {
      model.addAttribute("fromSearch", false);
    }

    return "post/read";
  }

  @GetMapping("/update")
  public String updateForm(@RequestParam("postno") int postno, HttpSession session, Model model,
      RedirectAttributes ra) {
    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login";
    }

    Integer usersno = (Integer) session.getAttribute("usersno");
    String grade = (String) session.getAttribute("grade"); // 관리자 확인용

    PostVO postVO = postProc.read(postno);
    // ✅ 권한 확인: 작성자 또는 관리자만 가능
    if (!postVO.getUsersno().equals(usersno) && !"admin".equals(grade)) {
      ra.addFlashAttribute("msg", "수정 권한이 없습니다.");
      return "redirect:/post/read?postno=" + postno;
    }
    model.addAttribute("postVO", postVO);

    if (postVO.getSpeciesno() != null) {
      SpeciesVO speciesVO = speciesProc.read(postVO.getSpeciesno());
      model.addAttribute("speciesVO", speciesVO);
    }

    return "post/update";
  }

  @PostMapping("/update")
  public String update(@RequestParam("file1MF") MultipartFile file1MF, PostVO postVO, RedirectAttributes ra,
      HttpSession session) {
    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login";
    }

    Integer usersno = (Integer) session.getAttribute("usersno");
    String grade = (String) session.getAttribute("grade");

    PostVO origin = postProc.read(postVO.getPostno());

    // ✅ 작성자 또는 관리자만 수정 가능
    if (!origin.getUsersno().equals(usersno) && !"admin".equals(grade)) {
      ra.addFlashAttribute("msg", "수정 권한이 없습니다.");
      return "redirect:/post/read?postno=" + postVO.getPostno();
    }

    if (file1MF != null && !file1MF.isEmpty()) {
      String savedFileName = Tool.saveFile(file1MF, "C:\\kd\\deploy\\team1\\contents\\storage");
      postVO.setFile1(file1MF.getOriginalFilename());
      postVO.setFile1saved(savedFileName);
      postVO.setSize1((int) file1MF.getSize());
    }
    postProc.update(postVO);
    ra.addAttribute("postno", postVO.getPostno());
    return "redirect:/post/read";
  }

  @GetMapping("/delete")
  public String deleteConfirm(@RequestParam("postno") int postno,
      @RequestParam(name = "now_page", required = false, defaultValue = "1") int now_page,
      @RequestParam(name = "word", required = false, defaultValue = "") String word, HttpSession session, Model model,
      RedirectAttributes ra) {
    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login";
    }

    PostVO postVO = postProc.read(postno);
    model.addAttribute("postVO", postVO);
    model.addAttribute("speciesno", postVO.getSpeciesno());
    model.addAttribute("now_page", now_page);
    model.addAttribute("word", word);
    return "post/delete";
  }

  @PostMapping("/delete")
  public String delete(@RequestParam("postno") int postno, @RequestParam("speciesno") Integer speciesno,
      HttpSession session, RedirectAttributes ra, @RequestParam(name = "now_page", defaultValue = "1") int now_page,
      @RequestParam(name = "word", defaultValue = "") String word, Model model) {

    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login";
    }

    Integer usersno = (Integer) session.getAttribute("usersno");
    UsersVO usersVO = usersProc.readByUsersno(usersno); // ✅ 사용자 정보
    PostVO postVO = postProc.read(postno);

    String role = (String) session.getAttribute("role"); // 관리자 체크 (role 또는 grade 사용)
    
    if (postVO == null) {
      ra.addFlashAttribute("msg", "존재하지 않는 게시글입니다.");
      return "redirect:/post/list_by_grp";
    }

    // 🔒 권한 체크: 본인 or 관리자만 삭제 가능
    boolean isWriter = usersno.equals(postVO.getUsersno());
    boolean isAdmin = "admin".equals(role);
    
    if (!isWriter && !isAdmin) {
      ra.addFlashAttribute("msg", "삭제 권한이 없습니다.");
      String grp = speciesProc.read(postVO.getSpeciesno()).getGrp();
      String encodedGrp = URLEncoder.encode(grp, StandardCharsets.UTF_8);
      return "redirect:/post/list_by_grp?grp=" + encodedGrp;
    }

    // ✅ 댓글 좋아요 먼저 postno 기준으로 일괄 삭제
    replyLikeProc.deleteByPostno(postno); // 💡 핵심 변경

    // ✅ 댓글 삭제
    replyProc.deleteByPostno(postno);

    // ✅ 게시글 좋아요 전체 삭제 (다른 사람 포함)
    postgoodProc.deleteByPostno(postno);

    // 🔒 로그인 사용자 개인 좋아요 삭제 (중복 제거 차원에서 남겨도 무방)
    Map<String, Object> map = new HashMap<>();
    map.put("postno", postno);
    map.put("usersno", session.getAttribute("usersno"));
    postgoodProc.deleteByPostnoUsersno(map);

    // ✅ 게시글 삭제
    postProc.delete(postno);

    // ✅ speciesno → grp로 리다이렉트 주소 구성
    SpeciesVO speciesVO = speciesProc.read(speciesno);
    String grp = (speciesVO != null) ? speciesVO.getGrp() : "";
    String encodedGrp = URLEncoder.encode(grp, StandardCharsets.UTF_8);
    ra.addFlashAttribute("msg", "게시글이 성공적으로 삭제되었습니다.");

    return "redirect:/post/list_by_grp?grp=" + encodedGrp + "&now_page=" + now_page + "&word=" + word;
  }

  @PostMapping("/password_check")
  @ResponseBody
  public int passwordCheck(@RequestBody HashMap<String, Object> param, HttpSession session) {
    if (!checkLogin(session))
      return -1;
    return postProc.password_check(param);
  }

  @GetMapping("/list")
  public String list_by_speciesno(@RequestParam("speciesno") int speciesno, HttpSession session, Model model,
      RedirectAttributes ra) {

    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login_cookie_need";
    }

    // ✅ 1. 게시글 목록 불러오기
    List<PostVO> list = postProc.listBySpecies(speciesno);
    model.addAttribute("list", list);

    // ✅ 2. 종 정보 및 그룹 추출
    SpeciesVO speciesVO = speciesProc.read(speciesno);
    String grp = speciesVO.getGrp();
    List<SpeciesVO> speciesList = speciesProc.list_by_grp_y(grp);

    // ✅ 3. 페이징 관련 변수 기본값 설정
    int now_page = 1;
    int record_per_page = 10;
    int total_record = (list != null) ? list.size() : 0;

    int startNum = total_record > 0 ? total_record - (now_page - 1) * record_per_page : 0;
    int total_page = (int) Math.ceil((double) total_record / record_per_page);
    if (total_page == 0)
      total_page = 1; // 최소 1페이지 유지

    // ✅ 4. 모델에 변수 전달
    model.addAttribute("grp", grp);
    model.addAttribute("speciesList", speciesList);
    model.addAttribute("selectedSpeciesno", speciesno); // 버튼 강조용
    model.addAttribute("now_page", now_page);
    model.addAttribute("startNum", startNum);
    model.addAttribute("total_page", total_page);
    model.addAttribute("searchCount", total_record);

    return "post/list_by_grp";
  }

  @GetMapping("/list_by_grp")
  public String listByGrp(@RequestParam("grp") String grp, @RequestParam(value = "word", required = false) String word,
      @RequestParam(value = "now_page", required = false, defaultValue = "1") int now_page, HttpSession session,
      Model model, RedirectAttributes ra) {

    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login_cookie_need";
    }

    int total = postProc.countByGrp(grp); // 총 게시글 수
    int record_per_page = 10;
    int offset = (now_page - 1) * record_per_page;

    List<SpeciesVO> speciesList = speciesProc.list_by_grp_y(grp);
    if (speciesList.isEmpty()) {
      model.addAttribute("msg", "해당 그룹에 등록된 중분류가 없습니다.");
      return "post/list_by_grp";
    }

    Map<String, Object> map = new HashMap<>();
    map.put("grp", grp);
    map.put("offset", offset);
    map.put("limit", record_per_page);

    if (word != null && !word.isEmpty()) {
      map.put("word", word);
    }

    List<PostVO> postList = (word != null && !word.isEmpty()) ? postProc.searchPaging(map)
        : postProc.listPagingByGrp(map);

    int total_record = (word != null && !word.isEmpty()) ? postProc.countSearchByGrp(map) : postProc.countByGrp(grp);

    for (PostVO post : postList) {
      int replyCount = replyProc.countByPostno(post.getPostno());
      post.setReplycnt(replyCount);
    }

    int total_page = 1; // 기본값을 1로 설정하여 Null 방지
    if (total_record > 0) {
      total_page = (int) Math.ceil((double) total_record / record_per_page);
    }

    model.addAttribute("grp", grp);
    model.addAttribute("word", word);
    model.addAttribute("speciesList", speciesList);
    model.addAttribute("list", postList);
    model.addAttribute("now_page", now_page);
    model.addAttribute("total_page", total_page);
    model.addAttribute("searchCount", total_record);

    model.addAttribute("startNum", total_record - offset);

    return "post/list_by_grp";
  }

  @GetMapping("/youtube")
  public String youtubeEdit(@RequestParam("postno") int postno, HttpSession session, Model model,
      RedirectAttributes ra) {
    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login";
    }

    PostVO postVO = postProc.read(postno);
    model.addAttribute("postVO", postVO);
    return "/post/youtube";
  }

  @PostMapping("/youtube")
  public String updateYoutube(PostVO postVO, RedirectAttributes ra, HttpSession session) {
    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login";
    }

    // ✅ <p> 제거된 YouTube 코드로 저장
    String cleaned = Tool.cleanYouTube(postVO.getYoutube());
    postVO.setYoutube(cleaned);

    postProc.updateYoutube(postVO);

    ra.addAttribute("postno", postVO.getPostno());
    return "redirect:/post/read";
  }

  @GetMapping("/map")
  public String mapForm(@RequestParam("postno") int postno, HttpSession session, Model model, RedirectAttributes ra) {
    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login";
    }

    PostVO postVO = postProc.read(postno);
    model.addAttribute("postVO", postVO);
    return "post/map";
  }

  @PostMapping("/map")
  public String updateMap(PostVO postVO, RedirectAttributes ra, HttpSession session) {
    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login";
    }

    postProc.updateMap(postVO);
    ra.addAttribute("postno", postVO.getPostno());
    return "redirect:/post/read";
  }

  @GetMapping("/search")
  public String search(@RequestParam(name = "word") String word,
      @RequestParam(name = "now_page", defaultValue = "1") int nowPage, HttpSession session, Model model,
      RedirectAttributes ra) {
    if (!checkLogin(session)) {
      ra.addFlashAttribute("msg", "로그인 후 이용 가능합니다.");
      return "redirect:/users/login";
    }

    int recordPerPage = 10;
    int offset = (nowPage - 1) * recordPerPage;

    Map<String, Object> map = new HashMap<>();
    map.put("word", word);
    map.put("offset", offset);
    map.put("limit", recordPerPage);

    List<PostVO> list = postProc.searchPaging(map);
    int searchCount = postProc.countSearch(word);
    int totalPage = (int) Math.ceil((double) searchCount / recordPerPage);

    List<SpeciesVO> speciesList = speciesProc.list_all();
    model.addAttribute("speciesList", speciesList);
    model.addAttribute("list", list);
    model.addAttribute("word", word);
    model.addAttribute("nowPage", nowPage);
    model.addAttribute("totalPage", totalPage);
    model.addAttribute("searchCount", searchCount);

    return "post/search";
  }

  @GetMapping("/good")
  @ResponseBody
  public Map<String, Object> testGood(@RequestParam(name = "postno") int postno, HttpSession session) {
    Map<String, Object> result = new HashMap<>();
    if (!checkLogin(session)) {
      result.put("recom", -1);
      result.put("hartCnt", 0);
      result.put("msg", "로그인이 필요합니다.");
      return result;
    }

    Integer usersno = (Integer) session.getAttribute("usersno");
    Map<String, Object> map = new HashMap<>();
    map.put("postno", postno);
    map.put("usersno", usersno);

    boolean alreadyLiked = postgoodProc.hartCnt(map) > 0;
    if (alreadyLiked) {
      postgoodProc.deleteByPostnoUsersno(map);
      postProc.decreaseRecom(postno);
      result.put("hartCnt", 0);
      result.put("msg", "추천 취소됨");
    } else {
      PostgoodVO vo = new PostgoodVO(postno, usersno);
      postgoodProc.create(vo);
      postProc.increaseRecom(postno);
      result.put("hartCnt", 1);
      result.put("msg", "추천 추가됨");
    }

    int recom = postProc.getRecom(postno);
    result.put("recom", recom);
    return result;
  }

  @PostMapping("/good")
  @ResponseBody
  public Map<String, Object> good(@RequestBody Map<String, Integer> data, HttpSession session) {
    Map<String, Object> result = new HashMap<>();
    if (!checkLogin(session)) {
      result.put("recom", -1);
      result.put("hartCnt", 0);
      return result;
    }

    Integer postno = data.get("postno");
    Integer usersno = (Integer) session.getAttribute("usersno");

    Map<String, Object> map = new HashMap<>();
    map.put("postno", postno);
    map.put("usersno", usersno);

    boolean alreadyLiked = postgoodProc.hartCnt(map) > 0;
    if (alreadyLiked) {
      postgoodProc.deleteByPostnoUsersno(map);
      postProc.decreaseRecom(postno);
      result.put("hartCnt", 0);
    } else {
      PostgoodVO vo = new PostgoodVO(postno, usersno);
      postgoodProc.create(vo);
      postProc.increaseRecom(postno);
      result.put("hartCnt", 1);

      // 게시글 알림
      PostVO postVO = postProc.read(postno);
      if (postVO != null && postVO.getUsersno() != null && !usersno.equals(postVO.getUsersno())) {
        AlarmLogVO alarmVO = new AlarmLogVO();
        alarmVO.setUsersno(postVO.getUsersno());
        alarmVO.setMsg("회원님의 게시글에 ❤️ 좋아요가 눌렸습니다.");
        alarmVO.setContent("게시글 좋아요");
        alarmVO.setUrl("/post/read?postno=" + postno);
        alarmVO.setType("POST_LIKE");
        alarmLogProc.create(alarmVO);

        result.put("msg", alarmVO.getMsg());
        result.put("url", alarmVO.getUrl());
        result.put("type", alarmVO.getType());
      }
    }

    int recom = postProc.getRecom(postno);
    result.put("recom", recom);

    return result;
  }

  @PostMapping("/keyword")
  @ResponseBody
  public Map<String, Object> extractKeywords(@RequestBody Map<String, String> body) {
    Map<String, Object> resultMap = new HashMap<>();
    String content = body.get("content");

    try {
      // FastAPI 요청
      String apiUrl = "http://localhost:8000/extract_keywords";
      URL url = new URL(apiUrl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setDoOutput(true);

      // JSON 문자열로 content 전송
      String jsonInput = "{\"content\": \"" + content.replace("\"", "\\\"") + "\"}";
      try (OutputStream os = conn.getOutputStream()) {
        byte[] input = jsonInput.getBytes("utf-8");
        os.write(input, 0, input.length);
      }

      // 응답 읽기
      int responseCode = conn.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
          StringBuilder response = new StringBuilder();
          String line;
          while ((line = br.readLine()) != null) {
            response.append(line.trim());
          }

          ObjectMapper objectMapper = new ObjectMapper();
          Map<String, Object> responseJson = objectMapper.readValue(response.toString(), Map.class);
          resultMap.put("keywords", responseJson.get("keywords"));
        }
      } else {
        resultMap.put("keywords", List.of("키워드 추출 실패"));
      }
    } catch (Exception e) {
      resultMap.put("keywords", List.of("GPT 오류: " + e.getMessage()));
    }

    return resultMap;
  }

}
