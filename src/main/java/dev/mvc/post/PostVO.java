package dev.mvc.post;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class PostVO {
  
  private MultipartFile file1MF; 

  /** 게시글 번호 (PK) */
  private int postno;

  /** 그룹명 (상위 분류, 예: 설치류, 조류 등) */
  private String grp;

  /** 제목 */
  private String title = "";

  /** 내용 */
  private String content = "";

  /** 추천 수 */
  private int recom = 0;

  /** 조회수 */
  private int cnt = 0;

  /** 댓글 수 */
  private int replycnt = 0;

  /** 게시글 비밀번호 (수정/삭제용) */
  private String passwd = "";

  /** 검색 키워드 */
  private String word = "";

  /** 등록일 */
  private Date pdate;

  /** 업로드 파일명 (원본) */
  private String file1 = "";

  /** 저장된 파일명 (서버 저장용) */
  private String file1saved = "";

  /** 썸네일 경로 */
  private String thumb1 = "";

  /** 파일 크기 */
  private long size1 = 0;

  /** 지도 정보 (위치 텍스트 등) */
  private String map = "";

  /** YouTube 영상 링크 */
  private String youtube = "";

  /** 게시글 요약 */
  private String summary = "";

  /** 회원 번호 (FK - users 테이블) */
  private Integer usersno;

  /** 종 번호 (FK - species 테이블) */
  private Integer speciesno;

  /** 감정 정보 (예: 행복, 슬픔, 공감 등) */
  private String emotion = "";
  
  /** 작성자 닉네임 (Users 테이블에서 조인해서 가져옴) */
  private String nickname;
}
