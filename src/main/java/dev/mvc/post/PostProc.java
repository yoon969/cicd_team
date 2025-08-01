package dev.mvc.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mvc.alarm_log.AlarmLogDAOInter;
import dev.mvc.alarm_log.AlarmLogVO;
import dev.mvc.post.PostVO;
import dev.mvc.reply.ReplyDAOInter;
import dev.mvc.reply.ReplyVO;
import dev.mvc.species.SpeciesDAOInter;
import dev.mvc.species.SpeciesVO;
import dev.mvc.tool.Security;

@Component("dev.mvc.post.PostProc")
public class PostProc implements PostProcInter {
  @Autowired
  Security security;

  @Autowired
  private ReplyDAOInter replyDAO;
  
  @Autowired
  private PostDAOInter postDAO;

  @Autowired
  private SpeciesDAOInter speciesDAO;

  @Autowired
  private PostDAOInter postDAOInter;

  @Autowired
  private AlarmLogDAOInter alarmLogDAO;

  @Override
  public int getPostSeq() {
    return postDAO.getPostSeq();
  }

  @Override
  public int create(PostVO postVO) {
    // 비밀번호 암호화
    String passwd = postVO.getPasswd();
    String passwd_encoded = this.security.aesEncode(passwd);
    postVO.setPasswd(passwd_encoded);

    return postDAO.create(postVO);
  }

  @Override
  public ArrayList<PostVO> list_all() {
    return postDAO.list_all();
  }

  @Override
  public PostVO read(int postno) {
    return postDAO.read(postno);
  }

  @Override
  public int update(PostVO postVO) {
    // 비밀번호 재암호화
    String passwd = postVO.getPasswd();
    String passwd_encoded = this.security.aesEncode(passwd);
    postVO.setPasswd(passwd_encoded);

    return postDAO.update(postVO);
  }

  @Override
  public int delete(int postno) {
    return postDAO.delete(postno);
  }

  @Override
  public int increaseCnt(int postno) {
    return postDAO.increaseCnt(postno);
  }

  @Override
  public int password_check(HashMap<String, Object> map) {
    String passwd = (String) map.get("passwd");
    passwd = this.security.aesEncode(passwd);
    map.put("passwd", passwd);

    return postDAO.password_check(map); // 이 쿼리는 XML에 없으므로 추가 필요
  }

  @Override
  public int search_count(HashMap<String, Object> map) {
    return postDAO.search_count(map);
  }

  @Override
  public ArrayList<PostVO> list_search_paging(HashMap<String, Object> map) {
    return postDAO.list_search_paging(map);
  }

