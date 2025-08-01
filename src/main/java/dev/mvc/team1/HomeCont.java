package dev.mvc.team1;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import dev.mvc.animal_today.AnimalTodayProcInter;
import dev.mvc.animal_today.AnimalTodayVO;
import dev.mvc.post.PostProcInter;
import dev.mvc.species.SpeciesProcInter;
import dev.mvc.species.SpeciesVOMenu;
import dev.mvc.tool.FastAPIClient;
import dev.mvc.tool.Security;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeCont {

  @Autowired
  @Qualifier("dev.mvc.animal_today.AnimalTodayProc")
  private AnimalTodayProcInter animalTodayProc;

  @Autowired
  @Qualifier("dev.mvc.species.SpeciesProc")
  private SpeciesProcInter speciesProc;

  @Autowired
  private Security security;

  public HomeCont() {
    System.out.println("-> HomeCont created.");
  }

//  @GetMapping(value="/")
//  public String home(Model model, HttpSession session) {
//    if (this.security != null) {
//      System.out.println("-> 객체 고유 코드: " + security.hashCode());
//      System.out.println(security.aesEncode("1234"));
//    }
//
//    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
//    model.addAttribute("menu", menu);
//
//    // ✅ 세션 정보 모델에 전달
//    model.addAttribute("usersno", session.getAttribute("usersno"));
//    model.addAttribute("email", session.getAttribute("email"));
//    model.addAttribute("role", session.getAttribute("role"));
//
//    return "forward:/index.html"; // 리액트 index.html을 렌더링
//  }

  @GetMapping(value = "/")
  public String home(Model model, HttpSession session) {
    if (this.security != null) {
      System.out.println("-> 객체 고유 코드: " + security.hashCode());
      System.out.println(security.aesEncode("1234"));
    }

    ArrayList<SpeciesVOMenu> menu = this.speciesProc.menu();
    model.addAttribute("menu", menu);

    // ✅ 오늘의 숨숨이 친구 정보 추가
    String name = FastAPIClient.pickRandomAnimalName();

    if (name == null || name.trim().isEmpty()) {
      System.out.println("❗ FastAPI에서 이름을 받아오지 못했습니다.");
      model.addAttribute("animal", null);
      return "index";
    }

    AnimalTodayVO vo = animalTodayProc.findByName(name);

    if (vo == null) {
      String description = name + "은(는) 특수반려동물 중 하나입니다. 특징 설명이 필요합니다.";
      
      // ✅ 요약, 추천정보 받아오기
      Map<String, String> result = FastAPIClient.getAnimalSummary(name, description);
      String summary = result.getOrDefault("summary", "");
      String recommendation = result.getOrDefault("recommendation", "");

      vo = new AnimalTodayVO();
      vo.setName(name);
      vo.setDescription(description);
      vo.setSummary(summary);
      vo.setRecommendation(recommendation);
      animalTodayProc.insert(vo);
    }

    model.addAttribute("animal", vo);

    return "index";
  }

}
