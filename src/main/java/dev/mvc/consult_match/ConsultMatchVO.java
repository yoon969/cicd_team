package dev.mvc.consult_match;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
//CREATE TABLE consult_match (
//    matchno            NUMBER(10)        PRIMARY KEY,                -- 고유 매핑 번호
//    consultno          NUMBER(10)        NOT NULL,                   -- 기준(신규) 상담번호 (FK)
//    matched_consultno  NUMBER(10)        NOT NULL,                   -- 매칭된(기존) 상담번호 (FK)
//    match_score        NUMBER(5,4)       NOT NULL,                   -- 유사도 점수 (0.0000 ~ 1.0000)
//    created_at         DATE              DEFAULT SYSDATE            -- 매핑 생성 시각
//);
@Getter @Setter @ToString
public class ConsultMatchVO {

  private int matchno;
  private int consultno;           // 기준 상담번호
  private int matchedConsultno;    // 매칭된 상담번호
  private double matchScore;
  private Date created_at;
}
