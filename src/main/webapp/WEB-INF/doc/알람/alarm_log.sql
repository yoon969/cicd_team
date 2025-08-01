-- 📌 알림 내역 테이블 생성
CREATE TABLE alarm_log (
  alarmno   NUMBER(10) PRIMARY KEY,            -- 알림 고유번호 (PK)
  usersno   NUMBER(10) NOT NULL,               -- 알림 받을 사용자 번호 (FK)
  content   VARCHAR2(500) NOT NULL,            -- 알림 메시지 내용
  url       VARCHAR2(255),                     -- 클릭 시 이동할 경로
  checked   CHAR(1) DEFAULT 'N',               -- 읽음 여부 ('N': 안 읽음, 'Y': 읽음)
  rdate     DATE DEFAULT SYSDATE,              -- 생성일시
  type      VARCHAR2(50),                      -- 알림 유형 (예: 댓글, 좋아요 등)
  postno    NUMBER(10),                        -- 관련 게시글 번호 (선택)
  msg       VARCHAR2(200),                     -- 메시지 요약 또는 설명 (선택)

  FOREIGN KEY (usersno) REFERENCES users(usersno)
);

-- 📌 시퀀스 생성
CREATE SEQUENCE alarm_log_seq
  START WITH 1
  INCREMENT BY 1
  NOCACHE;

-- 📌 주석 추가
COMMENT ON TABLE alarm_log IS '사용자 알림 내역 테이블';

COMMENT ON COLUMN alarm_log.alarmno IS '알림 고유번호 (PK)';
COMMENT ON COLUMN alarm_log.usersno IS '알림을 받는 사용자 번호 (FK)';
COMMENT ON COLUMN alarm_log.content IS '알림 메시지 내용';
COMMENT ON COLUMN alarm_log.url IS '알림 클릭 시 이동할 경로';
COMMENT ON COLUMN alarm_log.checked IS '읽음 여부 (Y/N)';
COMMENT ON COLUMN alarm_log.rdate IS '알림 생성일시';
COMMENT ON COLUMN alarm_log.type IS '알림 유형';
COMMENT ON COLUMN alarm_log.postno IS '관련 게시글 번호';
COMMENT ON COLUMN alarm_log.msg IS '간략 메시지';

COMMIT;

-- 📌 테스트 데이터 삽입 (예: 댓글 알림)
INSERT INTO alarm_log (alarmno, usersno, content, url)
VALUES (
  alarm_log_seq.NEXTVAL,
  101,
  '홍길동님이 회원님의 게시글에 댓글을 남겼습니다.',
  '/post/read?postno=5'
);

-- 📌 특정 사용자의 최근 알림 10개 조회 (최신순)
SELECT *
FROM alarm_log
WHERE usersno = 101
ORDER BY rdate DESC
FETCH FIRST 10 ROWS ONLY;

-- 📌 안 읽은 알림 개수 조회
SELECT COUNT(*)
FROM alarm_log
WHERE usersno = 101 AND checked = 'N';

-- 📌 특정 알림을 읽음 처리
UPDATE alarm_log
SET checked = 'Y'
WHERE alarmno = 12 AND usersno = 101;

-- 📌 특정 알림 삭제
DELETE FROM alarm_log
WHERE alarmno = 12;

-- 📌 특정 사용자의 모든 알림 삭제
DELETE FROM alarm_log
WHERE usersno = 101;

-- ⚠ 전체 알림 삭제 (주의!)
-- DELETE FROM alarm_log;

COMMIT;