  @Override
  public ArrayList<PostVO> list_by_speciesno(int speciesno) {
    ArrayList<PostVO> list = this.postDAO.list_by_speciesno(speciesno);
    return list;
  }

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
  @Override
  public String pagingBox(int speciesno, int now_page, String word, String list_file, int search_count,
      int record_per_page, int page_per_block) {
    // 전체 페이지 수: (double)1/10 -> 0.1 -> 1 페이지, (double)12/10 -> 1.2 페이지 -> 2 페이지
    int total_page = (int) (Math.ceil((double) search_count / record_per_page));
    // 전체 그룹 수: (double)1/10 -> 0.1 -> 1 그룹, (double)12/10 -> 1.2 그룹-> 2 그룹
    int total_grp = (int) (Math.ceil((double) total_page / page_per_block));
    // 현재 그룹 번호: (double)13/10 -> 1.3 -> 2 그룹
    int now_grp = (int) (Math.ceil((double) now_page / page_per_block));

    // 1 group: 1, 2, 3 ... 9, 10
    // 2 group: 11, 12 ... 19, 20
    // 3 group: 21, 22 ... 29, 30
    int start_page = ((now_grp - 1) * page_per_block) + 1; // 특정 그룹의 시작 페이지
    int end_page = (now_grp * page_per_block); // 특정 그룹의 마지막 페이지

    StringBuffer str = new StringBuffer(); // String class 보다 문자열 추가등의 편집시 속도가 빠름

    // style이 java 파일에 명시되는 경우는 로직에 따라 css가 영향을 많이 받는 경우에 사용하는 방법
    str.append("<style type='text/css'>");
    str.append("  #paging {text-align: center; margin-top: 5px; font-size: 1em;}");
    str.append("  #paging A:link {text-decoration:none; color:black; font-size: 1em;}");
    str.append("  #paging A:hover{text-decoration:none; background-color: #FFFFFF; color:black; font-size: 1em;}");
    str.append("  #paging A:visited {text-decoration:none;color:black; font-size: 1em;}");
    str.append("  .span_box_1{");
    str.append("    text-align: center;");
    str.append("    font-size: 1em;");
    str.append("    border: 1px;");
    str.append("    border-style: solid;");
    str.append("    border-color: #cccccc;");
    str.append("    padding:1px 6px 1px 6px; /*위, 오른쪽, 아래, 왼쪽*/");
    str.append("    margin:1px 2px 1px 2px; /*위, 오른쪽, 아래, 왼쪽*/");
    str.append("  }");
    str.append("  .span_box_2{");
    str.append("    text-align: center;");
    str.append("    background-color: #668db4;");
    str.append("    color: #FFFFFF;");
    str.append("    font-size: 1em;");
    str.append("    border: 1px;");
    str.append("    border-style: solid;");
    str.append("    border-color: #cccccc;");
    str.append("    padding:1px 6px 1px 6px; /*위, 오른쪽, 아래, 왼쪽*/");
    str.append("    margin:1px 2px 1px 2px; /*위, 오른쪽, 아래, 왼쪽*/");
    str.append("  }");
    str.append("</style>");
    str.append("<DIV id='paging'>");
//    str.append("현재 페이지: " + nowPage + " / " + totalPage + "  "); 

    // 이전 10개 페이지로 이동
    // now_grp: 1 (1 ~ 10 page)
    // now_grp: 2 (11 ~ 20 page)
    // now_grp: 3 (21 ~ 30 page)
    // 현재 2그룹일 경우: (2 - 1) * 10 = 1그룹의 마지막 페이지 10
    // 현재 3그룹일 경우: (3 - 1) * 10 = 2그룹의 마지막 페이지 20
    int _now_page = (now_grp - 1) * page_per_block;
    if (now_grp >= 2) { // 현재 그룹번호가 2이상이면 페이지수가 11페이지 이상임으로 이전 그룹으로 갈수 있는 링크 생성
      str.append("<span class='span_box_1'><A href='" + list_file + "?speciesno=" + speciesno + "&word=" + word
          + "&now_page=" + _now_page + "'>이전</A></span>");
    }

    // 중앙의 페이지 목록
    for (int i = start_page; i <= end_page; i++) {
      if (i > total_page) { // 마지막 페이지를 넘어갔다면 페이 출력 종료
        break;
      }

      if (now_page == i) { // 목록에 출력하는 페이지가 현재페이지와 같다면 CSS 강조(차별을 둠)
        str.append("<span class='span_box_2'>" + i + "</span>"); // 현재 페이지, 강조
      } else {
        // 현재 페이지가 아닌 페이지는 이동이 가능하도록 링크를 설정
        str.append("<span class='span_box_1'><A href='" + list_file + "?speciesno=" + speciesno + "&word=" + word
            + "&now_page=" + i + "'>" + i + "</A></span>");
      }
    }

    // 10개 다음 페이지로 이동
    // nowGrp: 1 (1 ~ 10 page), nowGrp: 2 (11 ~ 20 page), nowGrp: 3 (21 ~ 30 page)
    // 현재 페이지 5일경우 -> 현재 1그룹: (1 * 10) + 1 = 2그룹의 시작페이지 11
    // 현재 페이지 15일경우 -> 현재 2그룹: (2 * 10) + 1 = 3그룹의 시작페이지 21
    // 현재 페이지 25일경우 -> 현재 3그룹: (3 * 10) + 1 = 4그룹의 시작페이지 31
    _now_page = (now_grp * page_per_block) + 1; // 최대 페이지수 + 1
    if (now_grp < total_grp) {
      str.append("<span class='span_box_1'><A href='" + list_file + "?speciesno=" + speciesno + "&word=" + word
          + "&now_page=" + _now_page + "'>다음</A></span>");
    }
    str.append("</DIV>");

    return str.toString();
  }

