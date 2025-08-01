package dev.mvc.species;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//import dev.mvc.post.PostProcInter;
import dev.mvc.users.UsersProc;
import dev.mvc.post.PostProc;
import dev.mvc.tool.Tool;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Controller
@RequestMapping("/species")
public class SpeciesCont {
  @Autowired // Spring이 SpeciesProcInter를 구현한 SpeciesProc 클래스의 객체를 생성하여 할당
  @Qualifier("dev.mvc.species.SpeciesProc")
  private SpeciesProcInter speciesProc;

  @Autowired 
  @Qualifier("dev.mvc.users.UsersProc")
  private UsersProc usersProc;
  
  @Autowired 
  @Qualifier("dev.mvc.post.PostProc")
  private PostProc postProc;
  
//  @Autowired
//  @Qualifier("dev.mvc.post.PostProc") // @Component("dev.mvc.post.PostProc")
//  private PostProcInter postProc;  
  
  /** 페이지당 출력할 레코드 갯수, nowPage는 1부터 시작 */
  public int record_per_page = 7;

  /** 블럭당 페이지 수, 하나의 블럭은 10개의 페이지로 구성됨 */
  public int page_per_block = 10;
  
  /** 페이징 목록 주소, @GetMapping(value="/list_search") */
  private String list_url = "/species/list_search";
  
  public SpeciesCont( ) {
    System.out.println("-> SpeciesCont created.");
  }

//  @GetMapping(value="/create")  // http://localhost:9091/species/create
//  @ResponseBody
//  public String create() {
//    System.out.println("-> http://localhost:9091/species/create");
//    return "<h2>Create test</h2>";
//  }

  /**
   * 등록폼
   * // http://localhost:9091/species/create
   * // http://localhost:9091/species/create/  X
   * @return
   */
  @GetMapping(value="/create")  
  public String create(@ModelAttribute("speciesVO") SpeciesVO speciesVO) {
    speciesVO.setGrp("영화/여행/개발...");
    speciesVO.setSname("SF");
    
    return "species/create"; // /templates/species/create.html
  }

  /**
   * 등록 처리
   * Model model: controller -> html로 데이터 전송 제공
   * @Valid: @NotEmpty, @Size, @NotNull, @Min, @Max, @Pattern... 규칙 위반 검사 지원
   * SpeciesVO speciesVO: FORM 태그의 값 자동 저장, Integer.parseInt(request.getParameter("seqno")) 자동 실행
   * BindingResult bindingResult: @Valid의 결과 저장
   * @param model
   * @return
   */
  @PostMapping(value="/create")
  public String create(Model model, 
                              @Valid SpeciesVO speciesVO, 
                              BindingResult bindingResult,
                              @RequestParam(name="word", defaultValue = "") String word,
                              RedirectAttributes ra) {
    // System.out.println("-> create post");
    if (bindingResult.hasErrors() == true) {
      return "species/create"; // /templates/species/create.html 
    }
    
    // System.out.println("-> speciesVO.getSname(): " + speciesVO.getSname());
    // System.out.println("-> speciesVO.getSeqno(): " + speciesVO.getSeqno());
    
    int cnt = this.speciesProc.create(speciesVO);
    // System.out.println("-> cnt: " + cnt);
    
    if (cnt == 1) {
//      model.addAttribute("code", Tool.CREATE_SUCCESS);  
//      model.addAttribute("name", speciesVO.getSname());
      ra.addAttribute("word", word);
      // return "redirect:/species/list_all"; // @GetMapping(value="/list_all") 호출
      return "redirect:/species/list_search"; // @GetMapping(value="/list_search") 호출
    } else {
      model.addAttribute("code", Tool.CREATE_FAIL); 
    }
    
    model.addAttribute("cnt", cnt);
    
    return "species/msg";  // /templates/species/msg.html
  }
  
