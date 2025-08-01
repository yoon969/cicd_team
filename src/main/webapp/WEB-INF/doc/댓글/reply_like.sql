-- 댓글 좋아요 테이블
CREATE TABLE reply_like (
  replylikeno NUMBER(10) PRIMARY KEY,                 -- 좋아요 번호 (PK)
  replyno     NUMBER(10) NOT NULL,                    -- 대상 댓글 번호 (FK)
  usersno     NUMBER(10) NOT NULL,                    -- 좋아요 누른 회원번호 (FK)
  rdate       DATE DEFAULT SYSDATE,                   -- 좋아요 등록일시

  CONSTRAINT fk_reply_like_reply FOREIGN KEY (replyno) REFERENCES reply(replyno),
  CONSTRAINT fk_reply_like_users FOREIGN KEY (usersno) REFERENCES users(usersno)
);

ALTER TABLE reply_like ADD CONSTRAINT unique_reply_user UNIQUE (replyno, usersno);

-- 시퀀스 생성
CREATE SEQUENCE reply_like_seq
  START WITH 1
  INCREMENT BY 1
  MAXVALUE 999999999
  NOCACHE
  NOCYCLE;

-- 주석
COMMENT ON TABLE reply_like IS '댓글 좋아요 정보';
COMMENT ON COLUMN reply_like.replylikeno IS '댓글 좋아요 번호 (PK)';
COMMENT ON COLUMN reply_like.replyno IS '좋아요가 달린 댓글 번호 (reply 테이블 참조)';
COMMENT ON COLUMN reply_like.usersno IS '좋아요 누른 사용자 번호 (users 테이블 참조)';
COMMENT ON COLUMN reply_like.rdate IS '좋아요 등록일시';

COMMIT;

-- 좋아요 등록
INSERT INTO reply_like (replylikeno, replyno, usersno)
VALUES (reply_like_seq.NEXTVAL, 1, 101);  -- 댓글번호 1에 회원번호 101이 좋아요
-- 특정 댓글의 총 좋아요 수
SELECT COUNT(*) FROM reply_like WHERE replyno = 1;

-- 특정 회원이 해당 댓글을 좋아요 했는지 확인
SELECT COUNT(*) FROM reply_like
WHERE replyno = 1 AND usersno = 101;

-- 특정 댓글에 좋아요 누른 회원 목록
SELECT rl.replylikeno, rl.replyno, u.usersname, rl.rdate
FROM reply_like rl
  JOIN users u ON rl.usersno = u.usersno
WHERE rl.replyno = 1
ORDER BY rl.rdate DESC;

-- 특정 회원이 누른 좋아요 취소 (단건)
DELETE FROM reply_like
WHERE replyno = 1 AND usersno = 101;

-- 특정 댓글의 모든 좋아요 삭제
DELETE FROM reply_like
WHERE replyno = 1;

-- 특정 회원이 누른 모든 댓글 좋아요 삭제
DELETE FROM reply_like
WHERE usersno = 101;

-- 테이블 삭제
DROP TABLE reply_like CASCADE CONSTRAINTS;

-- 시퀀스 삭제
DROP SEQUENCE reply_like_seq;

-- 테이블 존재 확인
SELECT table_name FROM user_tables WHERE table_name = 'REPLY_LIKE';

-- 시퀀스 존재 확인
SELECT sequence_name FROM user_sequences WHERE sequence_name = 'REPLY_LIKE_SEQ';

COMMIT;
