package dev.mvc.reply;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service("dev.mvc.reply.ReplyProc")
public class ReplyProc implements ReplyProcInter {

  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;
  
  @Autowired
  private ReplyDAOInter replyDAO;

  @Override
  public int create(ReplyVO replyVO) {
    return replyDAO.create(replyVO);
  }

  @Override
  public List<ReplyVO> listByPostno(int postno) {
    return replyDAO.listByPostno(postno);
  }

  @Override
  public int update(ReplyVO replyVO) {
    return replyDAO.update(replyVO);
  }

  @Override
  public int delete(int replyno) {
    return replyDAO.delete(replyno);
  }

  @Override
  public int countByPostno(int postno) {
    return replyDAO.countByPostno(postno);
  }

  @Override
  public int deleteByPostno(int postno) {
    return replyDAO.deleteByPostno(postno);
  }

  @Override
  public int deleteByUsersno(int usersno) {
    return replyDAO.deleteByUsersno(usersno);
  }

  @Override
  public List<Map<String, Object>> listWithLike(Map<String, Object> map) {
    return replyDAO.listWithLike(map);
  }

  @Override
  public ReplyVO read(int replyno) {
    return this.replyDAO.read(replyno);
  }

  @Override
  public List<ReplyVO> listByUsersno(int usersno) {
    return this.replyDAO.listByUsersno(usersno);
  }

  @Override
  public int countByUsersno(int usersno) {
    return this.replyDAO.countByUsersno(usersno);
  }

  @Override
  public List<Integer> getReplynosByPostno(int postno) {
    return replyDAO.getReplynosByPostno(postno);
  }

  @Override
  public int countByUsersnoFiltered(Map<String, Object> map) {
    return replyDAO.countByUsersnoFiltered(map);
  }

  @Override
  public List<ReplyVO> listByUsersnoPaging(Map<String, Object> map) {
    return replyDAO.listByUsersnoPaging(map);
  }

  @Override
  public List<ReplyVO> listWithLikeVO(Map<String, Object> map) {
    return replyDAO.listWithLikeVO(map);
  }

  @Override
  public List<ReplyVO> listByPostnoFiltered(int postno, int usersno) {
    Map<String, Object> map = new HashMap<>();
    map.put("postno", postno);
    map.put("usersno", usersno);
    return this.replyDAO.listByPostnoFiltered(map);
  }
  
//@Override
//public int create(ReplyVO replyVO) {
//    int result = replyDAO.create(replyVO);
//
//    if (result > 0) {
//        int writerUsersno = postDAO.getWriter(replyVO.getPostno());
//
//        // ✅ 일반 댓글 알림 (게시글 주인에게)
//        if (writerUsersno != replyVO.getUsersno() && replyVO.getParentno() == 0) {
//            AlarmLogVO alarm = new AlarmLogVO();
//            alarm.setUsersno(writerUsersno);
//            alarm.setType("POST_REPLY");
//            alarm.setPostno(replyVO.getPostno());
//            alarm.setContent("내 게시글에 댓글이 달렸어요!");
//            alarm.setUrl("/post/read?postno=" + replyVO.getPostno());
//            alarmLogDAO.create(alarm);
//        }
//
//        // ✅ 대댓글 알림 (부모 댓글 작성자에게)
//        if (replyVO.getParentno() != 0) {
//            int parentWriter = replyDAO.getWriterByReplyno(replyVO.getParentno());
//            if (parentWriter != replyVO.getUsersno()) { // 본인 제외
//                AlarmLogVO replyAlarm = new AlarmLogVO();
//                replyAlarm.setUsersno(parentWriter);
//                replyAlarm.setType("REPLY_REPLY");
//                replyAlarm.setPostno(replyVO.getPostno());
//                replyAlarm.setContent("내 댓글에 답글이 달렸어요!");
//                replyAlarm.setUrl("/post/read?postno=" + replyVO.getPostno());
//                alarmLogDAO.create(replyAlarm);
//            }
//        }
//    }
//
//    return result;
//}
//
@Override
public int getWriterByReplyno(int replyno) {
    String sql = "SELECT usersno FROM reply WHERE replyno = :replyno";
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("replyno", replyno);

    return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);
}

}
