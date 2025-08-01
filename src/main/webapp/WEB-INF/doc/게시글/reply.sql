CREATE TABLE reply (
    replyno     NUMBER(10)     PRIMARY KEY,            -- 댓글 번호 (PK)
    content     VARCHAR2(1000),                        -- 댓글 내용
    rdate       DATE DEFAULT SYSDATE,                  -- 작성일시
    usersno     NUMBER(10),                            -- 회원번호 (FK)
    postno      NUMBER(10),                            -- 글번호 (FK)

    FOREIGN KEY (usersno) REFERENCES users(usersno),
    FOREIGN KEY (postno) REFERENCES post(postno)
);

CREATE SEQUENCE reply_seq
  START WITH 1
  INCREMENT BY 1
  MAXVALUE 999999999
  NOCACHE
  NOCYCLE;

COMMENT ON TABLE reply IS '게시글에 달리는 댓글 정보';
COMMENT ON COLUMN reply.replyno IS '댓글 번호 (PK)';
COMMENT ON COLUMN reply.content IS '댓글 내용';
COMMENT ON COLUMN reply.rdate   IS '댓글 작성일시';
COMMENT ON COLUMN reply.usersno IS '댓글 작성자 (회원번호, users 테이블 참조)';
COMMENT ON COLUMN reply.postno  IS '댓글이 달린 게시글 번호 (post 테이블 참조)';

commit;

-- 댓글 테이블 삭제
DROP TABLE reply CASCADE CONSTRAINTS;

-- 시퀀스 삭제
DROP SEQUENCE reply_seq;

-- 테이블 존재 여부 확인
SELECT table_name FROM user_tables WHERE table_name = 'REPLY';

-- 시퀀스 존재 여부 확인
SELECT sequence_name FROM user_sequences WHERE sequence_name = 'REPLY_SEQ';

INSERT INTO reply (
  replyno, content, usersno, postno
) VALUES (
  댓글번호, '댓글 내용', 회원번호, 게시글번호
);

INSERT INTO reply (
  replyno, content, usersno, postno
) VALUES (
  1, '좋은 글 감사합니다!', 101, 12
);

--전체 댓글 목록
SELECT * FROM reply ORDER BY replyno DESC;

--특정 게시글의 댓글 목록
SELECT r.replyno, r.content, r.rdate, u.usersname, r.usersno
FROM reply r
  JOIN users u ON r.usersno = u.usersno
WHERE r.postno = 12
ORDER BY r.replyno ASC;

--특정 회원의 댓글 목록
SELECT * FROM reply
WHERE usersno = 101
ORDER BY rdate DESC;

UPDATE reply
SET content = '수정된 댓글입니다.'
WHERE replyno = 1;

--특정 댓글 삭제
DELETE FROM reply
WHERE replyno = 1;

--특정 게시글의 모든 댓글 삭제
DELETE FROM reply
WHERE postno = 12;

--특정 회원의 모든 댓글 삭제
DELETE FROM reply
WHERE usersno = 101;