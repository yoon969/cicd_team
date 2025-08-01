package dev.mvc.hospital_species;

import java.util.ArrayList;

public interface HospitalSpeciesProcInter {

  /**
   * 병원-종 연결 등록
   * @param hospitalSpeciesVO
   * @return 등록된 레코드 수
   */
  public int create(HospitalSpeciesVO hospitalSpeciesVO);

  /**
   * 병원 기준 종 목록
   * @param hospitalno
   * @return 종 리스트
   */
  public ArrayList<HospitalSpeciesVO> listByHospitalno(int hospitalno);

  /**
   * 종 기준 병원 목록
   * @param speciesno
   * @return 병원 리스트
   */
  public ArrayList<HospitalSpeciesVO> list_by_speciesno(int speciesno);

  /**
   * 연결 단건 삭제
   * @param id
   * @return 삭제된 레코드 수
   */
  public int delete(int id);

  /**
   * 병원 기준 전체 삭제
   * @param hospitalno
   * @return 삭제된 레코드 수
   */
  public int delete_by_hospitalno(int hospitalno);

  /**
   * 종 기준 전체 삭제
   * @param speciesno
   * @return 삭제된 레코드 수
   */
  public int delete_by_speciesno(int speciesno);
}
