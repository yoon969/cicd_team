
-- users 삭제전에 FK가 선언된 blog 테이블 먼저 삭제합니다.
DROP TABLE users;
-- 제약 조건과 함께 삭제(제약 조건이 있어도 삭제됨, 권장하지 않음.)
DROP TABLE users CASCADE CONSTRAINTS; 
 
CREATE TABLE users (
    usersno     NUMBER(10)     PRIMARY KEY,           -- 회원번호 (기본키)
    email       VARCHAR2(30)   NOT NULL,              -- 이메일
    passwd      VARCHAR2(200)  NOT NULL,              -- 비밀번호
    usersname    VARCHAR2(30)   NOT NULL,              -- 회원이름
    tel         VARCHAR2(14),                         -- 전화번호
    zipcode     VARCHAR2(5),                          -- 우편번호
    address1    VARCHAR2(80),                         -- 주소1
    address2    VARCHAR2(50),                         -- 주소2
    created_at  DATE DEFAULT SYSDATE,                 -- 가입일자
    role        VARCHAR2(10) DEFAULT 'user'           -- 등급 (ex: user, admin)
);

 
COMMENT ON TABLE users is '회원';
COMMENT ON COLUMN users.usersno is '회원 번호';
COMMENT ON COLUMN users.email is '아이디';
COMMENT ON COLUMN users.passwd is '패스워드';
COMMENT ON COLUMN users.usersname is '성명';
COMMENT ON COLUMN users.tel is '전화번호';
COMMENT ON COLUMN users.zipcode is '우편번호';
COMMENT ON COLUMN users.address1 is '주소1';
COMMENT ON COLUMN users.address2 is '주소2';
COMMENT ON COLUMN users.created_at is '가입일';
COMMENT ON COLUMN users.role is '등급';

DROP SEQUENCE users_seq;

CREATE SEQUENCE users_seq
  START WITH 1              -- 시작 번호
  INCREMENT BY 1          -- 증가값
  MAXVALUE 9999999999 -- 최대값: 9999999 --> NUMBER(7) 대응
  CACHE 2                       -- 2번은 메모리에서만 계산
  NOCYCLE;                     -- 다시 1부터 생성되는 것을 방지
 
 commit;
 
 
-- users 삭제할때 자식도 함께 삭제하도록 하는 제약조건

-- 외래 키 제약조건 DROP
ALTER TABLE recommendation DROP CONSTRAINT SYS_C0022276;
ALTER TABLE pet            DROP CONSTRAINT SYS_C0017346;
ALTER TABLE ai_consult     DROP CONSTRAINT SYS_C0017583;
ALTER TABLE feedback       DROP CONSTRAINT SYS_C0023569;
ALTER TABLE memory         DROP CONSTRAINT SYS_C0016149;
ALTER TABLE calendar       DROP CONSTRAINT SYS_C0017429;
ALTER TABLE post           DROP CONSTRAINT SYS_C0016131;
ALTER TABLE reply          DROP CONSTRAINT SYS_C0017558;
ALTER TABLE postgood       DROP CONSTRAINT SYS_C0016134;
ALTER TABLE reply_like     DROP CONSTRAINT FK_REPLY_LIKE_USERS;
ALTER TABLE alarm_log      DROP CONSTRAINT SYS_C0022287;

-- 외래 키 재생성
ALTER TABLE recommendation
ADD CONSTRAINT fk_recommendation_users
FOREIGN KEY (usersno)
REFERENCES users(usersno)
ON DELETE CASCADE;

ALTER TABLE pet
ADD CONSTRAINT fk_pet_users
FOREIGN KEY (usersno)
REFERENCES users(usersno)
ON DELETE CASCADE;

ALTER TABLE ai_consult
ADD CONSTRAINT fk_ai_consult_users
FOREIGN KEY (usersno)
REFERENCES users(usersno)
ON DELETE CASCADE;

ALTER TABLE feedback
ADD CONSTRAINT fk_feedback_users
FOREIGN KEY (usersno)
REFERENCES users(usersno)
ON DELETE CASCADE;

ALTER TABLE memory
ADD CONSTRAINT fk_memory_users
FOREIGN KEY (usersno)
REFERENCES users(usersno)
ON DELETE CASCADE;

ALTER TABLE calendar
ADD CONSTRAINT fk_calendar_users
FOREIGN KEY (usersno)
REFERENCES users(usersno)
ON DELETE CASCADE;

ALTER TABLE post
ADD CONSTRAINT fk_post_users
FOREIGN KEY (usersno)
REFERENCES users(usersno)
ON DELETE CASCADE;

ALTER TABLE reply
ADD CONSTRAINT fk_reply_users
FOREIGN KEY (usersno)
REFERENCES users(usersno)
ON DELETE CASCADE;

ALTER TABLE postgood
ADD CONSTRAINT fk_postgood_users
FOREIGN KEY (usersno)
REFERENCES users(usersno)
ON DELETE CASCADE;

ALTER TABLE reply_like
ADD CONSTRAINT fk_reply_like_users
FOREIGN KEY (usersno)
REFERENCES users(usersno)
ON DELETE CASCADE;

ALTER TABLE alarm_log
ADD CONSTRAINT fk_alarm_log_users
FOREIGN KEY (usersno)
REFERENCES users(usersno)
ON DELETE CASCADE;



-- 제약 조건 이름 검색

SELECT constraint_name
FROM user_constraints
WHERE table_name = 'POSTGOOD'
  AND constraint_type = 'R';
  
SELECT a.constraint_name, a.column_name
FROM user_cons_columns a
WHERE a.table_name = 'POSTGOOD'
  AND a.constraint_name IN ('SYS_C0016134', 'SYS_C0016135');

 
-- INSERT (회원 등록)
INSERT INTO users (
    usersno, email, passwd, usersname, tel, zipcode, address1, address2
) VALUES (
    users_seq.NEXTVAL, 'admin', '1234', '관리자', '010-1234-5678', '12345', '서울시 강남구', '101호'
);

INSERT INTO users (
    usersno, email, passwd, usersname, tel, zipcode, address1, address2
) VALUES (
    users_seq.NEXTVAL, 'dbsrn1224@example.com', '12234', '갱뮨겨', '010-1234-5678', '12345', '서울시 강남구', '101호'
);

-- SELECT (회원 전체 조회)
SELECT * FROM users;

-- SELECT (특정 회원 조회 - 예: 이메일 기준)
SELECT * FROM users
WHERE email = 'test01@example.com';

-- UPDATE (비밀번호, 전화번호 변경)
UPDATE users
SET passwd = 'abcd1234',
    tel = '010-9876-5432'
WHERE email = 'test01@example.com';

UPDATE users
SET role = 'admin'
WHERE usersno=4;

-- DELETE (회원 탈퇴)
DELETE FROM users
WHERE email = 'test01@example.com';

COMMIT;



