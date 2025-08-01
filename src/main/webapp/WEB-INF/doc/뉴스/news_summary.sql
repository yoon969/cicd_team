CREATE TABLE news_summary (
  newsno NUMBER PRIMARY KEY,
  title VARCHAR2(500),
  link VARCHAR2(1000),
  source VARCHAR2(100),
  summary VARCHAR2(1000),
  summary_long VARCHAR2(4000),
  category VARCHAR2(100),
  hash VARCHAR2(100),
  visible CHAR(1) DEFAULT 'Y',
  created_at DATE DEFAULT SYSDATE
);

-- 시퀀스 생성
CREATE SEQUENCE news_summary_seq START WITH 1 INCREMENT BY 1;

-- 테스트 데이터 삽입
INSERT INTO news_summary (
  newsno, title, link, source, summary, summary_long, category, hash, visible
) VALUES (
  news_summary_seq.NEXTVAL, 
  '특수동물에 대한 관심 증가', 
  'https://example.com/news1', 
  '네이버뉴스', 
  '짧은 요약', 
  '긴 요약 내용입니다.', 
  '특수동물', 
  'abc123', 
  'Y'
);

COMMIT;
