package dev.mvc.pet;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class PetVO {

  /** 반려동물 번호 (PK) */
  private int petno;

  /** 반려동물 이름 */
  private String name = "";

  /** 성별 (M: 수컷, F: 암컷) */
  private String gender = "";

  /** 생년월일 */
  private String birthday = "";

  /** 반려동물 설명 */
  private String description = "";

  /** 종 번호 (FK - species 테이블) */
  private Integer speciesno;

  /** 회원 번호 (FK - users 테이블) */
  private int usersno;

  /** 등록일 */
  private String regdate = "";
  
  // 파일 업로드 관련
  // -----------------------------------------------------------------------------------
  /**
  이미지 파일
  <input type='file' class="form-control" name='file1MF' id='file1MF' 
             value='' placeholder="파일 선택">
  */
  private MultipartFile file1MF = null;
  /** 메인 이미지 크기 단위, 파일 크기 */
  private String size1_label = "";
  /** 메인 이미지 */
  private String file1 = "";
  /** 실제 저장된 메인 이미지 */
  private String file1saved = "";
  /** 메인 이미지 preview */
  private String thumb1 = "";
  /** 메인 이미지 크기 */
  private long size1 = 0;
}
