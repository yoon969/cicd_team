package dev.mvc.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.mvc.tool.Security;

@Component("dev.mvc.users.UsersProc")
public class UsersProc implements UsersProcInter {
  @Autowired
  private UsersDAOInter usersDAO;

  @Autowired
  private Security security;

  public UsersProc() {
    // System.out.println("-> UsersProc created.");
  }

  @Override
  public int checkID(String email) {
    int cnt = this.usersDAO.checkID(email);
    return cnt;
  }

  @Override
  public int create(UsersVO usersVO) {
    String passwd = usersVO.getPasswd();

    if (!"social_login".equals(passwd)) {
      String passwd_encoded = this.security.aesEncode(passwd);
      usersVO.setPasswd(passwd_encoded); // 패스워드 저장
    }

    int cnt = this.usersDAO.create(usersVO); // 가입
    return cnt;
  }

  @Override
  public ArrayList<UsersVO> list() {
    ArrayList<UsersVO> list = this.usersDAO.list();
    return list;
  }

  @Override
  public UsersVO read(int usersno) {
    UsersVO usersVO = this.usersDAO.read(usersno);
    return usersVO;
  }

  @Override
  public UsersVO readByEmail(String email) {
    System.out.println("📥 readByEmail() 호출됨, 파라미터 email: " + email);

    UsersVO usersVO = null;

    try {
      usersVO = this.usersDAO.readByEmail(email);
      System.out.println("📤 DAO로부터 받은 결과: " + usersVO);
    } catch (Exception e) {
      System.out.println("❌ 예외 발생 in readByEmail()");
      e.printStackTrace();
    }

    return usersVO;
  }

  @Override
  public boolean isUsers(HttpSession session) {
    boolean sw = false; // 로그인하지 않은 것으로 초기화

    if (session.getAttribute("role") != null) {
      if (((String) session.getAttribute("role")).equals("admin")
          || ((String) session.getAttribute("role")).equals("user")) {
        sw = true;
      }
    }

    return sw;
  }

  @Override
  public boolean isAdmin(HttpSession session) {
    boolean sw = false; // 로그인하지 않은 것으로 초기화

    if (session.getAttribute("role") != null) {
      if (((String) session.getAttribute("role")).equals("admin")) {
        sw = true;
      }
    }

    return sw;
  }

  @Override
  public int update(UsersVO usersVO) {
    int cnt = this.usersDAO.update(usersVO);
    return cnt;
  }

  @Override
  public int delete(int usersno) {
    int cnt = this.usersDAO.delete(usersno);
    return cnt;
  }

  @Override
  public int passwd_check(HashMap<String, Object> map) {
    String passwd = (String) map.get("passwd");
    
    String enc = this.security.aesEncode(passwd);
    System.out.println("🔐 입력된 비밀번호: " + passwd);
    System.out.println("🔒 암호화된 비밀번호: " + enc);
    
    // map.put("passwd", new Security().aesEncode(passwd));
    map.put("passwd", this.security.aesEncode(passwd));
    int cnt = this.usersDAO.passwd_check(map);
    return cnt;
  }

  @Override
  public int passwd_update(HashMap<String, Object> map) {
    String passwd = (String) map.get("passwd");
    // map.put("passwd", new Security().aesEncode(passwd));
    map.put("passwd", this.security.aesEncode(passwd));
    int cnt = this.usersDAO.passwd_update(map);
    return cnt;
  }

  @Override
  public int login(Map<String, Object> map) {
    String passwd = (String) map.get("passwd");
    // map.put("passwd", new Security().aesEncode(passwd));
    map.put("passwd", this.security.aesEncode(passwd));
    int cnt = this.usersDAO.login(map);

    return cnt;
  }

  @Override
  public int checkName(String usersname) {
    return usersDAO.checkName(usersname);
  }

  /**
   * 이메일 + 전화번호로 사용자 조회
   * 
   * @param map - email, tel 포함
   * @return UsersVO - 해당 회원 정보
   */
  @Override
  public UsersVO findByEmailTel(Map<String, Object> map) {
    return this.usersDAO.findByEmailTel(map);
  }

  /**
   * 비밀번호 재설정
   * 
   * @param usersVO - 변경할 비밀번호와 usersno 포함
   * @return 변경된 행 수
   */
  @Override
  public int updatePasswd(UsersVO usersVO) {
    String encodedPasswd = this.security.aesEncode(usersVO.getPasswd());
    usersVO.setPasswd(encodedPasswd);

    return this.usersDAO.updatePasswd(usersVO);
  }

  @Override
  public int count() {
    return usersDAO.count();
  }

  @Override
  public ArrayList<UsersVO> listPaging(Map<String, Integer> params) {
    return usersDAO.listPaging(params);
  }

  /** 
   * 검색어 기준 전체 레코드 수 반환 
   */
  @Override
  public int list_search_count(String word) {
    return this.usersDAO.list_search_count(word);
  }

  /**
   * 검색 + 페이징 목록
   * select id="list_search_paging" resultType="dev.mvc.users.UsersVO" parameterType="Map" 
   */
  @Override
  public ArrayList<UsersVO> list_search_paging(String word, int now_page, int record_per_page) {
    int start_num = ((now_page - 1) * record_per_page) + 1;
    int end_num = start_num + record_per_page - 1;

    Map<String, Object> map = new HashMap<>();
    map.put("word", word);
    map.put("start_num", start_num);
    map.put("end_num", end_num);

    return this.usersDAO.list_search_paging(map);
  }

  /**
   * SPAN 태그 기반 페이징 박스 모델
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
    str.append("<div class='paging'>"); // ✅ 시작 div

 // ◀ 이전 화살표
 if (now_page > 1) {
   int prev_page = now_page - 1;
   str.append("<span class='arrow' onclick=\"location.href='")
      .append(list_url).append("?word=").append(word)
      .append("&now_page=").append(prev_page)
      .append("'\">&#x2039;</span> "); // ‹
 }

 // ✅ 페이지 번호들
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
      .append("'\">&#x203A;</span> "); // ›
 }

 str.append("</div>"); // ✅ 끝 div
    return str.toString();
  }
  
  /**
   * 전화번호로 정보 조회
   * @param tel
   * @return
   */
  @Override
  public UsersVO findByTel(String tel) {
    return this.usersDAO.findByTel(tel);
  }
  
  @Override
  public int withdraw(int usersno) {
      return usersDAO.withdraw(usersno);
  }

  @Override
  public UsersVO readByUsersno(Integer usersno) {
      return usersDAO.readByUsersno(usersno);
  }

  @Override
  public UsersVO findByEmail(String email) {
      return usersDAO.findByEmail(email);
  }
  
  @Override
  public String getPhone(int usersno) {
    return usersDAO.getPhone(usersno);
  }

}
