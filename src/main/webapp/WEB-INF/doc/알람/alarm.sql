DROP TABLE alarm;

CREATE TABLE alarm (
  usersno NUMBER PRIMARY KEY,
  calendar CHAR(1) DEFAULT 'Y',  -- 케어 캘린더 알림
  email CHAR(1) DEFAULT 'Y',     -- 이메일 알림
  sms CHAR(1) DEFAULT 'Y',       -- 문자 알림
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE alarm IS '알림 설정 테이블';

COMMENT ON COLUMN alarm.usersno IS '회원 번호 (PK)';
COMMENT ON COLUMN alarm.calendar IS '캘린더 알림 여부 (Y/N)';
COMMENT ON COLUMN alarm.email IS '이메일 알림 여부 (Y/N)';
COMMENT ON COLUMN alarm.sms IS '문자 알림 여부 (Y/N)';
COMMENT ON COLUMN alarm.updated_at IS '최종 수정일시';

CREATE SEQUENCE alarm_seq
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;

commit;

-- 시퀀스 사용하는 경우
INSERT INTO alarm (usersno, calendar, email, sms)
VALUES (alarm_seq.NEXTVAL, 'Y', 'N', 'Y');

-- 또는 usersno가 이미 존재하는 외래키라면:
INSERT INTO alarm (usersno, calendar, email, sms)
VALUES (101, 'Y', 'Y', 'N');

-- 전체 조회
SELECT * FROM alarm;

-- 특정 회원의 알림 설정 조회
SELECT * FROM alarm WHERE usersno = 101;

-- 특정 회원의 알림 설정 변경
UPDATE alarm
SET calendar = 'N',
    email = 'Y',
    sms = 'Y',
    updated_at = CURRENT_TIMESTAMP
WHERE usersno = 101;

-- 특정 회원의 알림 설정 삭제
DELETE FROM alarm WHERE usersno = 101;

-- 전체 삭제 (주의)
-- DELETE FROM alarm;
