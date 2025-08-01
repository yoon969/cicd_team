package dev.mvc.news;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class NewsService {

  public List<NewsVO> list() {
    // 둘 중 하나 선택 또는 합치기
    List<NewsVO> list = new ArrayList<>();
    list.addAll(fetchNewsFromNaver());
    list.addAll(fetchNewsFromDaum());
    return list;
  }
  
  public List<NewsVO> fetchNewsFromNaver() {
    List<NewsVO> newsList = new ArrayList<>();

    try {
      String url = "https://search.naver.com/search.naver?where=news&query=동물";
      Document doc = Jsoup.connect(url).get();

      Elements items = doc.select("a.news_tit"); // 뉴스 제목 링크
      for (Element item : items) {
        String title = item.attr("title");
        String link = item.attr("href");

        NewsVO news = new NewsVO(title, link, "", "");
        newsList.add(news);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return newsList;
  }
  
  public List<NewsVO> fetchNewsFromDaum() {
    List<NewsVO> newsList = new ArrayList<>();

    try {
      String url = "https://search.daum.net/search?w=news&q=동물";
      Document doc = Jsoup.connect(url).get();

      Elements items = doc.select(".c-item-content .tit-g a");
      for (Element item : items) {
        String title = item.text();
        String link = item.attr("href");

        NewsVO news = new NewsVO(title, link, "", "");
        newsList.add(news);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return newsList;
  }
}
