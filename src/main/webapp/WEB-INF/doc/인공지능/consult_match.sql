DROP TABLE consult_match

CREATE TABLE consult_match (
    matchno        NUMBER(10)      PRIMARY KEY,            -- 유사질문번호 (PK)
    matched_score  NUMBER(5,2),                             -- 유사도 점수 (0~1)
    created_at     DATE DEFAULT SYSDATE,                   -- 생성일자
    sourceno       NUMBER(10),                             -- 기준 상담 번호 (FK)
    targetno       NUMBER(10),                             -- 유사한 상담 번호 (FK)

    FOREIGN KEY (sourceno) REFERENCES ai_consult(consultno),
    FOREIGN KEY (targetno) REFERENCES ai_consult(consultno)
);

COMMENT ON TABLE consult_match IS 'AI 상담 기록 간 유사도를 기반으로 연결된 유사 상담 매핑 테이블';

COMMENT ON COLUMN consult_match.matchno        IS '유사 상담 매핑 고유 번호 (PK)';
COMMENT ON COLUMN consult_match.matched_score  IS '상담 간 유사도 점수 (0.00 ~ 1.00)';
COMMENT ON COLUMN consult_match.created_at     IS '유사도 분석 및 매핑 생성일자';
COMMENT ON COLUMN consult_match.sourceno       IS '기준이 되는 상담 번호 (ai_consult 테이블 참조)';
COMMENT ON COLUMN consult_match.targetno       IS '유사하다고 판단된 상담 번호 (ai_consult 테이블 참조)';

CREATE SEQUENCE consult_match_seq
  START WITH 1
  INCREMENT BY 1
  MAXVALUE 9999999999
  CACHE 2
  NOCYCLE;

commit;

INSERT INTO consult_match (
  matchno, matched_score, sourceno, targetno
) VALUES (
  consult_match_seq.NEXTVAL,
  0.87,
  101,
  109
);

-- 전체 유사 상담 매핑 조회
SELECT * FROM consult_match;

-- 특정 기준 상담번호(sourceno)에 대한 유사 상담 목록
SELECT * 
FROM consult_match 
WHERE sourceno = 101;

-- 유사도 점수 높은 순 조회
SELECT * 
FROM consult_match 
ORDER BY matched_score DESC;

UPDATE consult_match
SET matched_score = 0.92
WHERE matchno = 5;

-- 특정 매핑 삭제
DELETE FROM consult_match
WHERE matchno = 5;

-- 특정 기준 상담에 대한 유사 매핑 전체 삭제
DELETE FROM consult_match
WHERE sourceno = 101;
