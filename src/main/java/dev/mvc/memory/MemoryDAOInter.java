package dev.mvc.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface MemoryDAOInter {

  /**
   * 추억 이미지 등록
   * @param memoryVO
   * @return 등록된 레코드 수
   */
  public int create(MemoryVO memoryVO);

  /**
   * 전체 추억 이미지 목록 조회 (관리자용)
   * @return MemoryVO 리스트
   */
  public ArrayList<MemoryVO> list();

  /**
   * 특정 사용자의 추억 이미지 목록 조회
   * @param usersno
   * @return MemoryVO 리스트
   */
  public ArrayList<MemoryVO> listByUsersno(int usersno);

  /**
   * 특정 추억 이미지 상세 조회
   * @param memoryno
   * @return MemoryVO
   */
  public MemoryVO read(int memoryno);

  /**
   * 특정 이미지 삭제
   * @param memoryno
   * @return 삭제된 레코드 수
   */
  public int delete(int memoryno);

  /**
   * 사용자 탈퇴 시 전체 삭제
   * @param usersno
   * @return 삭제된 레코드 수
   */
  public int deleteByUsersno(int usersno);
  
  public List<MemoryVO> list_by_paging(Map<String, Object> map);
  public int total();
  
//🔹 사용자별 전체 개수
 public int total_by_usersno(int usersno);
}
