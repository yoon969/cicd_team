package dev.mvc.animal_today;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import dev.mvc.pet.PetProcInter;
import dev.mvc.tool.FastAPIClient;

@Controller
@RequestMapping("/animal")
public class AnimalTodayCont {

  @Autowired
  @Qualifier("dev.mvc.animal_today.AnimalTodayProc")
  private AnimalTodayProcInter animalTodayProc;

  @GetMapping("/today")
  @ResponseBody
  public AnimalTodayVO todayAnimal(Model model) {
    // ✅ 1. AI가 랜덤 종 이름 선택
    String name = FastAPIClient.pickRandomAnimalName();
    if (name == null) {
      System.out.println("AI로부터 이름을 받아오지 못했습니다.");
      return null;
    }

    // ✅ 2. DB에서 이름으로 검색
    AnimalTodayVO vo = animalTodayProc.findByName(name);
    if (vo != null) {
      return vo;  // 이미 DB에 있으면 바로 반환
    }

    // ✅ 3. GPT 설명 생성
    String description = name + "은(는) 특수반려동물 중 하나입니다. 특징 설명이 필요합니다.";
    Map<String, String> result = FastAPIClient.getAnimalSummary(name, description);
    String summary = result.get("summary");
    String recommendation = result.get("recommendation");
    
    // ✅ 4. DB 저장
    vo = new AnimalTodayVO();
    vo.setName(name);
    vo.setDescription(description);
    vo.setSummary(summary);
    vo.setRecommendation(recommendation);
    animalTodayProc.insert(vo);

    // ✅ 5. 반환
    return vo;
  }
}
