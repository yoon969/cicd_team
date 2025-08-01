package dev.mvc.species;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.mvc.post.PostDAOInter;
import dev.mvc.post.PostVO;

// Service, Process, Manager: DAO 호출 및 알고리즘 구현
@Service("dev.mvc.species.SpeciesProc")
public class SpeciesProc implements SpeciesProcInter {
  @Autowired
  private SpeciesDAOInter speciesDAO;
  
  @Autowired
  private PostDAOInter postDAO;
  
  @Override
  public int create(SpeciesVO speciesVO) {
    int cnt = this.speciesDAO.create(speciesVO);
    return cnt;
  }

  @Override
  public ArrayList<SpeciesVO> list_all() {
    ArrayList<SpeciesVO> list = this.speciesDAO.list_all();
    return list;
  }

  @Override
  public SpeciesVO read(int speciesno) {
    SpeciesVO speciesVO = this.speciesDAO.read(speciesno);
    return speciesVO;
  }

  @Override
  public int update(SpeciesVO speciesVO) {
    int cnt = this.speciesDAO.update(speciesVO);
    return cnt;
  }

  @Override
  public int delete(int speciesno) {
    int cnt = this.speciesDAO.delete(speciesno);
    return cnt;
  }

  @Override
  public int update_seqno_forward(int speciesno) {
    int cnt = this.speciesDAO.update_seqno_forward(speciesno);
    return cnt;
  }

  @Override
  public int update_seqno_backward(int speciesno) {
    int cnt = this.speciesDAO.update_seqno_backward(speciesno);
    return cnt;
  }

  @Override
  public int update_visible_y(int speciesno) {
    int cnt = this.speciesDAO.update_visible_y(speciesno);
    return cnt;
  }

  @Override
  public int update_visible_n(int speciesno) {
    int cnt = this.speciesDAO.update_visible_n(speciesno);
    return cnt;
  }

  @Override
  public ArrayList<SpeciesVO> list_all_grp_y() {
    ArrayList<SpeciesVO> list = this.speciesDAO.list_all_grp_y();
    return list;
  }

  @Override
  public ArrayList<SpeciesVO> list_all_name_y(String grp) {
    ArrayList<SpeciesVO> list = this.speciesDAO.list_all_name_y(grp);
    return list;
  }

  @Override
  public ArrayList<SpeciesVOMenu> menu() {
    ArrayList<SpeciesVOMenu> menu = new ArrayList<SpeciesVOMenu>();
    ArrayList<SpeciesVO> grps = this.speciesDAO.list_all_grp_y(); // 공개된 대분류 목록
    
    for(SpeciesVO speciesVO: grps) {
      SpeciesVOMenu speciesVOMenu = new SpeciesVOMenu();
      speciesVOMenu.setGrp(speciesVO.getGrp()); // 대분류 이름 저장
      
      // 특정 대분류에 해당하는 공개된 중분류 추출
      ArrayList<SpeciesVO> list_name = this.speciesDAO.list_all_name_y(speciesVO.getGrp());
      speciesVOMenu.setList_name(list_name);
      
      menu.add(speciesVOMenu); // 하나의 그룹에 해당하는 중분류 메뉴 객체 저장
    }
    
    return menu;
  }

  @Override
  public ArrayList<String> grpset() {
    ArrayList<String> grpset = this.speciesDAO.grpset();
    return grpset;
  }

  @Override
  public ArrayList<SpeciesVO> list_search(String word) {
    ArrayList<SpeciesVO> list = this.speciesDAO.list_search(word);
    return list;
  }

  @Override
  public int list_search_count(String word) {
    int cnt = this.speciesDAO.list_search_count(word);
    return cnt;
  }

