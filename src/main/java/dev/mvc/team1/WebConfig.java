package dev.mvc.team1;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dev.mvc.post.Post;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  // ✅ React 요청 허용
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**") // 모든 API 경로에 대해
        .allowedOrigins("http://localhost:5173") // React 서버 주소
        .allowedMethods("GET", "POST", "PUT", "DELETE").allowedHeaders("*").allowCredentials(true); // 쿠키 포함 허용
  }

  // ✅ 정적 리소스 설정
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/memory/storage/**")
        .addResourceLocations("file:///C:/kd/deploy/team1/memory/storage/");

    registry.addResourceHandler("/memory/upload/**").addResourceLocations("file:///C:/kd/deploy/team1/memory/upload/");

    // pet 이미지 불러오기 위한 경로
    registry.addResourceHandler("/pet/storage/**").addResourceLocations("file:///C:/kd/deploy/team1/pet/storage/");

    registry.addResourceHandler("/contents/storage/**").addResourceLocations("file:///" + Post.getUploadDir());

    registry.addResourceHandler("/news/**").addResourceLocations("classpath:/static/news/");
  }
}
