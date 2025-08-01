package dev.mvc.species;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dev.mvc.post.PostVO;

public interface SpeciesProcInter {
  /**
   * 등록
   * @param speciesVO
   * @return
   */
  public int create(SpeciesVO speciesVO);
  
  /**
   * 전체 목록
   * @return
   */
  public ArrayList<SpeciesVO> list_all();

  /**
   * 조회
   * @param speciesno
   * @return
   */
  public SpeciesVO read(int speciesno); 

  /**
   * 수정
   * @param int
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
   * @param speciesVO
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
   * 화면 상단 메뉴
   * @return
   */
  public ArrayList<SpeciesVOMenu> menu();

  /**
   * 카테고리 그룹 목록
   * @return
   */
  public ArrayList<String> grpset();

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
   * 검색 + 페이징 목록
   * select id="list_search_paging" resultType="dev.mvc.species.SpeciesVO" parameterType="Map" 
   * @param word 검색어
   * @param now_page 현재 페이지, 시작 페이지 번호: 1 ★
   * @param record_per_page 페이지당 출력할 레코드 수
   * @return
   */
  public ArrayList<SpeciesVO> list_search_paging(String word, int now_page, int record_per_page);

  /** 
   * SPAN태그를 이용한 박스 모델의 지원, 1 페이지부터 시작 
   * 현재 페이지: 11 / 22   [이전] 11 12 13 14 15 16 17 18 19 20 [다음] 
   *
   * @param now_page  현재 페이지
   * @param word 검색어
   * @param list_url 페이지 버튼 클릭시 이동할 주소, @GetMapping(value="/list_search") 
   * @param search_count 검색 레코드수   
   * @param record_per_page 페이지당 레코드 수
   * @param page_per_block 블럭당 페이지 수
   * @return 페이징 생성 문자열
   */
  String pagingBox(int now_page, String word, String list_url, int search_count, int record_per_page,
      int page_per_block);

  /**
   * 갯수 전달받아 대분류 감소
   * @return
   */
  public int update_cnt_by_speciesno(Map<String, Object> map);

  public List<SpeciesVO> listByGrp(String grp);
  
  /**
   * 여러 중분류(SpeciesVO)에 해당하는 게시글 리스트
   * @param speciesList
   * @return
   */
  public List<PostVO> listBySpeciesList(List<SpeciesVO> speciesList);
  
  public List<SpeciesVO> list_all_name_y();
  
  public List<SpeciesVO> list_by_grp_y(@Param("grp") String grp);
  
  public List<SpeciesVO> list_all_y();

}


