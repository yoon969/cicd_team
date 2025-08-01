-- /src/main/webapp/WEB-INF/doc/컨텐츠/post_c.sql
DROP TABLE post CASCADE CONSTRAINTS; -- 자식 무시하고 삭제 가능
DROP TABLE post;

CREATE TABLE post (
    postno        NUMBER(10)     PRIMARY KEY,           -- 글번호
    title         VARCHAR2(200),                        -- 글제목
    content       CLOB,                                 -- 글내용
    recom         NUMBER(7),                            -- 추천수
    cnt           NUMBER(7),                            -- 조회수
    replycnt      NUMBER(7),                            -- 댓글수
    passwd        VARCHAR2(100),                        -- 글비밀번호
    word          VARCHAR2(2000),                       -- 검색어
    pdate         DATE DEFAULT SYSDATE,                 -- 등록일
    file1         VARCHAR2(100),                        -- 메인이미지 파일명
    file1saved    VARCHAR2(100),                        -- 실제 저장된 메인이미지
    thumb1        VARCHAR2(100),                        -- 썸네일 이미지
    size1         NUMBER(10),                           -- 메인이미지 크기
    map           VARCHAR2(1000),                       -- 지도
    youtube       VARCHAR2(1000),                       -- 유튜브영상
    summary       VARCHAR2(1000),                       -- 요약
    usersno       NUMBER(10),                           -- 회원번호 (FK)
    speciesno     NUMBER(10),                           -- 품종번호 (FK)
    emotion       NUMBER(1),                            -- 감정 분석 결과

    FOREIGN KEY (usersno) REFERENCES users(usersno),
    FOREIGN KEY (speciesno) REFERENCES species(speciesno)
);

COMMENT ON TABLE post is '컨텐츠';
COMMENT ON COLUMN post.postno is '컨텐츠 번호';
COMMENT ON COLUMN post.usersno is '관리자 번호';
COMMENT ON COLUMN post.speciesno is '종 번호';
COMMENT ON COLUMN post.title is '제목';
COMMENT ON COLUMN post.content is '내용';
COMMENT ON COLUMN post.recom is '추천수';
COMMENT ON COLUMN post.cnt is '조회수';
COMMENT ON COLUMN post.replycnt is '댓글수';
COMMENT ON COLUMN post.passwd is '패스워드';
COMMENT ON COLUMN post.word is '검색어';
COMMENT ON COLUMN post.pdate is '등록일';
COMMENT ON COLUMN post.file1 is '메인 이미지';
COMMENT ON COLUMN post.file1saved is '실제 저장된 메인 이미지';
COMMENT ON COLUMN post.thumb1 is '메인 이미지 Preview';
COMMENT ON COLUMN post.size1 is '메인 이미지 크기';
COMMENT ON COLUMN post.map is '지도';
COMMENT ON COLUMN post.youtube is 'Youtube 영상';
COMMENT ON COLUMN post.emotion is '감정 분석';
COMMENT ON COLUMN post.summary is '요약';

DROP SEQUENCE post_seq;

CREATE SEQUENCE post_seq
  START WITH 1                -- 시작 번호
  INCREMENT BY 1            -- 증가값
  MAXVALUE 9999999999  -- 최대값: 9999999999 --> NUMBER(10) 대응
  CACHE 2                        -- 2번은 메모리에서만 계산
  NOCYCLE;                      -- 다시 1부터 생성되는 것을 방지

commit;

-- 파일 포함 예시
INSERT INTO post (
  postno, title, content, passwd, file1, file1saved, thumb1, size1,
  map, youtube, summary, usersno, speciesno, emotion
) VALUES (
  post_seq.NEXTVAL, '강아지 이야기', '내용입니다', '1234',
  'dog.jpg', 'dog_1234.jpg', 'thumb_dog.jpg', 2048,
  '서울특별시 강남구', 'https://youtu.be/abc123', '귀여운 강아지 이야기', 1, 3, 1
);

commit;

--전체 게시글 목록 조회
SELECT * FROM post ORDER BY postno DESC;


--특정 게시글 상세 조회
SELECT p.*, u.id AS userid, s.sname AS speciesname
FROM post p
JOIN users u ON p.usersno = u.usersno
JOIN species s ON p.speciesno = s.speciesno
WHERE p.postno = 1;

--특정 종의 게시글 목록
SELECT * FROM post
WHERE speciesno = 3
ORDER BY postno DESC;

UPDATE post
SET title = '수정된 제목',
    content = '수정된 내용',
    summary = '수정된 요약',
    map = '수정된 지도 위치'
WHERE postno = 1;
        
UPDATE post
SET content = '내용 수정됨'
WHERE postno = 1 AND passwd = '1234';
       
--게시글 번호로 삭제
DELETE FROM post
WHERE postno = 1;


--비밀번호 인증 후 삭제
DELETE FROM post
WHERE postno = 1 AND passwd = '1234';

--특정 회원의 모든 게시글 삭제
DELETE FROM post
WHERE usersno = 1;

ALTER TABLE post MODIFY map VARCHAR2(2000);
commit;

SELECT 
  P.postno, 
  P.usersno, 
  P.title, 
  U.usersname AS nickname
FROM post P
LEFT JOIN users U ON P.usersno = U.usersno
ORDER BY P.postno DESC;

SELECT * FROM species WHERE speciesno = 0;
