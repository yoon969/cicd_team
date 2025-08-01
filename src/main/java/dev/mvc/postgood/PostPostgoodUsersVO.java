package dev.mvc.postgood;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//-- 테이블 3개 join
//SELECT r.postgoodno, r.rdate, r.postno, c.title as c_title, r.usersno, m.id, m.mname
//FROM post c, postgood g, users u
//WHERE c.postno = r.postno AND r.usersno = m.usersno
//ORDER BY postgoodno DESC;

@Getter @Setter @ToString
public class PostPostgoodUsersVO {
  /** 컨텐츠 추천 번호 */
  private int postgoodno;
  
  /** 등록일 */
  private String rdate;
  
  /** 컨텐츠 번호 */
  private int postno;
  
  /** 제목 */
  private String p_title = "";
  
  /** 회원 번호 */
  private int usersno;
  
  /** 아이디(이메일) */
  private String email = "";
  
  /** 회원 성명 */
  private String usersname = "";
  
}



