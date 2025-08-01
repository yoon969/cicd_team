package dev.mvc.hospital;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("dev.mvc.hospital.HospitalProc")
public class HospitalProc implements HospitalProcInter {

  @Autowired
  private HospitalDAOInter hospitalDAO;

  @Override
  public int create(HospitalVO hospitalVO) {
    return this.hospitalDAO.create(hospitalVO);
  }

  @Override
  public ArrayList<HospitalVO> list() {
    return this.hospitalDAO.list();
  }

  @Override
  public HospitalVO read(int hospitalno) {
    return this.hospitalDAO.read(hospitalno);
  }

  @Override
  public int update(HospitalVO hospitalVO) {
    return this.hospitalDAO.update(hospitalVO);
  }

  @Override
  public int delete(int hospitalno) {
    return this.hospitalDAO.delete(hospitalno);
  }
  
  @Override
  public int isDuplicateAddress(String address) {
    return this.hospitalDAO.isDuplicateAddress(address);
  }

  
  @Override
  public ArrayList<HospitalVO> list_all_paging(Map<String, Object> map) {
    return hospitalDAO.list_all_paging(map);
  }

  @Override
  public int count_all(Map<String, Object> map) {
    return hospitalDAO.count_all(map);
  }

  @Override
  public String pagingBox(int total, int now_page, String word, String list_url, int pagePerBlock) {
    int recordsPerPage = 10;
    int totalPage = (int) Math.ceil((double) total / recordsPerPage);
    int totalGrp = (int) Math.ceil((double) totalPage / pagePerBlock);
    int nowGrp = (int) Math.ceil((double) now_page / pagePerBlock);
    int startPage = ((nowGrp - 1) * pagePerBlock) + 1;
    int endPage = nowGrp * pagePerBlock;

    StringBuilder str = new StringBuilder();

    // ✅ 시작: <div class="paging">
    str.append("<div class='paging'>");

    // ◀ 이전 페이지 그룹
    if (nowGrp >= 2) {
      int prev = ((nowGrp - 1) * pagePerBlock);
      str.append("<a class='arrow' href='").append(list_url)
         .append("?now_page=").append(prev)
         .append("&word=").append(word)
         .append("'\">&#x2039;</a> "); // ‹
    }

    // 페이지 번호들
    for (int i = startPage; i <= endPage && i <= totalPage; i++) {
      if (i == now_page) {
        str.append("<a class='btn-page current'>").append(i).append("</a>");
      } else {
        str.append("<a class='btn-page' href='").append(list_url)
           .append("?now_page=").append(i)
           .append("&word=").append(word)
           .append("'>").append(i).append("</a>");
      }
    }

    // ▶ 다음 페이지 그룹
    if (nowGrp < totalGrp) {
      int next = (nowGrp * pagePerBlock) + 1;
      str.append("<a class='arrow' href='").append(list_url)
         .append("?now_page=").append(next)
         .append("&word=").append(word)
         .append("'\">&#x203A;</a> "); // ›
    }

    // ✅ 끝: </div>
    str.append("</div>");

    return str.toString();
  }

  
}
