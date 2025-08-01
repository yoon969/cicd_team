package dev.mvc.hospital_species;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("dev.mvc.hospital_species.HospitalSpeciesProc")
public class HospitalSpeciesProc implements HospitalSpeciesProcInter {

  @Autowired
  private HospitalSpeciesDAOInter hospitalSpeciesDAO;

  @Override
  public int create(HospitalSpeciesVO hospitalSpeciesVO) {
    return this.hospitalSpeciesDAO.create(hospitalSpeciesVO);
  }

  @Override
  public ArrayList<HospitalSpeciesVO> listByHospitalno(int hospitalno) {
      return this.hospitalSpeciesDAO.listByHospitalno(hospitalno);
  }

  @Override
  public ArrayList<HospitalSpeciesVO> list_by_speciesno(int speciesno) {
    return this.hospitalSpeciesDAO.list_by_speciesno(speciesno);
  }

  @Override
  public int delete(int id) {
    return this.hospitalSpeciesDAO.delete(id);
  }

  @Override
  public int delete_by_hospitalno(int hospitalno) {
    return this.hospitalSpeciesDAO.delete_by_hospitalno(hospitalno);
  }

  @Override
  public int delete_by_speciesno(int speciesno) {
    return this.hospitalSpeciesDAO.delete_by_speciesno(speciesno);
  }
}