  /**
   * 전체 목록
   * http://localhost:9091/species/list_all
   * @param model
   * @return
   */
  @GetMapping(value="/list_all")
  public String list_all(Model model, @ModelAttribute("speciesVO") SpeciesVO speciesVO) {
    speciesVO.setGrp("");
    speciesVO.setSname("");
    
    ArrayList<SpeciesVO> list = this.speciesProc.list_all();
    model.addAttribute("list", list);
    
    // 2단 메뉴
    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
    model.addAttribute("menu", menu);
    
    // 카테고리 그룹 목록
    ArrayList<String> grpset = this.speciesProc.grpset();
    speciesVO.setGrp(String.join("/",  grpset));
    // System.out.println("-> speciesVO.getGrp(): " + speciesVO.getGrp());
    
    return "species/list_all"; // /templates/species/list_all.html
  }

//  /**
//   * 전체 목록
//   * http://localhost:9091/species/list_search
//   * @param model
//   * @return
//   */
//  @GetMapping(value="/list_search")
//  public String list_search(Model model, 
//                                    @ModelAttribute("speciesVO") SpeciesVO speciesVO,
//                                    @RequestParam(name="word", defaultValue = "") String word) {
//    speciesVO.setGrp("");
//    speciesVO.setSname("");
//    
//    ArrayList<SpeciesVO> list = this.speciesProc.list_search(word);
//    model.addAttribute("list", list);
//    
//    // 2단 메뉴
//    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
//    model.addAttribute("menu", menu);
//    
//    // 카테고리 그룹 목록
//    ArrayList<String> grpset = this.speciesProc.grpset();
//    speciesVO.setGrp(String.join("/",  grpset));
//    System.out.println("-> speciesVO.getGrp(): " + speciesVO.getGrp());
//    
//    model.addAttribute("word", word);
//    
//    int list_search_count = list.size(); // 검색된 레코드 갯수
//    model.addAttribute("list_search_count", list_search_count);
//    
//    return "species/list_search"; // /templates/species/list_search.html
//  }
  
  /**
   * 등록 폼 및 검색 목록 + 페이징
   * http://localhost:9091/species/list_search
   * http://localhost:9091/species/list_search?word=&now_page=
   * http://localhost:9091/species/list_search?word=까페&now_page=1
   * @param model
   * @return
   */
  @GetMapping(value="/list_search") 
  public String list_search_paging(HttpSession session, Model model, 
                                   @RequestParam(name="word", defaultValue = "") String word,
                                   @RequestParam(name="now_page", defaultValue="1") int now_page) {
    
    if (this.usersProc.isAdmin(session)) {
      SpeciesVO speciesVO = new SpeciesVO(); // form 초기값 전달
      // speciesVO.setGenre("분류");
      // speciesVO.setSname("카테고리 이름을 입력하세요."); // Form으로 초기값을 전달
      
      // 카테고리 그룹 목록
      ArrayList<String> list_grp = this.speciesProc.grpset();
      speciesVO.setGrp(String.join("/", list_grp)); // 기존에 등록된 그룹명 제시
      
      model.addAttribute("speciesVO", speciesVO); // 등록폼 카테고리 그룹 초기값
      
      word = Tool.checkNull(word);             // null -> ""
      
      ArrayList<SpeciesVO> list = this.speciesProc.list_search_paging(word, now_page, this.record_per_page);
      model.addAttribute("list", list);
      
//      ArrayList<SpeciesVO> menu = this.speciesProc.list_all_speciesgrp_y();
//      model.addAttribute("menu", menu);

      ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
      model.addAttribute("menu", menu);
      
      int search_cnt = list.size();
      model.addAttribute("search_cnt", search_cnt);    
      
      model.addAttribute("word", word); // 검색어
      
      // --------------------------------------------------------------------------------------
      // 페이지 번호 목록 생성
      // --------------------------------------------------------------------------------------
      int search_count = this.speciesProc.list_search_count(word);
      String paging = this.speciesProc.pagingBox(now_page, word, this.list_url, search_count, this.record_per_page, this.page_per_block);
      model.addAttribute("paging", paging);
      model.addAttribute("now_page", now_page);
      
      // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
      int no = search_count - ((now_page - 1) * this.record_per_page);
      model.addAttribute("no", no);
      System.out.println("-> no: " + no);
      // --------------------------------------------------------------------------------------    
      
      return "species/list_search";  // /templates/species/list_search.html
    } else {
      return "redirect:/users/login_cookie_need?url=/species/list_search"; // redirect
    }
    

  }  
  
