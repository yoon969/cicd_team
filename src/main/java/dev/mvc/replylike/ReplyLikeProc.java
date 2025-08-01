package dev.mvc.replylike;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dev.mvc.replylike.ReplyLikeProc")
public class ReplyLikeProc implements ReplyLikeProcInter {

    @Autowired
    private ReplyLikeDAOInter replyLikeDAO;

    @Override
    public int checkLiked(Map<String, Object> map) {
      return replyLikeDAO.checkLiked(map);
    }

    @Override
    public int create(ReplyLikeVO vo) {
      return replyLikeDAO.create(vo);
    }

    @Override
    public int delete(Map<String, Object> map) {
      return replyLikeDAO.delete(map);
    }

    @Override
    public int countByReplyno(int replyno) {
      return replyLikeDAO.countByReplyno(replyno);
    }

    @Override
    public int deleteByReplyno(int replyno) {
      return this.replyLikeDAO.deleteByReplyno(replyno);
    }


    @Override
    public List<Map<String, Object>> listWithLike(Map<String, Object> map) {
      return this.replyLikeDAO.listWithLike(map);
    }

    @Override
    public int deleteByPostno(int postno) {
      return this.replyLikeDAO.deleteByPostno(postno);
    }


}
