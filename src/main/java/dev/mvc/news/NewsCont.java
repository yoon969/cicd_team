package dev.mvc.news;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import dev.mvc.users.UsersProcInter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

@Controller
public class NewsCont {

  @Autowired
  @Qualifier("dev.mvc.news.NewsProc")
  private NewsProcInter newsProc;

  @ResponseBody
  @GetMapping("/api/news/list") // ✅ API 경로는 /api/news/list로 하는 게 더 명확
  public List<NewsVO> list() {
    return newsProc.list();
  }

  @RequestMapping({ "/news", "/news/**" }) // ✅ React SPA 대응
  public void forwardNews(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.getRequestDispatcher("/news/index.html").forward(request, response);
  }
}
