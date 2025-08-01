package dev.mvc.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoticeVO {
  private int notice_id;
  private String title;
  private String content;
  private String rdate;
}