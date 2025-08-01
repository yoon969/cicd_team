/**********************************/
/* Table cname: 카테고리 */
/**********************************/
-- 장르를 카테고리 그룹 테이블로 처리하지 않고 컬럼으로 처리함 ★
DROP TABLE cate;
DROP TABLE species CASCADE CONSTRAINTS; 

CREATE TABLE species (
    speciesno   NUMBER(10)     PRIMARY KEY,           -- 품번(기본키)
    grp         VARCHAR2(20),                         -- 중그룹(파충류, 포유류 등)
    sname       VARCHAR2(30),                         -- 종이름(도마뱀, 토끼 등)
    CNT         NUMBER(7),                            -- 게시글 수 등 용도
    SEQNO       NUMBER(5),                            -- 출력 순서
    VISIBLE     CHAR(1),                              -- 출력여부 ('Y'/'N')
    sdate       DATE DEFAULT SYSDATE                  -- 등록일
);

COMMENT ON TABLE species is '카테고리';
COMMENT ON COLUMN species.speciesno is '품번(기본키) 번호';
COMMENT ON COLUMN species.grp is '중그룹(파충류, 포유류 등)';
COMMENT ON COLUMN species.sname is '종이름(도마뱀, 토끼 등)';
COMMENT ON COLUMN species.CNT is '관련 자료수';
COMMENT ON COLUMN species.SEQNO is '출력 순서';
COMMENT ON COLUMN species.VISIBLE is '출력여부';
COMMENT ON COLUMN species.sdate is '등록일';

DROP SEQUENCE species_SEQ;

CREATE SEQUENCE species_SEQ
START WITH 1         -- 시작 번호
INCREMENT BY 1       -- 증가값
MAXVALUE 9999999999  -- 최대값: 9999999999 --> NUMBER(10) 대응
CACHE 2              -- 2번은 메모리에서만 계산
NOCYCLE;             -- 다시 1부터 생성되는 것을 방지

commit;

INSERT INTO species (
  speciesno, grp, sname, CNT, SEQNO, VISIBLE, sdate
) VALUES (
  species_SEQ.NEXTVAL,
  '파충류',
  '레오파드게코',
  0,
  1,
  'Y',
  SYSDATE
);

-- 전체 조회 (등록일 내림차순)
SELECT * 
FROM species
ORDER BY sdate DESC;

-- 출력 여부가 'Y'인 것만 조회 (홈페이지에 노출용)
SELECT * 
FROM species
WHERE visible = 'Y'
ORDER BY seqno;

-- 출력 순서와 출력 여부 수정
UPDATE species
SET 
  SEQNO = 5,
  VISIBLE = 'N'
WHERE speciesno = 101;

-- 게시글 수 증가 (예: 포스트 등록 시)
UPDATE species
SET 
  CNT = CNT + 1
WHERE speciesno = 38;

-- 특정 품종 삭제
DELETE FROM species
WHERE speciesno = 101;

INSERT INTO species (speciesno, grp, sname, visible, rdate)
VALUES (0, '기타', '기타', 'Y', SYSDATE);

SELECT * FROM species WHERE speciesno = 0;



