package dev.mvc.sms;

import com.google.gson.Gson;
import dev.mvc.tool.Tool;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;

public class SMS {
  private static final String SMS_SEND_URL = "https://sms.gabia.com/api/send/sms";
  private static final String SMS_ID = "dbsrn1224sms";
  private static final String CALLBACK = "01039262649";

  /**
   * SMS 인증번호 전송
   * @param phone 수신자 번호 (예: 01012345678)
   * @param authCode 인증번호 (예: 123456)
   * @return Gabia API의 응답 JSON 문자열
   * @throws IOException
   */
  public static String sendAuthCode(String phone, String authCode) throws IOException {
    String accessToken = Tool.getSMSToken();
    String authValue = 
        Base64.getEncoder().encodeToString(String.format("%s:%s", SMS_ID, 
            accessToken).getBytes(StandardCharsets.UTF_8)
    );

    OkHttpClient client = new OkHttpClient();

    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
      .addFormDataPart("phone", phone)
      .addFormDataPart("callback", CALLBACK)
      .addFormDataPart("message", "[숨숨이들] 인증번호 [" + authCode + "] 를 입력해주세요.")
      .addFormDataPart("refkey", "1")
      .build();

    Request request = new Request.Builder()
      .url(SMS_SEND_URL)
      .post(requestBody)
      .addHeader("Content-Type", "application/x-www-form-urlencoded")
      .addHeader("Authorization", "Basic " + authValue)
      .addHeader("cache-control", "no-cache")
      .build();

    Response response = client.newCall(request).execute();
    
    
    return Objects.requireNonNull(response.body()).string();
  }
  
  /**
   * 오늘 일정 문자 발송
   * @param phone 수신자 번호 (01012345678)
   * @param scheduleTitle 일정 제목
   * @return Gabia API 응답
   * @throws IOException
   */
  public static String sendScheduleNotice(String phone, String scheduleTitle) throws IOException {
    String accessToken = Tool.getSMSToken();
    String authValue = Base64.getEncoder()
        .encodeToString(String.format("%s:%s", SMS_ID, accessToken).getBytes(StandardCharsets.UTF_8));

    OkHttpClient client = new OkHttpClient();

    String message = "[숨숨이들] 오늘 일정: [" + scheduleTitle + "] 꼭 잊지 마세요!";

    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
      .addFormDataPart("phone", phone)
      .addFormDataPart("callback", CALLBACK)
      .addFormDataPart("message", message)
      .addFormDataPart("refkey", "2")
      .build();

    Request request = new Request.Builder()
      .url(SMS_SEND_URL)
      .post(requestBody)
//      .addHeader("Content-Type", "application/x-www-form-urlencoded")
      .addHeader("Authorization", "Basic " + authValue)
      .addHeader("cache-control", "no-cache")
      .build();

    Response response = client.newCall(request).execute();
    return Objects.requireNonNull(response.body()).string();
  }

}
