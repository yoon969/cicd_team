package dev.mvc.postgood;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface PostgoodDAOInter {
  /**
   * 등록, 추상 메소드
   * @param postgoodVO
   * @return
   */
  public int create(PostgoodVO postgoodVO);
  
  /**
   * 모든 목록
   * @return
   */
  public ArrayList<PostgoodVO> list_all();
  
  /**
   * postno + usersno로 좋아요 취소
   * @param map
   * @return
   */
  public int deleteByPostnoUsersno(Map<String, Object> map);
  
  /**
   * 특정 컨텐츠의 특정 회원 추천 갯수 산출
   * @param map
   * @return
   */
  public int hartCnt(Map<String, Object> map);

  /**
   * 조회
   * @param postgoodno
   * @return
   */
  public PostgoodVO read(int postgoodno);

  /**
   * postno, memberno로 조회
   * @param map
   * @return
   */
  public PostgoodVO readByPostnoUsersno(HashMap<String, Object> map);
  
  /**
   * 모든 목록, 테이블 3개 join
   * @return
   */
  public ArrayList<PostPostgoodUsersVO> list_all_join();
  
  public int deleteByPostno(int postno);

}




