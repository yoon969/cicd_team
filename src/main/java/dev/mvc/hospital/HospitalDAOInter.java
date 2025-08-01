package dev.mvc.hospital;

import java.util.ArrayList;
import java.util.Map;

public interface HospitalDAOInter {
  
  /**
   * 병원 등록
   * @param hospitalVO
   * @return 등록된 레코드 수
   */
  public int create(HospitalVO hospitalVO);

  /**
   * 병원 전체 목록 조회
   * @return 병원 리스트
   */
  public ArrayList<HospitalVO> list();

  /**
   * 병원 상세 조회
   * @param hospitalno 병원 번호 (PK)
   * @return 해당 병원의 정보
   */
  public HospitalVO read(int hospitalno);

  /**
   * 병원 정보 수정
   * @param hospitalVO
   * @return 수정된 레코드 수
   */
  public int update(HospitalVO hospitalVO);

  /**
   * 병원 삭제
   * @param hospitalno
   * @return 삭제된 레코드 수
   */
  public int delete(int hospitalno);
  
  public int isDuplicateAddress(String address);
  
  /**
   * 병원 전체 목록 (검색 + 페이징)
   * @param map (word, offset, limit)
   * @return 병원 목록
   */
  public ArrayList<HospitalVO> list_all_paging(Map<String, Object> map);

  /**
   * 병원 전체 개수 (검색어 포함)
   * @param map (word)
   * @return 병원 수
   */
  public int count_all(Map<String, Object> map);
}
