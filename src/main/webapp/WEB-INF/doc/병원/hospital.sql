DROP TABLE hospital CASCADE CONSTRAINTS; 

CREATE TABLE hospital (
    hospitalno   NUMBER(10)     PRIMARY KEY,            -- 병원 번호 (PK)
    name         VARCHAR2(100),                         -- 병원명
    tel          VARCHAR2(20),                          -- 전화번호
    address      VARCHAR2(200),                         -- 주소
    homepage     VARCHAR2(255),                         -- 홈페이지 URL
    regdate      DATE DEFAULT SYSDATE                   -- 등록일
);

COMMENT ON TABLE hospital IS '동물병원 정보';

COMMENT ON COLUMN hospital.hospitalno IS '병원 번호 (PK)';
COMMENT ON COLUMN hospital.name        IS '병원명';
COMMENT ON COLUMN hospital.tel         IS '전화번호';
COMMENT ON COLUMN hospital.address     IS '주소';
COMMENT ON COLUMN hospital.homepage    IS '홈페이지 URL';
COMMENT ON COLUMN hospital.regdate     IS '등록일';


DROP SEQUENCE hospital_seq;

CREATE SEQUENCE hospital_seq
  START WITH 1              -- 시작 번호
  INCREMENT BY 1          -- 증가값
  MAXVALUE 9999999999 -- 최대값: 9999999 --> NUMBER(7) 대응
  CACHE 2                       -- 2번은 메모리에서만 계산
  NOCYCLE;                     -- 다시 1부터 생성되는 것을 방지
 
 commit;

commit;

-- 병원 1
INSERT INTO hospital (
    hospitalno, name, tel, address, zipcode, homepage, openhour, memo, regdate
) VALUES (
    1, '서울동물메디컬센터', '02-1234-5678', '서울특별시 강남구 도산대로 101', '06000',
    'http://www.seoulvet.co.kr', '09:00~18:00 (주말 휴무)', '파충류 진료 가능', SYSDATE
);

-- 병원 2
INSERT INTO hospital (
    hospitalno, name, tel, address, zipcode, homepage, openhour, memo, regdate
) VALUES (
    2, '부산이구아나동물병원', '051-987-6543', '부산광역시 해운대구 해운대로 321', '48000',
    'http://www.busanpetcare.com', '10:00~20:00', '이구아나 특화 진료 병원', SYSDATE
);

-- 병원 3
INSERT INTO hospital (
    hospitalno, name, tel, address, zipcode, homepage, openhour, memo, regdate
) VALUES (
    3, '제주특수반려동물병원', '064-555-0001', '제주특별자치도 제주시 중앙로 88', '63000',
    'http://www.jejuspecialpet.com', '09:30~17:30', '소형 포유류 진료 가능', SYSDATE
);

COMMIT;

-- 전체 병원 목록 조회
SELECT * FROM hospital;

-- 병원명 + 운영시간 + 메모만 보기
SELECT name, openhour, memo FROM hospital;

-- 서울 지역 병원만 보기
SELECT * 
FROM hospital 
WHERE address LIKE '서울%';

-- 홈페이지 없는 병원만 보기
SELECT name, address 
FROM hospital 
WHERE homepage IS NULL;

-- 최신 등록 순으로 병원 목록 보기
SELECT * 
FROM hospital 
ORDER BY regdate DESC;

-- 병원 이름에 이구아나 포함된 곳
SELECT * 
FROM hospital 
WHERE name LIKE '%이구아나%';

UPDATE hospital
SET name      = '숨숨이동물병원',
    tel       = '02-1234-5678',
    address   = '서울특별시 강남구 테헤란로 123',
    zipcode   = '06236',
    homepage  = 'https://sumsumvet.co.kr',
    openhour  = '월~금 10:00~18:00 / 토 10:00~14:00',
    memo      = '특수동물 전문 진료 가능'
WHERE hospitalno = 1;

DELETE FROM hospital
WHERE hospitalno = 1;
