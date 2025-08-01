package dev.mvc.postgood;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("dev.mvc.postgood.PostgoodProc")
public class PostgoodProc implements PostgoodProcInter {
  @Autowired
  PostgoodDAOInter postgoodDAO;
  
  @Override
  public int create(PostgoodVO postgoodVO) {
    int cnt = this.postgoodDAO.create(postgoodVO);
    return cnt;
  }

  @Override
  public ArrayList<PostgoodVO> list_all() {
    ArrayList<PostgoodVO> list = this.postgoodDAO.list_all();
    return list;
  }

  @Override
  public PostgoodVO read(int postgoodno) {
    PostgoodVO postgoodVO = this.postgoodDAO.read(postgoodno);
    return postgoodVO;
  }
  
  @Override
  public int deleteByPostnoUsersno(Map<String, Object> map) {
    return this.postgoodDAO.deleteByPostnoUsersno(map);
  }

  @Override
  public int hartCnt(Map<String, Object> map) {
    return this.postgoodDAO.hartCnt(map);
  }

  @Override
  public PostgoodVO readByPostnoUsersno(HashMap<String, Object> map) {
    PostgoodVO postgoodVO = this.postgoodDAO.readByPostnoUsersno(map);
    return postgoodVO;
  }

  @Override
  public ArrayList<PostPostgoodUsersVO> list_all_join() {
    ArrayList<PostPostgoodUsersVO> list = this.postgoodDAO.list_all_join();
    return list;
  }

  @Override
  public int deleteByPostno(int postno) {
    return this.postgoodDAO.deleteByPostno(postno);
  }

}



