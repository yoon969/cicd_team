package dev.mvc.alarm_log;

import java.util.Date;

public class AlarmLogVO {
  private int alarmno;
  private int usersno;
  private String content;
  private String url;
  private String checked;
  private Date rdate;
//AlarmLogVO.java
  private String type; // 알림 타입 (예: "POST_LIKE")
  private Integer postno; // 게시글 번호
  private String msg; // 알림 메시지

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getPostno() {
    return postno;
  }

  public void setPostno(Integer postno) {
    this.postno = postno;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  // 🔽 자동 생성 (직접 타이핑하거나 IDE 사용)

  public int getAlarmno() {
    return alarmno;
  }

  public void setAlarmno(int alarmno) {
    this.alarmno = alarmno;
  }

  public int getUsersno() {
    return usersno;
  }

  public void setUsersno(int usersno) {
    this.usersno = usersno;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getChecked() {
    return checked;
  }

  public void setChecked(String checked) {
    this.checked = checked;
  }

  public Date getRdate() {
    return rdate;
  }

  public void setRdate(Date rdate) {
    this.rdate = rdate;
  }
}
