package dev.mvc.sms_log;

import java.time.LocalDateTime;

public class SmsLogVO {
  private int logId;
  private String recipient;   // ✅ recipient ← phone ❌
  private String message;
  private LocalDateTime sendTime; // ✅ sendTime ← senddate ❌
  private String status;
  private String responseMsg; // ✅ responseMsg ← reason ❌
  private int retryCount;

  // Getters & Setters
  public int getLogId() {
    return logId;
  }

  public void setLogId(int logId) {
    this.logId = logId;
  }

  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public LocalDateTime getSendTime() {
    return sendTime;
  }

  public void setSendTime(LocalDateTime sendTime) {
    this.sendTime = sendTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getResponseMsg() {
    return responseMsg;
  }

  public void setResponseMsg(String responseMsg) {
    this.responseMsg = responseMsg;
  }

  public int getRetryCount() {
    return retryCount;
  }

  public void setRetryCount(int retryCount) {
    this.retryCount = retryCount;
  }
}
