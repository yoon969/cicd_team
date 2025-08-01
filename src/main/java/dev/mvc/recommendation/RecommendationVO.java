package dev.mvc.recommendation;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class RecommendationVO {

  private Integer recommendationid;
  private Integer usersno;
  private String age;
  private String experience;
  private String personality;
  private String environment;
  private String condition;
  private String recommendation;
  private String description;
  private String realistic;
  private String caution;
  private String rdate;
}
