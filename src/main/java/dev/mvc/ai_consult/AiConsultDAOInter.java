package dev.mvc.ai_consult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiConsultDAOInter {
	/**
	 * 상담 등록
	 * 
	 * @param aiConsultVO
	 * @return 등록된 행 수
	 */
	public int create(AiConsultVO aiConsultVO);

	/**
	 * 전체 목록 조회
	 * 
	 * @return 상담 목록
	 */
	public ArrayList<AiConsultVO> list_all();

	/**
	 * 특정 상담 조회
	 * 
	 * @param consultno
	 * @return 상담 정보
	 */
	public AiConsultVO read(int consultno);

	/**
	 * 상담 내용 수정
	 * 
	 * @param aiConsultVO
	 * @return 수정된 행 수
	 */
	public int update(AiConsultVO aiConsultVO);

	/**
	 * 상담 삭제
	 * 
	 * @param consultno
	 * @return 삭제된 행 수
	 */
	public int delete(int consultno);

	/**
	 * 특정 사용자(userno)의 상담 목록 조회
	 * 
	 * @param usersno
	 * @return 상담 목록
	 */
	public ArrayList<AiConsultVO> list_by_usersno(int usersno);

	int countByQuestionAnswer(Map<String, Object> map);

	public ArrayList<AiConsultVO> list_by_usersno_paging(Map<String, Object> map);

	public int count_by_usersno(Map<String, Object> map);

	public ArrayList<AiConsultVO> listRecent();

	public ArrayList<AiConsultVO> listByConsultnos(List<Integer> consultnos);

//전체 상담 수 (검색 포함)
	public int count_all(Map<String, Object> map);

//전체 상담 목록 (페이징 포함)
	public ArrayList<AiConsultVO> list_all_paging(Map<String, Object> map);

	public int countByUsersno(int usersno);

	public List<AiConsultVO> listByUsersno(int usersno);

	/** 내가 쓴 상담 기록 전체 */
	  public ArrayList<AiConsultVO> listByUsersnoAll(int usersno);
	  
	  public int countByUsersnoFiltered(Map<String, Object> map);
	  public List<AiConsultVO> listByUsersnoPaging(Map<String, Object> map);

}
