CREATE TABLE feedback (
  feedbackno  NUMBER(10) PRIMARY KEY,         -- 피드백 번호 (PK)
  usersno     NUMBER(10),                     -- 작성자 (FK → users.usersno)
  content     VARCHAR2(1000),                 -- 피드백 내용
  created_at  DATE DEFAULT SYSDATE,           -- 작성일시
  visible     CHAR(1) DEFAULT 'Y',            -- 표시 여부
  FOREIGN KEY (usersno) REFERENCES users(usersno)
);

COMMENT ON TABLE feedback IS '사용자 피드백 테이블';

COMMENT ON COLUMN feedback.feedbackno IS '피드백 번호 (PK)';
COMMENT ON COLUMN feedback.usersno IS '작성자 번호 (users 테이블 참조)';
COMMENT ON COLUMN feedback.content IS '피드백 내용';
COMMENT ON COLUMN feedback.created_at IS '작성일시';
COMMENT ON COLUMN feedback.visible IS '게시 여부 (Y/N)';

CREATE SEQUENCE feedback_seq
  START WITH 1                -- 시작 번호
  INCREMENT BY 1            -- 증가값
  MAXVALUE 9999999999  -- 최대값: 9999999999 --> NUMBER(10) 대응
  CACHE 2                        -- 2번은 메모리에서만 계산
  NOCYCLE;                      -- 다시 1부터 생성되는 것을 방지
