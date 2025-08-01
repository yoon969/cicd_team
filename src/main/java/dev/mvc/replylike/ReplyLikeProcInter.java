package dev.mvc.replylike;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReplyLikeProcInter {
  public int checkLiked(Map<String, Object> map);     // 좋아요 눌렀는지
  public int create(ReplyLikeVO vo);                  // 좋아요 등록
  public int delete(Map<String, Object> map);         // 좋아요 삭제
  public int countByReplyno(int replyno);             // 좋아요 수
  public int deleteByReplyno(int replyno);
  public List<Map<String, Object>> listWithLike(Map<String, Object> map);

  public int deleteByPostno(int postno);

}




