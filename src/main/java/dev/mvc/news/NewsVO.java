package dev.mvc.news;

public class NewsVO {
  private String title;
  private String link;
  private String description; // ✅ 요약 내용
  private String pubDate;     // ✅ 작성 날짜

  public NewsVO() {}

  public NewsVO(String title, String link, String description, String pubDate) {
    this.title = title;
    this.link = link;
    this.description = description;
    this.pubDate = pubDate;
  }

  // --- 기존 ---
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  // --- 추가된 부분 ---
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPubDate() {
    return pubDate;
  }

  public void setPubDate(String pubDate) {
    this.pubDate = pubDate;
  }
}