  @Override
  /**
   * 검색 + 페이징 목록
   * select id="list_search_paging" resultType="dev.mvc.species.SpeciesVO" parameterType="Map" 
   * @param word 검색어
   * @param now_page 현재 페이지, 시작 페이지 번호: 1 ★
   * @param record_per_page 페이지당 출력할 레코드 수
   * @return
   */
  public ArrayList<SpeciesVO> list_search_paging(String word, int now_page, int record_per_page) {
    /*
     페이지당 10개의 레코드 출력
     1 page: WHERE r >= 1 AND r <= 10
     2 page: WHERE r >= 11 AND r <= 20
     3 page: WHERE r >= 21 AND r <= 30
     
     now_page 1: WHERE r >= 1 AND r <= 10
     now_page 2: WHERE r >= 11 AND r <= 20
     now_page 3: WHERE r >= 21 AND r <= 30
     
     int start_num = (now_page - 1) * record_per_page;
     int end_num=start_num + record_per_page;
     */

    int start_num = ((now_page - 1) * record_per_page) + 1;
    int end_num=(start_num + record_per_page) - 1;

    // System.out.println("WHERE r >= "+start_num+" AND r <= " + end_num);
    
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("word", word);
    map.put("start_num", start_num);
    map.put("end_num", end_num);
    
    ArrayList<SpeciesVO> list = this.speciesDAO.list_search_paging(map);
    // System.out.println("-> " + list.size());
    
    return list;
  }

  /**
   * 페이징 박스
   * HTML 여기서 처리
   */
  @Override
  public String pagingBox(int now_page, String word, String list_url, int search_count,
                          int record_per_page, int page_per_block) {
    int total_page = (int)(Math.ceil((double)search_count / record_per_page));
    int total_grp = (int)(Math.ceil((double)total_page / page_per_block));
    int now_grp = (int)(Math.ceil((double)now_page / page_per_block));

    int start_page = ((now_grp - 1) * page_per_block) + 1;
    int end_page = now_grp * page_per_block;

    StringBuilder str = new StringBuilder();
    str.append("<div class='paging'>"); //  paging

    // ◀ 이전 화살표
    if (now_page > 1) {
      int prev_page = now_page - 1;
      str.append("<span class='arrow' onclick=\"location.href='")
         .append(list_url).append("?word=").append(word)
         .append("&now_page=").append(prev_page)
         .append("'\">&#x2039;</span> ");
    }

    // ✅ 페이지 번호
    for (int i = start_page; i <= end_page && i <= total_page; i++) {
      if (now_page == i) {
        str.append("<a class='btn-page current'>").append(i).append("</a> ");
      } else {
        str.append("<a class='btn-page' href='")
           .append(list_url).append("?word=").append(word)
           .append("&now_page=").append(i)
           .append("'>").append(i).append("</a> ");
      }
    }

    // ▶ 다음 화살표
    if (now_page < total_page) {
      int next_page = now_page + 1;
      str.append("<span class='arrow' onclick=\"location.href='")
         .append(list_url).append("?word=").append(word)
         .append("&now_page=").append(next_page)
         .append("'\">&#x203A;</span> ");
    }

    str.append("</div>"); // ✅ 끝
    return str.toString();
  }


  @Override
  public int update_cnt_by_speciesno(Map<String, Object> map) {

    return 0;
  }

  @Override
  public List<SpeciesVO> listByGrp(String grp) {
      return speciesDAO.listByGrp(grp);
  }

  @Override
  public List<PostVO> listBySpeciesList(List<SpeciesVO> speciesList) {
    // speciesList에서 speciesno만 추출
    List<Integer> speciesNos = new ArrayList<>();
    for (SpeciesVO vo : speciesList) {
      speciesNos.add(vo.getSpeciesno());
    }

    // DAO에 넘겨줌
    return postDAO.listBySpeciesList(speciesNos);
  }
  
  @Override
  public List<SpeciesVO> list_all_name_y() {
    return speciesDAO.list_all_name_y();
  }
  
  @Override
  public List<SpeciesVO> list_by_grp_y(String grp) {
    return speciesDAO.list_by_grp_y(grp);
  }

  @Override
  public List<SpeciesVO> list_all_y() {
    return speciesDAO.list_all_y();
  }

}

