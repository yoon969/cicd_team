package dev.mvc.feedback;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("dev.mvc.feedback.FeedbackProc")
public class FeedbackProc implements FeedbackProcInter {

  @Autowired
  private FeedbackDAOInter feedbackDAO;

  @Override
  public int create(FeedbackVO vo) {
    return feedbackDAO.create(vo);
  }

  @Override
  public List<FeedbackVO> list() {
    return feedbackDAO.list();
  }

  @Override
  public int delete(int feedbackno) {
    return feedbackDAO.delete(feedbackno);
  }
}
