package dev.mvc.consult_match;

import java.util.List;

/**
 * ConsultMatch 매핑 Proc 인터페이스
 */
public interface ConsultMatchProcInter {
  /**
   * 유사 상담 매핑 등록
   * @param consultMatchVO 저장할 매핑 VO
   * @return 저장된 행 개수
   */
  int create(ConsultMatchVO consultMatchVO);

  /**
   * 특정 상담번호(consultno)에 대한 매핑 리스트 조회
   * @param consultno 기준 상담번호
   * @return ConsultMatchVO 리스트
   */
  List<ConsultMatchVO> listByConsultno(int consultno);


  /**
   * 특정 상담번호(consultno)에 매핑된 모든 매핑 삭제
   * @param consultno 기준 상담번호
   * @return 삭제된 행 개수
   */
  int deleteByConsultno(int consultno);
}
