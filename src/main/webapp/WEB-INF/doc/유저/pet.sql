DROP TABLE pet CASCADE CONSTRAINTS; 

CREATE TABLE pet (
    petno        NUMBER(10)     PRIMARY KEY,           -- 반려동물 번호 (PK)
    name         VARCHAR2(30),                         -- 반려동물 이름
    gender       CHAR(1),                              -- 성별
    birthday     VARCHAR2(10),                                 -- 생년월일
    description  VARCHAR2(2000),                       -- 설명
    speciesno    NUMBER(10),                           -- 종번호 (FK)
    usersno       NUMBER(10),                           -- 회원번호 (FK)
    regdate      DATE DEFAULT SYSDATE,                 -- 등록일
    file1                                   VARCHAR2(100)          NULL,  -- 원본 파일명 image
    file1saved                            VARCHAR2(100)          NULL,  -- 저장된 파일명, image
    thumb1                              VARCHAR2(100)          NULL,   -- preview image
    size1                                 NUMBER(10)      DEFAULT 0 NULL,  -- 파일 사이즈

    FOREIGN KEY (speciesno) REFERENCES species(speciesno),
    FOREIGN KEY (usersno) REFERENCES users(usersno)
);

COMMENT ON TABLE pet IS '회원이 등록한 반려동물 정보';

COMMENT ON COLUMN pet.petno       IS '반려동물 번호 (PK)';
COMMENT ON COLUMN pet.name        IS '반려동물 이름';
COMMENT ON COLUMN pet.gender      IS '성별 (M: 수컷, F: 암컷)';
COMMENT ON COLUMN pet.birthday    IS '생년월일';
COMMENT ON COLUMN pet.description IS '반려동물 설명';
COMMENT ON COLUMN pet.speciesno   IS '종 번호 (species 테이블 참조)';
COMMENT ON COLUMN pet.usersno      IS '회원 번호 (users 테이블 참조)';
COMMENT ON COLUMN pet.regdate     IS '등록일';
COMMENT ON COLUMN contents.file1 is '메인 이미지';
COMMENT ON COLUMN contents.file1saved is '실제 저장된 메인 이미지';
COMMENT ON COLUMN contents.thumb1 is '메인 이미지 Preview';
COMMENT ON COLUMN contents.size1 is '메인 이미지 크기';


DROP SEQUENCE pet_seq;

CREATE SEQUENCE pet_seq
  START WITH 1
  INCREMENT BY 1
  MAXVALUE 9999999999
  CACHE 2
  NOCYCLE;

commit;
rollback;

INSERT INTO pet (
  petno, name, gender, birthday, description, speciesno, userno
) VALUES (
  pet_seq.NEXTVAL, '콩이', 'F', TO_DATE('2022-05-03', 'YYYY-MM-DD'),
  '사람을 잘 따르는 귀여운 햄스터', 3, 1
);

commit;

--전체 목록
SELECT * FROM pet ORDER BY petno DESC;

--특정 회원의 반려동물
SELECT p.*, s.sname AS species_name
FROM pet p
JOIN species s ON p.speciesno = s.speciesno
WHERE p.userno = 1;

--특정 반려동물 상세
SELECT * FROM pet WHERE petno = 5;

UPDATE pet
SET name = '말랑이',
    gender = 'M',
    birthday = TO_DATE('2021-11-20', 'YYYY-MM-DD'),
    description = '새로운 설명으로 업데이트됨'
WHERE petno = 5;

--번호로 삭제
DELETE FROM pet WHERE petno = 5;

--회원이 등록한 모든 반려동물 삭제
DELETE FROM pet WHERE userno = 1;
















