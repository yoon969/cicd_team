package dev.mvc.pet;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("dev.mvc.pet.PetProc")
public class PetProc implements PetProcInter {

  @Autowired
  private PetDAOInter petDAO;

  @Override
  public int create(PetVO petVO) {
    return this.petDAO.create(petVO);
  }

  @Override
  public ArrayList<PetVO> list() {
    return this.petDAO.list();
  }

  @Override
  public ArrayList<PetVO> listByUserno(int userno) {
    return this.petDAO.listByUserno(userno);
  }

  @Override
  public PetVO read(int petno) {
    return this.petDAO.read(petno);
  }
  
  @Override
  public PetJoinVO readJoin(int petno) {
      return petDAO.readJoin(petno);
  }

  @Override
  public int update(PetVO petVO) {
    return this.petDAO.update(petVO);
  }

  @Override
  public int delete(int petno) {
    return this.petDAO.delete(petno);
  }
  
  @Override
  public ArrayList<PetJoinVO> listByUsersnoJoin(int usersno) {
    return petDAO.listByUsersnoJoin(usersno);
  }
  
}
