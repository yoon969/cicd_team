package dev.mvc.consult_match;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ConsultMatch 매핑 Proc 구현체
 */
@Component("dev.mvc.consult_match.ConsultMatchProc")
public class ConsultMatchProc implements ConsultMatchProcInter {

    @Autowired
    private ConsultMatchDAOInter consultMatchDAO;

    @Override
    public int create(ConsultMatchVO consultMatchVO) {
        return this.consultMatchDAO.create(consultMatchVO);
    }

    @Override
    public List<ConsultMatchVO> listByConsultno(int consultno) {
        return this.consultMatchDAO.listByConsultno(consultno);
    }


    @Override
    public int deleteByConsultno(int consultno) {
        return this.consultMatchDAO.deleteByConsultno(consultno);
    }
}
