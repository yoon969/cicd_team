package dev.mvc.animal_today;

public interface AnimalTodayProcInter {
  public AnimalTodayVO getTodayAnimal();
  public int updateSummary(AnimalTodayVO vo);
  public AnimalTodayVO findByName(String name);
  public int insert(AnimalTodayVO vo);
}
