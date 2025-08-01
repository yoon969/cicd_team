package dev.mvc.species;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dev.mvc.post.PostVO;

// MyBATIS 기준으로 추상 메소드를 만들면 Spring Boot가 자동으로 class로 구현함.
public interface SpeciesDAOInter {
  /**
   * <pre>
   * MyBATIS: insert id="create" parameterType="dev.mvc.species.SpeciesVO"
   * insert: INSERT SQL, 처리후 등록된 레코드 갯수를 리턴
   * id: 자바 메소드명
   * parameterType: MyBATS가 전달받는 VO 객체 타입
   * </pre>
   * @param speciesVO
   * @return 등록된 레코드 갯수
   */
  public int create(SpeciesVO speciesVO);
  
  /**
   * 전체 목록
   * @return
   */
  public ArrayList<SpeciesVO> list_all();

  /**
   * 검색, 전체 목록
   * @return
   */
  public ArrayList<SpeciesVO> list_search(String word);

  /**
   * 검색, 전체 레코드 갯수, 페이징 버튼 생성시 필요 ★★★★★
   * @return
   */
  public int list_search_count(String word);
  
  /**
   * 조회
   * @param speciesno
   * @return
   */
  public SpeciesVO read(int speciesno); 
  
  /**
   * 수정
   * @param speciesVO
   * @return
   */
  public int update(SpeciesVO speciesVO);

  /**
   * 삭제 처리
   * delete id="delete" parameterType="Integer"
   * @param int
   * @return 삭제된 레코드 갯수
   */
  public int delete(int speciesno);
  
  /**
   * 우선 순위 높임, 10 등 -> 1 등
   * @param int
   * @return
   */
  public int update_seqno_forward(int speciesno);

  /**
   * 우선 순위 낮춤, 1 등 -> 10 등
   * @param int
   * @return
   */
  public int update_seqno_backward(int speciesno);
  
  /**
   * 카테고리 공개 설정
   * @param int
   * @return
   */
  public int update_visible_y(int speciesno);

  /**
   * 카테고리 비공개 설정
   * @param int
   * @return
   */
  public int update_visible_n(int speciesno);

  /**
   * 공개된 대분류만 출력
   * @return
   */
  public ArrayList<SpeciesVO> list_all_grp_y();
  
  /**
   * 특정 그룹의 중분류 출력
   * @return
   */
  public ArrayList<SpeciesVO> list_all_name_y(String grp);

  /**
   * 카테고리 그룹 목록
   * @return
   */
  public ArrayList<String> grpset();

  /**
   * 검색, 전체 목록
   * @return
   */
  public ArrayList<SpeciesVO> list_search_paging(Map map);

  /**
   * 갯수 전달받아 대분류 감소
   * @return
   */
  public List<PostVO> listBySpecies(int speciesno);

  public List<SpeciesVO> listByGrp(String grp);
  
  /**
   * 여러 중분류(SpeciesVO)에 해당하는 게시글 리스트
   * @param speciesList
   * @return
   */
  public List<PostVO> listBySpeciesList(List<SpeciesVO> speciesList);
  
  public List<SpeciesVO> list_all_name_y();
  
  public List<SpeciesVO> list_by_grp_y(@Param("grp") String grp);
  
  public List<SpeciesVO> list_all_y();  // 중분류만

  
}






