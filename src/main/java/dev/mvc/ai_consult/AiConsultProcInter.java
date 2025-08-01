package dev.mvc.ai_consult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface AiConsultProcInter {
  public int create(AiConsultVO aiConsultVO);
  public ArrayList<AiConsultVO> list_all();
  public AiConsultVO read(int consultno);
  public int update(AiConsultVO aiConsultVO);
  public int delete(int consultno);
  public ArrayList<AiConsultVO> list_by_usersno(int usersno);
  boolean existsByQuestionAnswer(int usersno, String question, String answer);
  public ArrayList<AiConsultVO> list_by_usersno_paging(Map<String, Object> map);
  public int count_by_usersno(Map<String, Object> map);
  String pagingBox(int usersno, int total, int now_page, String word, String list_url, int pagePerBlock);
  public ArrayList<AiConsultVO> listRecent();
  public ArrayList<AiConsultVO> listByConsultnos(List<Integer> consultnos);

  public int count_all(Map<String, Object> map);
  public ArrayList<AiConsultVO> list_all_paging(Map<String, Object> map);

  public int countByUsersno(int usersno);

  public List<AiConsultVO> listByUsersno(int usersno);

  /** 내가 쓴 상담 기록 전체 */
  public ArrayList<AiConsultVO> listByUsersnoAll(int usersno);
  
  public int countByUsersnoFiltered(Map<String, Object> map);
  public List<AiConsultVO> listByUsersnoPaging(Map<String, Object> map);

}

