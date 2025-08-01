package dev.mvc.animal_today;

import lombok.Data;

@Data
public class AnimalTodayVO {
  private int animalno;          // PK
  private String name;           // 동물 이름
  private String description;    // 원문 설명
  private String summary;        // AI가 요약한 설명
  private String created_at;     // 등록일
  private String recommendation;
}
