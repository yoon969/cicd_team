DROP TABLE ai_consult;

CREATE TABLE ai_consult (
    consultno     NUMBER(10)     PRIMARY KEY,           -- 상담번호 (PK)
    question      VARCHAR2(500),                                 -- 사용자 질문
    answer        VARCHAR2(500),                                 -- GPT 응답
    symptom_tags  VARCHAR2(500),                        -- 증상 키워드
    source_type   VARCHAR2(20),                         -- 응답된 출처 (ex: GPT, 기억, 추천 등)
    created_at    DATE DEFAULT SYSDATE,                 -- 생성일
    summary       VARCHAR2(1000),                       -- 요약
    emotion       NUMBER(1),
    usersno       NUMBER(10),                           -- 회원번호 (FK)

    FOREIGN KEY (usersno) REFERENCES users(usersno)
);


COMMENT ON TABLE ai_consult IS 'GPT 기반 반려동물 증상 상담 기록 테이블';

COMMENT ON COLUMN ai_consult.consultno     IS '상담 번호 (PK)';
COMMENT ON COLUMN ai_consult.question      IS '사용자가 입력한 질문 또는 설명';
COMMENT ON COLUMN ai_consult.answer        IS 'GPT가 생성한 응답 내용';
COMMENT ON COLUMN ai_consult.symptom_tags  IS 'GPT가 추출한 주요 증상 키워드 목록';
COMMENT ON COLUMN ai_consult.source_type   IS '응답 출처 구분 (ex: GPT, 기억, 추천 등)';
COMMENT ON COLUMN ai_consult.created_at    IS '상담 생성 시각';
COMMENT ON COLUMN ai_consult.usersno       IS '상담 요청자 회원 번호 (users 테이블 참조)';
COMMENT ON COLUMN ai_consult.summary       IS '상담 내용 요약';
COMMENT ON COLUMN ai_consult.emotion       IS '상담 내용 감정 분석';

DROP SEQUENCE ai_consult_seq;

CREATE SEQUENCE ai_consult_seq
  START WITH 1
  INCREMENT BY 1
  MAXVALUE 9999999999
  CACHE 2
  NOCYCLE;

commit;

INSERT INTO ai_consult (
  consultno, question, answer, symptom_tags, source_type, created_at, usersno
) VALUES (
  ai_consult_seq.NEXTVAL,
  '강아지가 밥을 안 먹어요. 왜 그럴까요?',
  '식욕 저하는 스트레스, 질병, 환경 변화로 인해 발생할 수 있습니다.',
  '식욕 저하, 스트레스, 환경 변화',
  'GPT',
  SYSDATE,
  1001
);
commit;

-- 전체 상담 기록 조회
SELECT * FROM ai_consult;

-- 특정 회원의 상담 기록 조회
SELECT * 
FROM ai_consult
WHERE usersno = 1001;

-- 최근 상담 내역 5건
SELECT *
FROM ai_consult
ORDER BY created_at DESC
FETCH FIRST 5 ROWS ONLY;

UPDATE ai_consult
SET 
  answer = '해당 증상은 위장 문제 또는 스트레스에 기인할 수 있습니다.',
  symptom_tags = '식욕 저하, 위장 문제, 스트레스',
  updated_at = SYSDATE
WHERE consultno = 101;

-- 특정 상담 삭제
DELETE FROM ai_consult
WHERE consultno = 101;

-- 특정 회원의 전체 상담 삭제
DELETE FROM ai_consult
WHERE usersno = 1001;

ALTER TABLE ai_consult MODIFY answer VARCHAR2(2000);
