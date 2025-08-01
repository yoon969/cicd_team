package dev.mvc.memory;

public class Paging {
  public static String generate(int total, int now_page, String url, String keyword, String sort) {
    int record_per_page = 6;
    int total_page = (int) Math.ceil((double) total / record_per_page);

    StringBuilder sb = new StringBuilder();

    // ◀ 이전 페이지 화살표
    if (now_page > 1) {
      int prev_page = now_page - 1;
      sb.append("<span class='arrow' onclick=\"location.href='")
        .append(url).append("?now_page=").append(prev_page)
        .append("&keyword=").append(keyword)
        .append("&sort=").append(sort)
        .append("'\">&#x2039;</span> "); // ‹
    }

    // 숫자 버튼
    for (int i = 1; i <= total_page; i++) {
      if (i == now_page) {
        sb.append("<a class='btn-page current'>").append(i).append("</a> ");
      } else {
        sb.append("<a class='btn-page' href='")
          .append(url).append("?now_page=").append(i)
          .append("&keyword=").append(keyword)
          .append("&sort=").append(sort)
          .append("'>").append(i).append("</a> ");
      }
    }

    // ▶ 다음 페이지 화살표
    if (now_page < total_page) {
      int next_page = now_page + 1;
      sb.append("<span class='arrow' onclick=\"location.href='")
        .append(url).append("?now_page=").append(next_page)
        .append("&keyword=").append(keyword)
        .append("&sort=").append(sort)
        .append("'\">&#x203A;</span> "); // ›
    }

    return sb.toString();
  }
}
