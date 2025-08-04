package dev.mvc.feedback;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("viteFeedback")
public class ViteManifest {
  private final Map<String, Map<String, Object>> manifest;

  public ViteManifest() throws IOException {
    // ✅ classpath 경로를 통해 WAR 내부에서도 접근 가능
    ClassPathResource resource = new ClassPathResource("static/feedback/assets/manifest.json");

    try (InputStream is = resource.getInputStream()) {
      byte[] bytes = is.readAllBytes();
      ObjectMapper mapper = new ObjectMapper();
      this.manifest = mapper.readValue(bytes, new TypeReference<>() {});
    }
  }

  public String js(String entry) {
    Object file = manifest.get(entry).get("file");
    return file != null ? "/feedback/" + file.toString() : null;
  }

  public String css(String entry) {
    Object cssObj = manifest.get(entry).get("css");
    if (cssObj instanceof List<?> cssList && !cssList.isEmpty()) {
      return "/feedback/" + cssList.get(0).toString();
    }
    return null;
  }

  // 내부 클래스 (사용하지 않는다면 삭제 가능)
  public static class Entry {
    public String file;
    public List<String> css;
  }
}
