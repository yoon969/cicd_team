-- 📌 SMS 전송 로그 테이블 생성
CREATE TABLE sms_log (
  log_id        NUMBER PRIMARY KEY,                  -- 로그 고유 ID (PK)
  recipient     VARCHAR2(20 BYTE) NOT NULL,          -- 수신자 전화번호
  message       VARCHAR2(255 BYTE) NOT NULL,         -- 전송된 메시지 내용
  send_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 메시지 전송 시간
  status        VARCHAR2(20 BYTE) NOT NULL,          -- 전송 상태 (SUCCESS / FAIL)
  response_msg  VARCHAR2(500 BYTE),                  -- API 응답 메시지
  retry_count   NUMBER DEFAULT 0                     -- 재시도 횟수 (기본값 0)
);

-- 📌 시퀀스 생성
CREATE SEQUENCE sms_log_seq
  START WITH 1
  INCREMENT BY 1
  NOCACHE;

-- 📌 주석 추가
COMMENT ON TABLE sms_log IS 'SMS 전송 로그 테이블';

COMMENT ON COLUMN sms_log.log_id        IS '로그 고유 ID (PK)';
COMMENT ON COLUMN sms_log.recipient     IS '수신자 전화번호';
COMMENT ON COLUMN sms_log.message       IS '전송된 메시지 내용';
COMMENT ON COLUMN sms_log.send_time     IS '메시지 전송 시간 (TIMESTAMP)';
COMMENT ON COLUMN sms_log.status        IS '전송 상태 (SUCCESS/FAIL)';
COMMENT ON COLUMN sms_log.response_msg  IS 'API 응답 메시지';
COMMENT ON COLUMN sms_log.retry_count   IS '재시도 횟수 (기본값 0)';

COMMIT;
