CREATE TABLE hospital_species (
    id           NUMBER(10)     PRIMARY KEY,              -- 병원-종 연결 고유 ID
    hospitalno   NUMBER(10),                              -- 병원 번호 (FK)
    speciesno    NUMBER(10),                              -- 종 번호 (FK)

    FOREIGN KEY (hospitalno) REFERENCES hospital(hospitalno),
    FOREIGN KEY (speciesno)  REFERENCES species(speciesno)
);

CREATE SEQUENCE hospital_species_seq
  START WITH 1              -- 시작 번호
  INCREMENT BY 1          -- 증가값
  MAXVALUE 9999999999 -- 최대값: 9999999 --> NUMBER(7) 대응
  CACHE 2                       -- 2번은 메모리에서만 계산
  NOCYCLE;                     -- 다시 1부터 생성되는 것을 방지
 

COMMENT ON TABLE hospital_species IS '병원이 진료 가능한 반려동물 종을 연결하는 매핑 테이블';

COMMENT ON COLUMN hospital_species.id         IS '병원-종 연결 고유 ID (PK)';
COMMENT ON COLUMN hospital_species.hospitalno IS '병원 번호 (hospital 테이블 참조)';
COMMENT ON COLUMN hospital_species.speciesno  IS '진료 가능한 종 번호 (species 테이블 참조)';

commit;

INSERT INTO hospital_species (
  id, hospitalno, speciesno
) VALUES (
  병원종ID, 병원번호, 종번호
);

INSERT INTO hospital_species (
  id, hospitalno, speciesno
) VALUES (
  1, 101, 5
);

commit;

--전체 조회
SELECT * FROM hospital_species ORDER BY id;

--병원번호로 해당 병원이 진료 가능한 종 조회
SELECT hs.id, hs.hospitalno, hs.speciesno, s.sname, s.grp
FROM hospital_species hs
  JOIN species s ON hs.speciesno = s.speciesno
WHERE hs.hospitalno = 101;

--종번호로 해당 종을 진료 가능한 병원 조회
SELECT hs.id, hs.hospitalno, h.name, h.address
FROM hospital_species hs
  JOIN hospital h ON hs.hospitalno = h.hospitalno
WHERE hs.speciesno = 5;

UPDATE hospital_species
SET hospitalno = 102, speciesno = 3
WHERE id = 1;

--ID로 삭제
DELETE FROM hospital_species
WHERE id = 1;

--특정 병원의 모든 매핑 삭제
DELETE FROM hospital_species
WHERE hospitalno = 101;


--특정 종에 대한 모든 병원 매핑 삭제
DELETE FROM hospital_species
WHERE speciesno = 5;

-- 월드펫동물병원(병원번호: 19)이 진료 가능한 동물 등록 예시
INSERT INTO hospital_species (id, hospitalno, speciesno)
VALUES (hospital_species_seq.NEXTVAL, 19, 19); -- 도마뱀

INSERT INTO hospital_species (id, hospitalno, speciesno)
VALUES (hospital_species_seq.NEXTVAL, 19, 21); -- 뱀

INSERT INTO hospital_species (id, hospitalno, speciesno)
VALUES (hospital_species_seq.NEXTVAL, 19, 22); -- 거북이

INSERT INTO hospital_species (id, hospitalno, speciesno)
VALUES (hospital_species_seq.NEXTVAL, 19, 30); -- 앵무새

INSERT INTO hospital_species (id, hospitalno, speciesno)
VALUES (hospital_species_seq.NEXTVAL, 19, 31); -- 개구리
