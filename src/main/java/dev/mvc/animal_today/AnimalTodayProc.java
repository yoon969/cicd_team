package dev.mvc.animal_today;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dev.mvc.animal_today.AnimalTodayProc")
public class AnimalTodayProc implements AnimalTodayProcInter {
  
  @Autowired
  private AnimalTodayDAOInter animalTodayDAO;

  @Override
  public AnimalTodayVO getTodayAnimal() {
    return animalTodayDAO.getTodayAnimal();
  }

  @Override
  public int updateSummary(AnimalTodayVO vo) {
    return animalTodayDAO.updateSummary(vo);
  }
  
  @Override
  public AnimalTodayVO findByName(String name) {
    return animalTodayDAO.findByName(name);
  }

  @Override
  public int insert(AnimalTodayVO vo) {
    return animalTodayDAO.insert(vo);
  }
}