  @Override
  public List<PostVO> listBySpecies(int speciesno) {
    return postDAOInter.listBySpecies(speciesno);
  }

  @Override
  public List<PostVO> listBySpeciesList(List<SpeciesVO> speciesList) {
    List<Integer> speciesnoList = speciesList.stream().map(SpeciesVO::getSpeciesno).toList(); // 또는
                                                                                              // collect(Collectors.toList());
    return postDAOInter.listBySpeciesList(speciesnoList);
  }

  @Override
  public int count_by_speciesno(int speciesno) {
    int cnt = this.postDAOInter.count_by_speciesno(speciesno);
    return cnt;
  }

  @Override
  public int delete_by_speciesno(int speciesno) {
    int cnt = this.postDAOInter.delete_by_speciesno(speciesno);
    return cnt;
  }

  @Override
  public int updateYoutube(PostVO postVO) {
    return postDAOInter.updateYoutube(postVO); // MyBatis 또는 JPA DAO 호출
  }

  @Override
  public int updateMap(PostVO postVO) {
    return postDAOInter.updateMap(postVO);
  }

  @Override
  public List<PostVO> searchBySpecies(Map<String, Object> map) {
      return postDAOInter.searchBySpecies(map);
  }

  @Override
  public List<PostVO> search(String keyword) {
    return postDAOInter.search(keyword);
  }

  @Override
  public List<PostVO> searchPaging(Map<String, Object> map) {
      return this.postDAOInter.searchPaging(map);
  }

  @Override
  public int countSearch(String word) {
    return postDAOInter.countSearch(word);
  }

  @Override
  public List<PostVO> searchByGrpAndWord(Map<String, Object> map) {
      return this.postDAO.searchByGrpAndWord(map);
  }

  @Override
  public int getRecom(int postno) {
    return postDAO.getRecom(postno);
  }
  
  @Override
  public int decreaseRecom(int postno) {
    return this.postDAO.decreaseRecom(postno);
  }

  @Override
  public List<PostVO> listPagingByGrp(Map<String, Object> map) {
    return this.postDAO.listPagingByGrp(map);
  }

  @Override
  public int countByGrp(String grp) {
    return this.postDAO.countByGrp(grp);
  }

  @Override
  public int countSearchByGrp(Map<String, Object> map) {
    return this.postDAO.countSearchByGrp(map);
  }

  @Override
  public int countByUsersno(int usersno) {
    return this.postDAO.countByUsersno(usersno);
  }

  @Override
  public List<PostVO> listByUsersno(int usersno) {
    return this.postDAO.listByUsersno(usersno);
  }

  @Override
  public ArrayList<PostVO> listByUsersnoAll(int usersno) {
    return this.postDAO.listByUsersnoAll(usersno);
  }

  @Override
  public ArrayList<PostVO> listByUsersnoFiltered(int usersno, String keyword, String sort) {
    Map<String, Object> map = new HashMap<>();
    map.put("usersno", usersno);
    map.put("keyword", keyword);
    map.put("sort", sort);
    return postDAO.listByUsersnoFiltered(map);
  }
  
  @Override
  public int countByUsersnoFiltered(Map<String, Object> map) {
      return postDAO.countByUsersnoFiltered(map);
  }

  @Override
  public List<PostVO> listByUsersnoPaging(Map<String, Object> map) {
      return postDAO.listByUsersnoPaging(map);
  }

  @Override
  public int getWriter(int postno) {
    return this.postDAO.getWriter(postno);
  }

  @Override
  public int increaseRecom(int postno) {
      int result = postDAO.increaseRecom(postno);

      if (result > 0) {
          int writerUsersno = postDAO.getWriter(postno);

          AlarmLogVO alarm = new AlarmLogVO();
          alarm.setUsersno(writerUsersno); // 게시글 작성자 번호
          alarm.setType("POST_LIKE");      // 알림 타입
          alarm.setPostno(postno);         // 게시글 번호
          alarm.setContent("내 게시글에 좋아요가 눌렸어요!"); // ✅ 변경: content에 메시지 입력
          alarm.setUrl("/post/read?postno=" + postno);        // 클릭 시 이동할 URL

          alarmLogDAO.create(alarm); // 알림 저장
      }

      return result;
  }
}
