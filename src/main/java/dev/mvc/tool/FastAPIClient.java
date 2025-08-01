package dev.mvc.tool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class FastAPIClient {
  public static Map<String, String> getAnimalSummary(String name, String description) {
    Map<String, String> result = new HashMap<>();
    try {
      // 요청 본문 구성
      JSONObject body = new JSONObject();
      
      LLMKey llmKey = new LLMKey();
      String apiKey = llmKey.getSpringBoot_FastAPI_KEY();
      body.put("SpringBoot_FastAPI_KEY", apiKey);
      
      body.put("name", name);
      body.put("description", description);

      URL url = new URL("http://localhost:8000/animal/summary"); // 실제 주소
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json; utf-8");
      conn.setDoOutput(true);

      try (OutputStream os = conn.getOutputStream()) {
        byte[] input = body.toString().getBytes("utf-8");
        os.write(input, 0, input.length);
      }

      StringBuilder response = new StringBuilder();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
          response.append(responseLine.trim());
        }
      }

      JSONObject json = new JSONObject(response.toString());
      result.put("summary", json.optString("summary", "요약 없음"));
      result.put("recommendation", json.optString("recommendation", "추천 이유 없음"));

    } catch (Exception e) {
      e.printStackTrace();
      result.put("summary", "요약 실패");
      result.put("recommendation", "추천 실패");
    }

    return result;
  }

  public static String pickRandomAnimalName() {
    try {
      URL url = new URL("http://localhost:8000/animal/random_pick");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");

      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine, response = "";
      while ((inputLine = in.readLine()) != null) {
        response += inputLine;
      }
      in.close();

      JSONObject json = new JSONObject(response);
      return json.getString("name");

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public static void summarizeNews(String title, String content) {
    try {
      JSONObject body = new JSONObject();
      
      LLMKey llmKey = new LLMKey();
      String apiKey = llmKey.getSpringBoot_FastAPI_KEY();
      body.put("SpringBoot_FastAPI_KEY", apiKey);
      
      body.put("title", title);
      body.put("content", content);

      URL url = new URL("http://localhost:8000/news/summary");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json; utf-8");
      conn.setDoOutput(true);

      try (OutputStream os = conn.getOutputStream()) {
        os.write(body.toString().getBytes("utf-8"));
      }

      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = in.readLine()) != null) {
        response.append(line.trim());
      }
      in.close();

      System.out.println("✅ FastAPI 응답: " + response.toString());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
