package dev.mvc.memory;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class MemoryVO {

  /** 추억 변환 결과 번호 (PK) */
  private int memoryno;

  /** 업로드된 원본 반려동물 이미지 URL */
  private String image_url = "";

  /** 생성된 일러스트 이미지 URL */
  private String generated_image_url = "";

  /** 생성일자 */
  private String create_at = "";

  /** 회원 번호 (FK) */
  private int usersno;
}
