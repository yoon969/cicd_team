package dev.mvc.news;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("dev.mvc.news.NewsProc")
public class NewsProc implements NewsProcInter {

    @Autowired
    private SqlSession sqlSession;

    @Override
    public List<NewsVO> list() {
        NewsDAOInter newsDAO = sqlSession.getMapper(NewsDAOInter.class);
        return newsDAO.list(); // 여기서 실제 쿼리 실행
    }
}
