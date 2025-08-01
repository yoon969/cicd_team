package dev.mvc.species;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

//   - 메뉴의 구성
//     파충류         설치류         어류             <- 카테고리 그룹(대분류)
//     └ 도마뱀    └ 친칠라   └ 금붕어       <- 카테고리(중분류)
//     └ 뱀          └ 카피바라└ 폐어
//     └ 아나콘다  └ 토끼      └ 실러캔스
@Getter @Setter
public class SpeciesVOMenu {
  /** 카테고리 그룹(대분류) */
  private String grp;
  
  /** 카테고리(중분류) */
  private ArrayList<SpeciesVO> list_name;
  
}