  /**
   * 조회
   * http://localhost:9091/species/read/1
   * @param model
   * @return
   */
  @GetMapping(value="/read/{speciesno}")
  public String read (Model model, @PathVariable("speciesno") Integer speciesno,
                             @RequestParam(name="word", defaultValue = "") String word,
                             @RequestParam(name="now_page", defaultValue="1") int now_page) {
    // System.out.println("-> read speciesno: " + speciesno);
    
    SpeciesVO speciesVO = this.speciesProc.read(speciesno);
    model.addAttribute("speciesVO", speciesVO);
    
//    ArrayList<SpeciesVO> list = this.speciesProc.list_all();
//    model.addAttribute("list", list);
    
    // 2단 메뉴
    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
    model.addAttribute("menu", menu);
    
    // 카테고리 그룹 목록
//    ArrayList<String> grpset = this.speciesProc.grpset();
//    speciesVO.setGrp(String.join("/",  grpset));
//    System.out.println("-> speciesVO.getGrp(): " + speciesVO.getGrp());
    
    model.addAttribute("word", word);
    // System.out.println("-> word null 체크: " + word);

    // ArrayList<SpeciesVO> list = this.speciesProc.list_search(word);  // 검색 목록
    ArrayList<SpeciesVO> list = this.speciesProc.list_search_paging(word, now_page, this.record_per_page); // 검색 목록 + 페이징
    model.addAttribute("list", list);
    
    int list_search_count = this.speciesProc.list_search_count(word); // 검색된 레코드 갯수
    model.addAttribute("search_cnt", list_search_count);
    
    // --------------------------------------------------------------------------------------
    // 페이지 번호 목록 생성
    // --------------------------------------------------------------------------------------
    int search_count = this.speciesProc.list_search_count(word);
    String paging = this.speciesProc.pagingBox(now_page, word, this.list_url, search_count, this.record_per_page, this.page_per_block);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);
    
    // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
    int no = search_count - ((now_page - 1) * this.record_per_page);
    model.addAttribute("no", no);
    // System.out.println("-> no: " + no);    
    // --------------------------------------------------------------------------------------    
    
