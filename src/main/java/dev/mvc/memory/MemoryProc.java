package dev.mvc.memory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("dev.mvc.memory.MemoryProc")
public class MemoryProc implements MemoryProcInter {

  @Autowired
  private MemoryDAOInter memoryDAO;

  @Override
  public int create(MemoryVO memoryVO) {
    return this.memoryDAO.create(memoryVO);
  }

  @Override
  public ArrayList<MemoryVO> list() {
    return this.memoryDAO.list();
  }

  @Override
  public ArrayList<MemoryVO> listByUsersno(int usersno) {
    return this.memoryDAO.listByUsersno(usersno);
  }

  @Override
  public MemoryVO read(int memoryno) {
    return this.memoryDAO.read(memoryno);
  }

  @Override
  public int delete(int memoryno) {
    MemoryVO vo = memoryDAO.read(memoryno);  // 삭제 전 파일 경로 가져오기

    // ✅ 실제 경로 (원본 이미지)
    String uploadDir = "C:/kd/ws_java/team1_v2sbm3c/src/main/resources/static" + vo.getImage_url();
    File uploadFile = new File(uploadDir);
    if (uploadFile.exists()) {
      uploadFile.delete();
    }

    // ✅ 실제 경로 (생성 이미지)
    String storageDir = "C:/kd/ws_java/team1_v2sbm3c/src/main/resources" + vo.getGenerated_image_url();
    File genFile = new File(storageDir);
    if (genFile.exists()) {
      genFile.delete();
    }

    return memoryDAO.delete(memoryno);  // DB 삭제
  }


  @Override
  public int deleteByUsersno(int usersno) {
    return this.memoryDAO.deleteByUsersno(usersno);
  }
  
  @Override
  public List<MemoryVO> list_by_paging(Map<String, Object> map) {
    return memoryDAO.list_by_paging(map);
  }

  @Override
  public int total() {
    return memoryDAO.total();
  }
  
  @Override
  public int total_by_usersno(int usersno) {
    return memoryDAO.total_by_usersno(usersno); // 🔹 추가
  }
}
