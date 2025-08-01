package dev.mvc.users;

import java.util.ArrayList;
import java.util.HashMap;  // class
import java.util.List;
// interface, 인터페이스를 사용하는 이유는 다른 형태의 구현 클래스로 변경시 소스 변경이 거의 발생 안됨
// 예) 2022년 세금 계산 방법 구현 class, 2023년 세금 계산 방법 구현 class
// 인터페이스 = 구현 클래스
// Payend pay = new Payend2022();
// Payend pay = new Payend2023();
// Payend pay = new Payend2024();
// pay.calc();
import java.util.Map;

import org.apache.ibatis.annotations.Param;         

public interface UsersDAOInter {
  /**
   * 중복 아이디 검사
   * @param email
   * @return 중복 아이디 갯수
   */
  public int checkID(String email);
  
  /**
   * 회원 가입
   * @param usersVO
   * @return 추가한 레코드 갯수
   */
  public int create(UsersVO usersVO);

  /**
   * 회원 전체 목록
   * @return
   */
  public ArrayList<UsersVO> list();

  /**
   * usersno로 회원 정보 조회
   * @param usersno
   * @return
   */
  public UsersVO read(int usersno);
  
  /**
   * id로 회원 정보 조회
   * @param id
   * @return
   */
  public UsersVO readByEmail(String email);

  /**
   * 수정 처리
   * @param usersVO
   * @return
   */
  public int update(UsersVO usersVO);
 
  /**
   * 회원 삭제 처리
   * @param usersno
   * @return
   */
  public int delete(int usersno);
  
  /**
   * 현재 패스워드 검사
   * @param map
   * @return 0: 일치하지 않음, 1: 일치함
   */
  public int passwd_check(HashMap<String, Object> map);
  
  /**
   * 패스워드 변경
   * @param map
   * @return 변경된 패스워드 갯수
   */
  public int passwd_update(HashMap<String, Object> map);
  
  /**
   * 로그인 처리
   */
  public int login(Map<String, Object> map);
  
  /**
   * 닉네임 중복 체크
   * @param usersname 확인할 닉네임
   * @return 같은 이름을 가진 사용자의 수
   */
  public int checkName(String usersname);
  
  /**
   * 이메일 + 전화번호로 사용자 조회 (비밀번호 찾기 단계 1)
   * @param map - email, tel 포함
   * @return UsersVO - 해당 회원 정보
   */
  public UsersVO findByEmailTel(Map<String, Object> map);

  /**
   * 비밀번호 재설정
   * @param usersVO - 변경할 비밀번호와 usersno 포함
   * @return 변경된 행 수
   */
  public int updatePasswd(UsersVO usersVO);
  
  public int count();

  public ArrayList<UsersVO> listPaging(Map<String, Integer> params);
  
  /**
   * 검색, 전체 레코드 갯수, 페이징 버튼 생성시 필요 ★★★★★
   * @param word 검색어
   * @return 검색된 레코드 수
   */
  public int list_search_count(String word);

  /**
   * 검색, 전체 목록
   * @param map word, start_num, end_num 포함
   * @return 검색된 사용자 목록 (페이징 포함)
   */
  public ArrayList<UsersVO> list_search_paging(Map<String, Object> map);
  
  /**
   * 전화번호로 정보 조회
   * @param tel
   * @return
   */
  public UsersVO findByTel(String tel);

  /**
   * 탈퇴 처리
   * @param usersno
   * @return
   */
  public int withdraw(int usersno);

  public UsersVO readByUsersno(Integer usersno);
  
  public UsersVO findByEmail(String email);

  public String getPhone(int usersno);
}