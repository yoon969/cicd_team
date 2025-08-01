package dev.mvc.reply;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ReplyDAOInter {

  /** 댓글 등록 (일반 + 대댓글 공통) */
  public int create(ReplyVO replyVO);

  /** 게시글의 댓글 목록 */
  public List<ReplyVO> listByPostno(int postno);

  /** 댓글 수정 */
  public int update(ReplyVO replyVO);

  /** 댓글 삭제 */
  public int delete(int replyno);

  /** 게시글 댓글 개수 */
  public int countByPostno(int postno);

  /** 게시글 삭제 시 댓글 일괄 삭제 */
  public int deleteByPostno(int postno);

  /** 회원 탈퇴 시 댓글 일괄 삭제 */
  public int deleteByUsersno(int usersno);

  /** 좋아요 포함 댓글 목록 (Map 버전) */
  public List<Map<String, Object>> listWithLike(Map<String, Object> map);

  /** 댓글 1개 조회 */
  public ReplyVO read(int replyno);

  /** 내가 쓴 최근 댓글 3개 */
  public List<ReplyVO> listByUsersno(int usersno);

  /** 내가 쓴 댓글 총 개수 */
  public int countByUsersno(int usersno);

  /** 특정 게시글의 댓글 번호 리스트 */
  public List<Integer> getReplynosByPostno(int postno);

  /** 내가 쓴 댓글 (검색 필터 포함) */
  public ArrayList<ReplyVO> listByUsersnoFiltered(Map<String, Object> map);

  /** 내가 쓴 댓글 필터 결과 개수 */
  public int countByUsersnoFiltered(Map<String, Object> map);

  /** 내가 쓴 댓글 + 페이징 */
  public List<ReplyVO> listByUsersnoPaging(Map<String, Object> map);

  /** 좋아요 포함 댓글 목록 (VO 반환 버전) */
  public List<ReplyVO> listWithLikeVO(Map<String, Object> map);

  /** 특정 게시글의 댓글 목록 (신고 필터 없음) */
  public List<ReplyVO> listByPostnoFiltered(Map<String, Object> map);
  
  public List<ReplyVO> listByPostnoWithLiked(Map<String, Object> map);

  /** 특정 댓글 번호(replyno)로 작성자 번호(usersno) 조회 */
  public int getWriterByReplyno(int replyno);

}
