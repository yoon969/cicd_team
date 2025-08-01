package dev.mvc.pet;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetJoinVO {
  private int petno;
  private String name;
  private String gender;
  private String birthday;
  private String description;
  private String regdate;

  private int usersno;
  private int speciesno;

  // species 테이블 정보
  private String sname; // 종 이름 (ex: 토끼)
  private String grp; // 중그룹 (ex: 포유류)
  
  //파일 업로드 관련
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
