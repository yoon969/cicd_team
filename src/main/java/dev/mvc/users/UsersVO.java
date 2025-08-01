package dev.mvc.users;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class UsersVO {

  /** 사용자 번호 (PK) */
  private int usersno;

  /** 이메일 */
  private String email;

  /** 비밀번호 */
  private String passwd;

  /** 사용자 이름 */
  private String usersname;
  
  /** 사용자가 속해 있는 대분류 */
  private String grp;

  /** 전화번호 */
  private String tel ="" ;

  /** 우편번호 */
  private String zipcode  ="";

  /** 기본 주소 */
  private String address1 ="";

  /** 상세 주소 */
  private String address2 ="";

  /** 가입일 */
  private String created_at = "";

  /** 권한 (예: USER, ADMIN 등) */
  private String role;
}