    return "species/read"; // /templates/species/read.html
  }
  
  /**
   * 수정폼
   * http://localhost:9091/species/update/1
   * @param model
   * @return
   */
  @GetMapping(value="/update/{speciesno}")
  public String update(Model model, HttpSession session,
                               @PathVariable("speciesno") Integer speciesno,
                               @RequestParam(name="word", defaultValue = "") String word,
                               @RequestParam(name="now_page", defaultValue="1") int now_page) {
    // System.out.println("-> read speciesno: " + speciesno);

    if (this.usersProc.isAdmin(session)) {
      SpeciesVO speciesVO = this.speciesProc.read(speciesno);
      model.addAttribute("speciesVO", speciesVO);
      
      // 2단 메뉴
      ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
      model.addAttribute("menu", menu);
      
      model.addAttribute("word", word);
      // System.out.println("-> word null 체크: " + word);

      // ArrayList<SpeciesVO> list = this.speciesProc.list_search(word);  // 검색 목록
      ArrayList<SpeciesVO> list = this.speciesProc.list_search_paging(word, now_page, this.record_per_page); // 검색 목록 + 페이징
      model.addAttribute("list", list);
      
      int list_search_count = this.speciesProc.list_search_count(word); // 검색된 레코드 갯수
      model.addAttribute("search_cnt", list_search_count);
      
      // --------------------------------------------------------------------------------------
      // 페이지 번호 목록 생성
      // --------------------------------------------------------------------------------------
      int search_count = this.speciesProc.list_search_count(word);
      String paging = this.speciesProc.pagingBox(now_page, word, this.list_url, search_count, this.record_per_page, this.page_per_block);
      model.addAttribute("paging", paging);
      model.addAttribute("now_page", now_page);
      
      // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
      int no = search_count - ((now_page - 1) * this.record_per_page);
      model.addAttribute("no", no);
      // System.out.println("-> no: " + no);    
      // --------------------------------------------------------------------------------------  
             
      return "species/update"; // /templates/species/update.html
    } else {
      return "redirect:/users/login_cookie_need"; // redirect
    }
    

  }
  
  /**
   * 수정 처리
   * Model model: controller -> html로 데이터 전송 제공
   * @Valid: @NotEmpty, @Size, @NotNull, @Min, @Max, @Pattern... 규칙 위반 검사 지원
   * SpeciesVO speciesVO: FORM 태그의 값 자동 저장, Integer.parseInt(request.getParameter("seqno")) 자동 실행
   * BindingResult bindingResult: @Valid의 결과 저장
   * @param model
   * @return
   */
  @PostMapping(value="/update")
  public String update_process(Model model, 
                               @Valid SpeciesVO speciesVO, 
                               BindingResult bindingResult,
                               @RequestParam(name="word", defaultValue = "") String word,
                               @RequestParam(name="now_page", defaultValue="1") int now_page,
                               RedirectAttributes ra) {
    // System.out.println("-> update_process");
    
    if (bindingResult.hasErrors() == true) {
      return "species/update"; // /templates/species/update.html 
    }
    
    // System.out.println("-> speciesVO.getSname(): " + speciesVO.getSname());
    // System.out.println("-> speciesVO.getSeqno(): " + speciesVO.getSeqno());
    
    int cnt = this.speciesProc.update(speciesVO);
    // System.out.println("-> cnt: " + cnt);
    
    if (cnt == 1) {
//      model.addAttribute("code", Tool.UPDATE_SUCCESS);  
//      model.addAttribute("name", speciesVO.getSname());
      // System.out.println("-> redirect:/species/update/" + speciesVO.getSpeciesno() + "?word=" + word);
      // http://localhost:9091/species/update/33?word=영화
      // return "redirect:/species/update/" + speciesVO.getSpeciesno() + "?word=" + word; // @GetMapping(value="/update") // 한글 깨짐, X
      ra.addAttribute("word", word); // redirect로 데이터 전송, 한글 깨짐 방지
      return "redirect:/species/update/" + speciesVO.getSpeciesno();
    } else {
      model.addAttribute("code", Tool.UPDATE_FAIL); 
    }
    
    model.addAttribute("cnt", cnt);
    
    return "species/msg";  // /templates/species/msg.html
  }

  /**
   * 삭제폼
   * http://localhost:9091/species/delete/1
   * @param model
   * @return
   */
  @GetMapping(value="/delete/{speciesno}")
  public String delete(Model model, 
                              @PathVariable("speciesno") Integer speciesno,
                              @RequestParam(name="word", defaultValue = "") String word,
                              @RequestParam(name="now_page", defaultValue="1") int now_page) {
    // System.out.println("-> read speciesno: " + speciesno);
    
    SpeciesVO speciesVO = this.speciesProc.read(speciesno);
    model.addAttribute("speciesVO", speciesVO);
    
    // ArrayList<SpeciesVO> list = this.speciesProc.list_all();
    // ArrayList<SpeciesVO> list = this.speciesProc.list_search(word);
    ArrayList<SpeciesVO> list = this.speciesProc.list_search_paging(word, now_page, this.record_per_page);
    // System.out.println("-> delete form list.size(): " + list.size());
    model.addAttribute("list", list);
    
    int search_cnt = list.size();
    model.addAttribute("search_cnt", search_cnt);    
    
    // 2단 메뉴
    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
    model.addAttribute("menu", menu);
    
    model.addAttribute("word", word);
    
    // --------------------------------------------------------------------------------------
    // 페이지 번호 목록 생성
    // --------------------------------------------------------------------------------------
    int search_count = this.speciesProc.list_search_count(word);
    String paging = this.speciesProc.pagingBox(now_page, word, this.list_url, search_count, this.record_per_page, this.page_per_block);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);
    
    // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
    int no = search_count - ((now_page - 1) * this.record_per_page);
    model.addAttribute("no", no);
    // System.out.println("-> no: " + no);        
    // --------------------------------------------------------------------------------------    
    
    // 특정 speciesno에 해당하는 post 레코드 수          post 컨트롤러 만들고 주석 풀기
    int count_by_speciesno = this.postProc.count_by_speciesno(speciesno);
    model.addAttribute("count_by_speciesno", count_by_speciesno);
    
    // System.out.println("-> count_by_speciesno: " + count_by_speciesno);
    
    return "species/delete"; // /templates/species/delete.html
  }
  
  /**
   * 삭제 처리
   * @param model
   * @return
   */
  @PostMapping(value="/delete/{speciesno}")
  public String delete_process(Model model, 
                                         @PathVariable("speciesno") Integer speciesno,
                                         @RequestParam(name="word", defaultValue = "") String word,
                                         RedirectAttributes ra,
                                         @RequestParam(name="now_page", defaultValue="1") int now_page) {    
    SpeciesVO speciesVO = this.speciesProc.read(speciesno); // 삭제 정보 출력용으로 사전에 읽음
    model.addAttribute("speciesVO", speciesVO);
    
    int cnt = this.speciesProc.delete(speciesno); // 삭제 처리
    // System.out.println("-> cnt: " + cnt);
    
    if (cnt == 1) {
      // ----------------------------------------------------------------------------------------------------------
      // 마지막 페이지에서 모든 레코드가 삭제되면 페이지수를 1 감소 시켜야함.
      int search_cnt = this.speciesProc.list_search_count(word);
      if (search_cnt % this.record_per_page == 0) {
        now_page = now_page - 1;
        if (now_page < 1) {
          now_page = 1; // 최소 시작 페이지
        }
      }
      // ----------------------------------------------------------------------------------------------------------
      
      // model.addAttribute("code", Tool.DELETE_SUCCESS);
      ra.addAttribute("word", word);
      ra.addAttribute("now_page", now_page);      

      return "redirect:/species/list_search"; // @GetMapping(value="/list_search")
    } else {
      model.addAttribute("code", Tool.DELETE_FAIL); 
    }

    model.addAttribute("grp", speciesVO.getGrp());
    model.addAttribute("name", speciesVO.getSname());
    model.addAttribute("cnt", cnt);

    return "species/msg";  // /templates/species/msg.html
  }  

  /**
   * 특정 speciesno에 해당하는 post 삭제 후 species 삭제 처리
   * @param model
   * @return
   */
  @PostMapping(value="/delete_all_by_speciesno/{speciesno}")
  public String delete_all_by_speciesno(Model model, 
                                         @PathVariable("speciesno") Integer speciesno,
                                         @RequestParam(name="word", defaultValue = "") String word,
                                         RedirectAttributes ra,
                                         @RequestParam(name="now_page", defaultValue="1") int now_page) {    
    SpeciesVO speciesVO = this.speciesProc.read(speciesno); // 삭제 정보 출력용으로 사전에 읽음
    model.addAttribute("speciesVO", speciesVO);
    
    // int count_by_speciesno = this.postProc.count_by_speciesno(now_page);
    
    // 자식 테이블 삭제   post 컨트롤러 작업하면 주석 풀기
    int count_by_speciesno = this.postProc.delete_by_speciesno(speciesno);
    System.out.println("-> count_by_speciesno 삭제된 레코드 수: " + count_by_speciesno);
    
    // 카테고리 그룹에 등록된 글수 변경
    // .....
    
    int cnt = this.speciesProc.delete(speciesno); // 삭제 처리
    // System.out.println("-> cnt: " + cnt);
    
    if (cnt == 1) {
      // ----------------------------------------------------------------------------------------------------------
      // 마지막 페이지에서 모든 레코드가 삭제되면 페이지수를 1 감소 시켜야함.
      int search_cnt = this.speciesProc.list_search_count(word);
      if (search_cnt % this.record_per_page == 0) {
        now_page = now_page - 1;
        if (now_page < 1) {
          now_page = 1; // 최소 시작 페이지
        }
      }
      // ----------------------------------------------------------------------------------------------------------
      
      // model.addAttribute("code", Tool.DELETE_SUCCESS);
      ra.addAttribute("word", word);
      ra.addAttribute("now_page", now_page);      

      return "redirect:/species/list_search"; // @GetMapping(value="/list_search")
    } else {
      model.addAttribute("code", Tool.DELETE_FAIL); 
    }

    model.addAttribute("grp", speciesVO.getGrp());
    model.addAttribute("name", speciesVO.getSname());
    model.addAttribute("cnt", cnt);

    return "species/msg";  // /templates/species/msg.html
  }  
  
  /**
   * 우선 순위 높임, 10 등 -> 1 등
   * http://localhost:9091/species/update_seqno_forward/1
   */
  @GetMapping(value="/update_seqno_forward/{speciesno}")
  public String update_seqno_forward(Model model, @PathVariable("speciesno") Integer speciesno) {
    
    this.speciesProc.update_seqno_forward(speciesno);
    
    return "redirect:/species/list_all";  // @GetMapping(value="/list_all")
  }

  /**
   * 우선 순위 낮춤, 1 등 -> 10 등
   * http://localhost:9091/species/update_seqno_forward/1
   */
  @GetMapping(value="/update_seqno_backward/{speciesno}")
  public String update_seqno_backward(Model model, @PathVariable("speciesno") Integer speciesno) {
    
    this.speciesProc.update_seqno_backward(speciesno);
    
    return "redirect:/species/list_all";  // @GetMapping(value="/list_all")
  }
  
  /**
   * 카테고리 공개 설정
   * http://localhost:9091/species/update_visible_y/1
   */
  @GetMapping(value="/update_visible_y/{speciesno}")
  public String update_visible_y(Model model, @PathVariable("speciesno") Integer speciesno,
      @RequestParam(name="word", defaultValue = "") String word,
      @RequestParam(name="now_page", defaultValue="1") int now_page,
      RedirectAttributes ra) {
    
    // System.out.println("-> update_visible_y()");
    
    this.speciesProc.update_visible_y(speciesno);
    
    ra.addAttribute("word", word); // redirect로 데이터 전송, 한글 깨짐 방지
    ra.addAttribute("now_page", now_page); // redirect로 데이터 전송, 한글 깨짐 방지
    
    return "redirect:/species/list_search";  // @GetMapping(value="/list_search")
  }
  
  /**
   * 카테고리 비공개 설정
   * http://localhost:9091/species/update_visible_n/1
   */
  @GetMapping(value="/update_visible_n/{speciesno}")
  public String update_visible_n(Model model, @PathVariable("speciesno") Integer speciesno,
                                            @RequestParam(name="word", defaultValue = "") String word,
                                            @RequestParam(name="now_page", defaultValue="1") int now_page,
                                            RedirectAttributes ra) {
    
    // System.out.println("-> update_visible_n()");
    
    this.speciesProc.update_visible_n(speciesno);
    
    ra.addAttribute("word", word); // redirect로 데이터 전송, 한글 깨짐 방지
    ra.addAttribute("now_page", now_page); // redirect로 데이터 전송, 한글 깨짐 방지
    
    return "redirect:/species/list_search";  // @GetMapping(value="/list_search")
  }
  
}


