package dev.mvc.feedback;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class FeedbackVO {
  
  /** 피드백 번호 */
  private Integer feedbackno;
  
  /** 유저 번호 */
  private Integer usersno;
  
  /** 피드백 내용 */
  private String content;
  
  /** 작성일자 */ 
  private String created_at;
  
  /** 표시여부 */
  private String visible;

}
