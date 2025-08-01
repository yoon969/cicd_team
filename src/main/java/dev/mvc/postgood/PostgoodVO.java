package dev.mvc.postgood;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class PostgoodVO {

  /** 좋아요 고유 번호 (PK) */
  private int postgoodno;

  /** 등록일자 (좋아요 누른 날짜) */
  private String rdate = "";

  /** 게시글 번호 (FK) */
  private int postno;

  /** 회원 번호 (FK) */
  private int usersno;

  // ✅ postno, usersno만 받는 생성자 추가
  public PostgoodVO(int postno, int usersno) {
    this.postno = postno;
    this.usersno = usersno;
  }

  // 기본 생성자는 lombok이 생성해줌
}
