package dev.mvc.notice;

import java.io.FileReader;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NoticePageCont {

  @GetMapping("/notice/page")
  public String reactNoticePage(Model model) {
      try {
          // 1. manifest.json 경로 지정
          String manifestPath = "src/main/resources/static/notice/assets/manifest.json";

          // 2. JSON 파싱
          ObjectMapper mapper = new ObjectMapper();
          Map<?, ?> manifest = mapper.readValue(new FileReader(manifestPath), Map.class);

          // 3. "index.html" 기준으로 실제 JS/CSS 경로 추출
          Map<?, ?> entry = (Map<?, ?>) manifest.get("index.html");
          String jsPath = (String) entry.get("file");

          // ✅ CSS 경로는 배열이므로 첫 번째 값만 추출
          String cssPath = "";
          if (entry.get("css") instanceof java.util.List<?> cssList && !cssList.isEmpty()) {
              cssPath = (String) cssList.get(0);
          }

          // 4. Thymeleaf에 전달
          model.addAttribute("reactJsPath", "/notice/" + jsPath);
          model.addAttribute("reactCssPath", "/notice/" + cssPath);

      } catch (Exception e) {
          e.printStackTrace();
          model.addAttribute("reactJsPath", "");
          model.addAttribute("reactCssPath", "");
      }

      return "notice/page"; // 🔸 타임리프 템플릿 경로
  }
  

}
