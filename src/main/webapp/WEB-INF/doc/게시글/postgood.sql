DROP TABLE postgood;

CREATE TABLE postgood (
    postgoodno   NUMBER(10)     PRIMARY KEY,            -- 추천번호 (PK)
    rdate        DATE DEFAULT SYSDATE,                  -- 추천일
    usersno      NUMBER(10),                            -- 회원번호 (FK)
    postno       NUMBER(10),                            -- 글번호 (FK)

    FOREIGN KEY (usersno) REFERENCES users(usersno),
    FOREIGN KEY (postno) REFERENCES post(postno)
);

DROP SEQUENCE postgood_seq;

CREATE SEQUENCE postgood_seq
START WITH 1         -- 시작 번호
INCREMENT BY 1       -- 증가값
MAXVALUE 9999999999  -- 최대값: 9999999999 --> NUMBER(10) 대응
CACHE 2              -- 2번은 메모리에서만 계산
NOCYCLE;             -- 다시 1부터 생성되는 것을 방지

commit;

-- 데이터 삽입
INSERT INTO postgood(postgoodno, rdate, postno, usersno)
VALUES (postgood_seq.nextval, sysdate, 43, 1);

INSERT INTO postgood(postgoodno, rdate, postno, usersno)
VALUES (postgood_seq.nextval, sysdate, 43., 1);

INSERT INTO postgood(postgoodno, rdate, postno, usersno)
VALUES (postgood_seq.nextval, sysdate, 43, 1);

INSERT INTO postgood(postgoodno, rdate, postno, usersno)
VALUES (postgood_seq.nextval, sysdate, 43, 1);

COMMIT;

-- 전체 목록
SELECT postgoodno, rdate, postno, usersno
FROM postgood
ORDER BY postgoodno DESC;

postgoodno RDATE               postno   usersNO
-------------- ------------------- ---------- ----------
             5 2025-01-07 10:51:32          3          5
             3 2025-01-07 10:50:51          1          4
             2 2025-01-07 10:50:34          1          3
             1 2025-01-07 10:50:17          1          1

-- PK 조회
SELECT postgoodno, rdate, postno, usersno
FROM postgood
WHERE postgoodno = 5;

-- postno, usersno로 조회
SELECT postgoodno, rdate, postno, usersno
FROM postgood
WHERE postno=43 AND usersno=1;

-- 삭제
DELETE FROM postgood
WHERE postgoodno = 5;

commit;

-- 특정 컨텐츠의 특정 회원 추천 갯수 산출
SELECT COUNT(*) as cnt
FROM postgood
WHERE postno=1 AND usersno=1;

       CNT
----------
         1 <-- 이미 추천을 함
         
SELECT COUNT(*) as cnt
FROM postgood
WHERE postno=5 AND usersno=1;

       CNT
----------
         0 <-- 추천 안됨
         
-- JOIN, 어느 배우를 누가 추천 했는가?
SELECT postgoodno, rdate, postno, usersno
FROM postgood
ORDER BY postgoodno DESC;

-- 테이블 2개 join
SELECT r.postgoodno, r.rdate, r.postno, c.title, r.usersno
FROM post c, postgood r
WHERE c.postno = r.postno
ORDER BY postgoodno DESC;

-- 테이블 3개 join, as 사용시 컴럼명 변경 가능: c.title as c_title
SELECT r.postgoodno, r.rdate, r.postno, c.title as c_title, r.usersno, m.id, m.mname
FROM post c, postgood r, users m
WHERE c.postno = r.postno AND r.usersno = m.usersno
ORDER BY postgoodno DESC;

   
 
 