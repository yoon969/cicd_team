package dev.mvc.hospital;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class HospitalVO {

  /** 병원 번호 (PK) */
  private int hospitalno;

  /** 병원명 */
  private String name = "";

  /** 전화번호 */
  private String tel = "";

  /** 주소 */
  private String address = "";

  /** 홈페이지 URL */
  private String homepage = "";

  /** 등록일 */
  private String regdate = "";
}
