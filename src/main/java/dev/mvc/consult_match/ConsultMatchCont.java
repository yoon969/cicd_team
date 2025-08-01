package dev.mvc.consult_match;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 유사 상담 컨트롤러
 */
@Controller
@RequestMapping("/consult_match")
public class ConsultMatchCont {

  @Autowired
  @Qualifier("dev.mvc.consult_match.ConsultMatchProc")
  private ConsultMatchProcInter consultMatchProc;

  /**
   * 특정 상담(consultno) 기준 유사 상담 목록 조회회
   * @param consultno 기준 상담번호
   */
  @GetMapping("/list_by_consultno")
  public String listByConsultno(@RequestParam("consultno") int consultno, Model model) {
    List<ConsultMatchVO> list = consultMatchProc.listByConsultno(consultno);
    model.addAttribute("list", list);
    model.addAttribute("consultno", consultno);
    return "/consult_match/list";  // → /templates/consult_match/list.html
  }

  /**
   * 특정 상담 기준 전체 매핑 삭제
   */
  @PostMapping("/delete_by_consultno")
  public String deleteByConsultno(@RequestParam("consultno") int consultno) {
    consultMatchProc.deleteByConsultno(consultno);
    return "redirect:/consult_match/list_by_consultno?consultno=" + consultno;
  }

  /**
   * REST API: 유사 매핑 리스트 JSON 반환
   */
  @GetMapping("/api/list/{consultno}")
  @ResponseBody
  public List<ConsultMatchVO> apiListByConsultno(@PathVariable int consultno) {
    return consultMatchProc.listByConsultno(consultno);
  }
}
