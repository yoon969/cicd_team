package dev.mvc.ai_consult;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class AiConsultVO {

  /** 상담 번호 (PK) */
  private int consultno;

  /** 상담 요청자 회원 번호 */
  private int usersno;

  /** 사용자 질문 */
  private String question = "";

  /** GPT 응답 */
  private String answer = "";

  /** 증상 키워드 */
  private String symptom_tags = "";

  /** 응답 출처 (GPT, 기억, 추천 등) */
  private String source_type = "";
  
  /** 요약 내용 */
  private String summary = "";

  /** 감정 분석 결과 (예: 1~5 점수 등) */
  private int emotion;
  
  /** 생성일 */
  private String created_at = "";
  
  private Double similarity;


}
