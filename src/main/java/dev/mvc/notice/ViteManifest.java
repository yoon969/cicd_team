package dev.mvc.notice;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("vite")
public class ViteManifest {
  private final Map<String, Map<String, Object>> manifest;

  public ViteManifest() throws IOException {
    ClassPathResource resource = new ClassPathResource("static/notice/assets/manifest.json");

    try (InputStream is = resource.getInputStream()) {
      byte[] bytes = is.readAllBytes();
      ObjectMapper mapper = new ObjectMapper();
      this.manifest = mapper.readValue(bytes, new TypeReference<>() {});
    }
  }

  public String js(String entry) {
    Object file = manifest.get(entry).get("file");
    return file != null ? "/notice/" + file.toString() : null;
  }

  public String css(String entry) {
    Object cssObj = manifest.get(entry).get("css");
    if (cssObj instanceof List<?> cssList && !cssList.isEmpty()) {
      return "/notice/" + cssList.get(0).toString(); // 첫 번째 CSS만
    }
    return null;
  }

  public static class Entry {
    public String file;
    public List<String> css;
  }
}
