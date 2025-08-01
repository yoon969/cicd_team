package dev.mvc.feedback;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FeedbackPageCont {
  
  @GetMapping("/feedback/page")
  public String feedbackPage(Model model) {
      // Vite 빌드 결과물 경로 전달
      model.addAttribute("reactJsPath", "/feedback/assets/index.js");
      model.addAttribute("reactCssPath", "/feedback/assets/index.css");
      return "feedback/page";  // templates/feedback/page.html 로 이동
  }

}
