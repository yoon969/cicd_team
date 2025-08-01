package dev.mvc.species;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class SpeciesVO {

  /** 종 번호 (PK) */
  private int speciesno;

  /** 중그룹 (예: 파충류, 포유류 등) */
  private String grp = "";

  /** 종 이름 (예: 도마뱀, 토끼 등) */
  private String sname = "";

  /** 관련 게시글 수 */
  private int cnt = 0;

  /** 출력 순서 */
  private int seqno = 1;

  /** 출력 여부 ('Y' 또는 'N') */
  private String visible = "Y";

  /** 등록일 */
  private String sdate = "";
  
  /** 이모지 (UI용, DB 저장 X) */
  private String emoji = "";

}
