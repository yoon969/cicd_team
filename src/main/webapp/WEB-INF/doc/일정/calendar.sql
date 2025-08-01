DROP TABLE calendar;
DROP TABLE calendar CASCADE CONSTRAINTS; 

CREATE TABLE calendar (
  calendarno  NUMBER(10) NOT NULL, -- AUTO_INCREMENT 대체
  labeldate   VARCHAR2(10)  NOT NULL, -- 출력할 날짜 2013-10-20
  label       VARCHAR2(50)  NOT NULL, -- 달력에 출력될 레이블
  title       VARCHAR2(100) NOT NULL, -- 제목(*)
  content     CLOB          NOT NULL, -- 글 내용
  cnt         NUMBER        DEFAULT 0, -- 조회수
  seqno       NUMBER(5)     DEFAULT 1 NOT NULL, -- 일정 출력 순서
  regdate     DATE          NOT NULL, -- 등록 날짜
  usersno    NUMBER(10)     NOT NULL , -- FK
  summary       VARCHAR2(1000),                       -- 요약
  emotion       NUMBER(1),                            -- 감정 분석 결과
  type VARCHAR2(50),
  emoji VARCHAR2(10),
  PRIMARY KEY (calendarno),
  FOREIGN KEY (usersno) REFERENCES users (usersno) -- 일정을 등록한 관리자 
);

DROP SEQUENCE calendar_seq;

CREATE SEQUENCE calendar_seq
START WITH 1         -- 시작 번호
INCREMENT BY 1       -- 증가값
MAXVALUE 9999999999  -- 최대값: 9999999999 --> NUMBER(10) 대응
CACHE 2              -- 2번은 메모리에서만 계산
NOCYCLE;             -- 다시 1부터 생성되는 것을 방지


-- 데이터 삽입
INSERT INTO calendar(calendarno, labeldate, label, title, content, cnt, seqno, regdate, usersno)
VALUES (calendar_seq.nextval, '2024-12-24', '크리스마스 이브', '메리 크리스마스', '즐거운 크리스마스 되세요.', 0, 1, sysdate, 1);

INSERT INTO calendar(calendarno, labeldate, label, title, content, cnt, seqno, regdate, usersno)
VALUES (calendar_seq.nextval, '2024-12-25', '휴강 안내', '메리 크리스마스', '휴강입니다.', 0, 1, sysdate, 1);

INSERT INTO calendar(calendarno, labeldate, label, title, content, cnt, seqno, regdate, usersno)
VALUES (calendar_seq.nextval, '2024-12-25', '학원 출입 안내', '학원 출입 안내', '크리스마스 기간에 학원 입실 안됩니다.', 0, 2, sysdate, 1);

INSERT INTO calendar(calendarno, labeldate, label, title, content, cnt, seqno, regdate, usersno)
VALUES (calendar_seq.nextval, '2025-01-01', '새해 첫날 학원 출입 안내', '새해 첫날 학원 출입 안내', '새해 첫날에 학원 입실 안됩니다.', 0, 2, sysdate, 1);

commit;

-- 전체 목록
SELECT calendarno, labeldate, label, title, content, cnt, seqno, regdate, usersno
FROM calendar
ORDER BY calendarno DESC;

ALTER TABLE calendar ADD type VARCHAR2(50);

SELECT COLUMN_NAME FROM USER_TAB_COLUMNS WHERE TABLE_NAME = 'CALENDAR';


commit;
