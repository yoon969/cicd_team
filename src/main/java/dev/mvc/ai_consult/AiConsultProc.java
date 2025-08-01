package dev.mvc.ai_consult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("dev.mvc.ai_consult.AiConsultProc")
public class AiConsultProc implements AiConsultProcInter {
  @Autowired
  private AiConsultDAOInter aiConsultDAO;

  @Override
  public int create(AiConsultVO aiConsultVO) {
    return aiConsultDAO.create(aiConsultVO);
  }

  @Override
  public ArrayList<AiConsultVO> list_all() {
    return aiConsultDAO.list_all();
  }

  @Override
  public AiConsultVO read(int consultno) {
    return aiConsultDAO.read(consultno);
  }

  @Override
  public int update(AiConsultVO aiConsultVO) {
    return aiConsultDAO.update(aiConsultVO);
  }

  @Override
  public int delete(int consultno) {
    return aiConsultDAO.delete(consultno);
  }

  @Override
  public ArrayList<AiConsultVO> list_by_usersno(int usersno) {
    return aiConsultDAO.list_by_usersno(usersno);
  }
  
  @Override
  public boolean existsByQuestionAnswer(int usersno, String question, String answer) {
    Map<String, Object> map = new HashMap<>();
    map.put("usersno", usersno);
    map.put("question", question);
    map.put("answer", answer);
    int count = aiConsultDAO.countByQuestionAnswer(map);
    return count > 0;
  }

  @Override
  public ArrayList<AiConsultVO> list_by_usersno_paging(Map<String, Object> map) {
    return aiConsultDAO.list_by_usersno_paging(map);
  }

  @Override
  public int count_by_usersno(Map<String, Object> map) {
    return aiConsultDAO.count_by_usersno(map);
  }

  @Override
  public String pagingBox(int usersno, int total, int now_page, String word, String list_url, int pagePerBlock) {
    int recordsPerPage = 5;
    int totalPage = (int)Math.ceil((double)total / recordsPerPage);
    int totalGrp = (int)Math.ceil((double)totalPage / pagePerBlock);
    int nowGrp = (int)Math.ceil((double)now_page / pagePerBlock);
    int startPage = ((nowGrp - 1) * pagePerBlock) + 1;
    int endPage = (nowGrp * pagePerBlock);

    StringBuilder str = new StringBuilder();

 // ✅ 시작: <div class="paging">
    str.append("<div class='paging'>");

    // ◀ 이전 페이지 그룹
    if (nowGrp >= 2) {
      int prev = ((nowGrp - 1) * pagePerBlock);
      str.append("<a class='arrow' href='").append(list_url)
         .append("?now_page=").append(prev)
         .append("&word=").append(word)
         .append("'\">&#x2039;</a> "); // ‹
    }

    // 페이지 번호들
    for (int i = startPage; i <= endPage && i <= totalPage; i++) {
      if (i == now_page) {
        str.append("<a class='btn-page current'>").append(i).append("</a>");
      } else {
        str.append("<a class='btn-page' href='").append(list_url)
           .append("?now_page=").append(i)
           .append("&word=").append(word)
           .append("'>").append(i).append("</a>");
      }
    }

    // ▶ 다음 페이지 그룹
    if (nowGrp < totalGrp) {
      int next = (nowGrp * pagePerBlock) + 1;
      str.append("<a class='arrow' href='").append(list_url)
         .append("?now_page=").append(next)
         .append("&word=").append(word)
         .append("'\">&#x203A;</a> "); // ›
    }

    // ✅ 끝: </div>
    str.append("</div>");

    return str.toString();
  }

  @Override
  public ArrayList<AiConsultVO> listRecent() {
      return aiConsultDAO.listRecent();
  }
  
  
  @Override
  public ArrayList<AiConsultVO> listByConsultnos(List<Integer> consultnos) {
      return aiConsultDAO.listByConsultnos(consultnos);
  }

  @Override
  public int count_all(Map<String, Object> map) {
      return this.aiConsultDAO.count_all(map);
  }



  @Override
  public ArrayList<AiConsultVO> list_all_paging(Map<String, Object> map) {
    return aiConsultDAO.list_all_paging(map);
  }

  
  @Override
  public int countByUsersno(int usersno) {
    return this.aiConsultDAO.countByUsersno(usersno);
  }

  @Override
  public List<AiConsultVO> listByUsersno(int usersno) {
    return this.aiConsultDAO.listByUsersno(usersno);
  }

  @Override
  public ArrayList<AiConsultVO> listByUsersnoAll(int usersno) {
    return aiConsultDAO.listByUsersnoAll(usersno);
  }
  
  @Override
  public int countByUsersnoFiltered(Map<String, Object> map) {
    return aiConsultDAO.countByUsersnoFiltered(map);
  }

  @Override
  public List<AiConsultVO> listByUsersnoPaging(Map<String, Object> map) {
    return aiConsultDAO.listByUsersnoPaging(map);
  }

}


