package dev.mvc.tool;

public class ToolBox {

    /**
     * 검색어 하이라이트 처리
     * @param str 원본 문자열
     * @param keyword 검색어
     * @return 하이라이트 처리된 문자열
     */
    public static String highlight(String str, String keyword) {
        if (str == null || keyword == null || keyword.trim().isEmpty()) {
            return str;
        }

        // HTML 이스케이프 제거 및 하이라이팅 처리
        return str.replaceAll("(?i)" + keyword, "<span style='background-color:yellow;'>" + keyword + "</span>");
    }

    /**
     * 페이징 박스 생성
     * @param nowPage 현재 페이지
     * @param totalRecord 전체 레코드 수
     * @param url 호출 URL (/post/search)
     * @param params 추가 파라미터 (ex: "word=토끼")
     * @param recordPerPage 페이지당 레코드 수
     * @return HTML 문자열
     */
    public static String pagingBox(int nowPage, int totalRecord, String url, String params, int recordPerPage) {
        StringBuilder html = new StringBuilder();

        int totalPage = (int)Math.ceil((double) totalRecord / recordPerPage);
        int blockSize = 10;  // 한번에 보여줄 페이지 수
        int startPage = ((nowPage - 1) / blockSize) * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPage);

        html.append("<div class='pagination'>");

        if (startPage > 1) {
            html.append("<a href='" + url + "?now_page=" + (startPage - 1) + "&" + params + "'>&laquo;</a>");
        }

        for (int i = startPage; i <= endPage; i++) {
            if (i == nowPage) {
                html.append("<a class='active'>" + i + "</a>");
            } else {
                html.append("<a href='" + url + "?now_page=" + i + "&" + params + "'>" + i + "</a>");
            }
        }

        if (endPage < totalPage) {
            html.append("<a href='" + url + "?now_page=" + (endPage + 1) + "&" + params + "'>&raquo;</a>");
        }

        html.append("</div>");

        return html.toString();
    }
}
