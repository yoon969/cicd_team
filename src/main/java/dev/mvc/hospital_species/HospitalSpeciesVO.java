package dev.mvc.hospital_species;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class HospitalSpeciesVO {

  /** 병원-종 연결 고유 ID (PK) */
  private int id;

  /** 병원 번호 (FK) */
  private int hospitalno;

  /** 종 번호 (FK) */
  private int speciesno;

  /** 종 이름 (JOIN: species.sname) */
  private String sname = "";

  /** 종 그룹 (JOIN: species.grp) */
  private String grp = "";
}
