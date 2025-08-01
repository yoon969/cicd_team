CREATE TABLE adopt_recommendation (
  id            NUMBER PRIMARY KEY,
  usersno       NUMBER NOT NULL,               -- 사용자 번호
  age           VARCHAR2(20),
  experience    VARCHAR2(20),
  personality   VARCHAR2(20),
  environment   VARCHAR2(50),
  condition     VARCHAR2(500),
  recommendation VARCHAR2(100),                -- 추천 동물 이름
  description   VARCHAR2(1000),                -- 추천 동물 설명
  realistic     VARCHAR2(100),                 -- 현실성 여부
  caution       VARCHAR2(1000),                -- 주의사항
  rdate         DATE DEFAULT SYSDATE,          -- 작성일
  FOREIGN KEY (usersno) REFERENCES users(usersno)
);