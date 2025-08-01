package dev.mvc.replylike;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReplyLikeDAOInter {
  public int countByReplyno(int replyno);

  public int checkLiked(Map<String, Object> map);

  public int create(ReplyLikeVO replyLikeVO);

  public int deleteByReplyno(int replyno);
  
  public int delete(Map<String, Object> map);
  
  public List<Map<String, Object>> listWithLike(Map<String, Object> map);

  public int deleteByPostno(int postno);

}

// ReplyLikeProcInter.java
