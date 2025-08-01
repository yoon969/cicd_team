package dev.mvc.recommendation;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("dev.mvc.recommendation.RecommendationProc")
public class RecommendationProc implements RecommendationProcInter {

  @Autowired
  private RecommendationDAOInter recommendationDAO;

  @Override
  public int create(RecommendationVO vo) {
    return recommendationDAO.create(vo);
  }
}
