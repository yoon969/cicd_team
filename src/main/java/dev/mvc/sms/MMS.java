package dev.mvc.sms;

import com.google.gson.Gson;

import dev.mvc.tool.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// 외부 라이브러리 다운로드가 필요합니다. (gradle 기준)
// implementation 'com.squareup.okhttp3:okhttp:4.9.3'
// https://mvnrepository.com/artifact/com.squareup.okhttp/okhttp
// implementation 'com.google.code.gson:gson:2.9.0' https://github.com/google/gson

public class MMS {
  public static final String MMS_SEND_URL = "https://sms.gabia.com/api/send/mms";
  private static final String SMS_ID = "dbsrn1224sms";
  private static final String CALLBACK = "01039262649";

  public static String sendImage(String tel, String imagePath, String msg) throws IOException {
    String accessToken = Tool.getSMSToken();
    String authValue = Base64.getEncoder()
        .encodeToString(String.format("%s:%s", SMS_ID, accessToken).getBytes(StandardCharsets.UTF_8));

    OkHttpClient client = new OkHttpClient();

    File imageFile = new File(imagePath);
    if (!imageFile.exists()) {
      return "이미지 파일이 존재하지 않습니다.";
    }

    // ✅ 확장자 기반 MIME 타입 추론
    String lowerName = imageFile.getName().toLowerCase();
    String mimeType = null;
    if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
      mimeType = "image/jpeg";
    } else if (lowerName.endsWith(".png")) {
      mimeType = "image/png";
    } else {
      return "지원되지 않는 이미지 형식입니다. JPG 또는 PNG만 허용됩니다.";
    }

    RequestBody imageBody = RequestBody.create(imageFile, okhttp3.MediaType.parse(mimeType));

    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("phone", tel.replace("-", ""))
        .addFormDataPart("callback", CALLBACK)
        .addFormDataPart("message", msg)
        .addFormDataPart("refkey", "memory_" + System.currentTimeMillis())
        .addFormDataPart("subject", "숨숨이 일러스트 도착!")
        .addFormDataPart("image_cnt", "1")
        .addFormDataPart("images0", imageFile.getName(), imageBody)
        .build();

    Request request = new Request.Builder()
        .url(MMS_SEND_URL)
        .post(requestBody)
        .addHeader("Authorization", "Basic " + authValue)
        .build();

    Response response = client.newCall(request).execute();

    // ✅ JSON 응답 출력
    HashMap<String, String> result = new Gson()
        .fromJson(Objects.requireNonNull(response.body()).string(), HashMap.class);

    for (String key : result.keySet()) {
      System.out.printf("%s: %s%n", key, result.get(key));
    }

    return "전송 완료";
  }
}