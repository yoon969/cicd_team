package dev.mvc.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dev.mvc.species.SpeciesVO;

public interface PostDAOInter {

  /** 시퀀스 번호 생성 */
  public int getPostSeq();

  /** 게시글 등록 */
  public int create(PostVO postVO);

  /** 전체 게시글 목록 */
  public ArrayList<PostVO> list_all();

  /** 게시글 상세 조회 */
  public PostVO read(int postno);

  /** 게시글 수정 */
  public int update(PostVO postVO);

  /** 게시글 삭제 */
  public int delete(int postno);

  /** 조회수 증가 */
  public int increaseCnt(int postno);

  /** 검색 결과 개수 */
  public int search_count(HashMap<String, Object> map);

  /** 검색 + 페이징 목록 */
  public ArrayList<PostVO> list_search_paging(HashMap<String, Object> map);

  /** 패스워드 검사 */
  public int password_check(HashMap<String, Object> map);

  /** 카테고리로 게시글 목록 조회 */
  public ArrayList<PostVO> list_by_speciesno(int speciesno);
  
  /**
   * SPAN태그를 이용한 박스 모델의 지원, 1 페이지부터 시작 현재 페이지: 11 / 22 [이전] 11 12 13 14 15 16 17
   * 18 19 20 [다음]
   * 
   * @param now_page        현재 페이지
   * @param word            검색어
   * @param list_file       목록 파일명
   * @param search_count    검색 레코드수
   * @param record_per_page 페이지당 레코드 수
   * @param page_per_block  블럭당 페이지 수
   * @return 페이징 생성 문자열
   */
  String pagingBox(int species, int now_page, String word, String list_file, int search_count, int record_per_page,
      int page_per_block);

  public List<PostVO> listBySpecies(int speciesno);
  
  /**
   * 여러 중분류를 받아 해당하는 게시글 반환
   * @param speciesList 중분류 리스트
   * @return 게시글 리스트
   */

  public List<PostVO> listBySpeciesList(List<Integer> speciesNos);
  
  /**
   * FK speciesno 값이 같은 레코드 갯수 산출
   * @param speciesno
   * @return
   */
  public int count_by_speciesno(int speciesno);
  
  /**
   * 특정 카테고리에 속한 모든 레코드 삭제
   * @param speciesno
   * @return 삭제된 레코드 갯수
   */
  public int delete_by_speciesno(int speciesno);

  public int updateYoutube(PostVO postVO);
  
  public int updateMap(PostVO postVO);

   //🔍 키워드 검색
  public List<PostVO> searchBySpecies(Map<String, Object> map);
   
   public List<PostVO> search(String word);
  
   /**
    * 검색어 포함 게시물 목록 (페이징 포함)
    * @param word
    * @param offset 시작 위치
    * @param limit 한 페이지에 보여줄 수
    * @return
    */
   public List<PostVO> searchPaging(Map<String, Object> map);
  
   /**
    * 검색어에 해당하는 총 게시물 수
    * @param word
    * @return
    */
   public int countSearch(String word);

   public List<PostVO> searchByGrpAndWord(Map<String, Object> map);

   /**
    * 게시글의 추천 수 증가
    * @param postno
    * @return 처리된 레코드 수
    */
   public int increaseRecom(@Param("postno") int postno);

   /**
    * 게시글의 추천 수 조회
    * @param postno
    * @return 추천 수
    */
   public int getRecom(@Param("postno") int postno);

   public int decreaseRecom(int postno);
   
   public List<PostVO> listPagingByGrp(Map<String, Object> map);
   
   public int countByGrp(String grp);
   
   public int countSearchByGrp(Map<String, Object> map);

   public int countByUsersno(int usersno);

   public List<PostVO> listByUsersno(int usersno);

   public ArrayList<PostVO> listByUsersnoAll(int usersno);

   public ArrayList<PostVO> listByUsersnoFiltered(Map<String, Object> map);

   public int countByUsersnoFiltered(Map<String, Object> map);
   
   public List<PostVO> listByUsersnoPaging(Map<String, Object> map);
   
   public int getWriter(int postno);

}
