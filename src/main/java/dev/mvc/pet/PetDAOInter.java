package dev.mvc.pet;

import java.util.ArrayList;

public interface PetDAOInter {

  /**
   * 반려동물 등록
   * @param petVO
   * @return 등록된 레코드 수
   */
  public int create(PetVO petVO);

  /**
   * 전체 반려동물 목록
   * @return 목록
   */
  public ArrayList<PetVO> list();

  /**
   * 특정 사용자 기준 목록
   * @param userno
   * @return 목록
   */
  public ArrayList<PetVO> listByUserno(int userno);

  /**
   * 반려동물 상세 조회
   * @param petno
   * @return PetVO
   */
  public PetVO read(int petno);
  
  /**
   * 조인 반려동물 상세 조회
   * @param petno
   * @return PetVO
   */
public PetJoinVO readJoin(int petno);

  /**
   * 반려동물 정보 수정
   * @param petVO
   * @return 수정된 레코드 수
   */
  public int update(PetVO petVO);

  /**
   * 반려동물 삭제
   * @param petno
   * @return 삭제된 레코드 수
   */
  public int delete(int petno);
  
  public ArrayList<PetJoinVO> listByUsersnoJoin(int usersno);
}
