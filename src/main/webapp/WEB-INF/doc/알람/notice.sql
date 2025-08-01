-- ✅ 기존 테이블 및 시퀀스 제거 (주의)
DROP TABLE animal_today CASCADE CONSTRAINTS;
DROP SEQUENCE animal_today_seq;

-- ✅ 테이블 생성
CREATE TABLE animal_today (
  animalno       NUMBER PRIMARY KEY,                         -- 동물 고유번호 (PK)
  name           VARCHAR2(100) NOT NULL,                     -- 동물 이름
  image_url      VARCHAR2(255),                              -- 이미지 경로
  description    VARCHAR2(1000),                             -- 동물 설명
  summary        VARCHAR2(1000),                             -- GPT 요약
  recommendation VARCHAR2(1000),                             -- GPT 추천 포인트
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP         -- 등록일시
);

-- ✅ 컬럼 주석
COMMENT ON TABLE animal_today IS '오늘의 특수동물 소개 테이블';
COMMENT ON COLUMN animal_today.animalno IS '동물 번호 (PK)';
COMMENT ON COLUMN animal_today.name IS '동물 이름';
COMMENT ON COLUMN animal_today.image_url IS '이미지 URL';
COMMENT ON COLUMN animal_today.description IS '동물 설명';
COMMENT ON COLUMN animal_today.summary IS 'GPT가 생성한 요약 문장';
COMMENT ON COLUMN animal_today.recommendation IS 'GPT가 생성한 추천 포인트';
COMMENT ON COLUMN animal_today.created_at IS '등록일시';

-- ✅ 시퀀스 생성
CREATE SEQUENCE animal_today_seq
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;

COMMIT;

INSERT INTO animal_today (
  animalno, name, image_url, description, summary, recommendation
) VALUES (
  animal_today_seq.NEXTVAL,
  '슈가글라이더',
  '/images/pets/sugar_glider.jpg',
  '작고 활발한 야행성 동물로, 날개막을 펼쳐 짧은 거리를 활강할 수 있어요. 사회성이 높아 함께 놀아주는 것이 중요해요.',
  '활동적인 야행성 동물로 사람과 잘 어울림.',
  '자주 놀아줄 시간이 있는 분께 추천해요!'
);

INSERT INTO animal_today (
  animalno, name, image_url, description, summary, recommendation
) VALUES (
  animal_today_seq.NEXTVAL,
  '레오파드게코',
  '/images/pets/leopard_gecko.jpg',
  '조용하고 낮은 온도에서 잘 지내는 도마뱀으로 초보자에게 적합합니다.',
  '조용하고 관리가 쉬워 입문자에게 적합한 파충류입니다.',
  '파충류를 처음 키워보는 분께 추천합니다.'
);

COMMIT;

-- 전체 목록 조회
SELECT * FROM animal_today ORDER BY animalno;

-- 랜덤 동물 1마리 조회
SELECT *
FROM (
  SELECT * FROM animal_today ORDER BY DBMS_RANDOM.VALUE
)
WHERE ROWNUM = 1;

-- 오늘 날짜 기준 순환 조회
SELECT *
FROM animal_today
WHERE MOD(TO_NUMBER(TO_CHAR(SYSDATE, 'DDD')), (SELECT COUNT(*) FROM animal_today)) + 1 = animalno;

DESC notice;