package dev.mvc.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface MemoryProcInter {

  /**
   * 추억 이미지 등록
   * @param memoryVO
   * @return 등록된 레코드 수
   */
  public int create(MemoryVO memoryVO);

  /**
   * 전체 목록
   * @return MemoryVO 리스트
   */
  public ArrayList<MemoryVO> list();

  /**
   * 사용자 기준 목록
   * @param usersno
   * @return MemoryVO 리스트
   */
  public ArrayList<MemoryVO> listByUsersno(int usersno);

  /**
   * 상세 조회
   * @param memoryno
   * @return MemoryVO
   */
  public MemoryVO read(int memoryno);

  /**
   * 단건 삭제
   * @param memoryno
   * @return 삭제된 레코드 수
   */
  public int delete(int memoryno);

  /**
   * 회원 기준 전체 삭제
   * @param usersno
   * @return 삭제된 레코드 수
   */
  public int deleteByUsersno(int usersno);
  
  public List<MemoryVO> list_by_paging(Map<String, Object> map);
  public int total();
  
  public int total_by_usersno(int usersno); // 🔹 추가
}
