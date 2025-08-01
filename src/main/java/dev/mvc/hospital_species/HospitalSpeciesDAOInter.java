package dev.mvc.hospital_species;

import java.util.ArrayList;

public interface HospitalSpeciesDAOInter {

  /**
   * 병원-종 연결 등록
   * @param hospitalSpeciesVO
   * @return 등록된 레코드 수
   */
  public int create(HospitalSpeciesVO hospitalSpeciesVO);

  /**
   * 특정 병원에 연결된 종 목록 조회
   * @param hospitalno
   * @return 종 리스트
   */
  public ArrayList<HospitalSpeciesVO> listByHospitalno(int hospitalno);


  /**
   * 특정 종에 연결된 병원 목록 조회
   * @param speciesno
   * @return 병원 리스트
   */
  public ArrayList<HospitalSpeciesVO> list_by_speciesno(int speciesno);

  /**
   * 병원-종 연결 삭제 (단일)
   * @param id
   * @return 삭제된 레코드 수
   */
  public int delete(int id);

  /**
   * 특정 병원의 연결 모두 삭제
   * @param hospitalno
   * @return 삭제된 레코드 수
   */
  public int delete_by_hospitalno(int hospitalno);

  /**
   * 특정 종의 연결 모두 삭제
   * @param speciesno
   * @return 삭제된 레코드 수
   */
  public int delete_by_speciesno(int speciesno);
}
