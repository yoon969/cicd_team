package dev.mvc.feedback;

import java.util.List;

public interface FeedbackProcInter {
  public int create(FeedbackVO vo);
  public List<FeedbackVO> list();
  public int delete(int feedbackno);
}
