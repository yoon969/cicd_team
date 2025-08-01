CREATE TABLE memory (
    memoryno              NUMBER(10)     PRIMARY KEY,           -- 변환 번호 (PK)
    image_url             VARCHAR2(255),                        -- 원본 이미지 URL
    generated_image_url   VARCHAR2(255),                        -- 생성된 일러스트 이미지 URL
    create_at             DATE DEFAULT SYSDATE,                 -- 생성일자
    usersno               NUMBER(10),                           -- 회원번호 (FK)

    FOREIGN KEY (usersno) REFERENCES users(usersno)
);

COMMENT ON TABLE memory IS 'AI 추억변환기 기능을 통해 생성된 반려동물 일러스트 이미지 기록 테이블';

COMMENT ON COLUMN memory.memoryno            IS 'AI 추억 변환 결과 번호 (PK)';
COMMENT ON COLUMN memory.image_url           IS '사용자가 업로드한 원본 반려동물 이미지 URL';
COMMENT ON COLUMN memory.generated_image_url IS 'GPT 기반으로 생성된 일러스트 이미지 URL';
COMMENT ON COLUMN memory.create_at           IS '일러스트 이미지 생성 일자';
COMMENT ON COLUMN memory.usersno             IS '이미지 생성 요청한 회원 번호 (users 테이블 참조)';

CREATE SEQUENCE memory_seq
  START WITH 1
  INCREMENT BY 1
  MAXVALUE 9999999999
  CACHE 2
  NOCYCLE;

commit;

INSERT INTO memory (
  memoryno, image_url, generated_image_url, usersno
) VALUES (
  memory_seq.NEXTVAL,
  '/images/uploads/pet01.png',
  '/images/generated/illust01.png',
  1
);

-- 전체 일러스트 생성 기록 조회
SELECT * FROM memory;

-- 특정 회원이 생성한 일러스트 목록
SELECT * 
FROM memory 
WHERE usersno = 1;

-- 특정 memoryno로 조회
SELECT * 
FROM memory 
WHERE memoryno = 10;

UPDATE memory
SET 
  generated_image_url = '/images/generated/illust01_v2.png'
WHERE memoryno = 10;

-- 특정 일러스트 기록 삭제
DELETE FROM memory 
WHERE memoryno = 10;

-- 특정 회원의 생성 기록 전체 삭제
DELETE FROM memory 
WHERE usersno = 1;