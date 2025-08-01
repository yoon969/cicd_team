package dev.mvc.calendar;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.JSONObject;

import dev.mvc.tool.LLMKey;

public class CalendarRest {

  public static Map<String, Object> summaryContent(String content) {
    Map<String, Object> resultMap = new HashMap<>();

    try {
      URL url = new URL("http://localhost:8000/calendar/summary");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json; utf-8");
      conn.setDoOutput(true);

      // JSON 요청 구성
      String jsonInput = String.format(
        "{\"SpringBoot_FastAPI_KEY\": \"%s\", \"content\": \"%s\"}",
        new LLMKey().getSpringBoot_FastAPI_KEY(),
        content.replace("\"", "\\\"")
      );

      try (OutputStream os = conn.getOutputStream()) {
        byte[] input = jsonInput.getBytes("utf-8");
        os.write(input, 0, input.length);
      }

      // 응답 수신
      StringBuilder response = new StringBuilder();
      try (BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), "utf-8"))) {
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
          response.append(responseLine.trim());
        }
      }

      // JSON 파싱
      JSONObject json = new JSONObject(response.toString());
      resultMap.put("summary", json.getString("summary"));
      resultMap.put("emotion", json.getInt("emotion"));

    } catch (Exception e) {
      e.printStackTrace();
    }

    return resultMap;
  }

}
