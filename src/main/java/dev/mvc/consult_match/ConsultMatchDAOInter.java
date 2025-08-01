package dev.mvc.consult_match;

import java.util.ArrayList;
import java.util.List;

/**
 * ConsultMatch 매퍼 인터페이스
 */
public interface ConsultMatchDAOInter {
  /**
   * 유사 상담 매핑 등록
   * @param vo ConsultMatchVO
   * @return 저장된 행 개수
   */
  int create(ConsultMatchVO vo);

  /**
   * 특정 상담번호(consultno)에 대한 유사상담 매핑 목록 조회
   * @param consultno 기준 상담번호
   * @return ConsultMatchVO 리스트
   */
  List<ConsultMatchVO> listByConsultno(int consultno);

  /**
   * 특정 상담번호에 매핑된 모든 유사상담 매핑 삭제
   * @param consultno 기준 상담번호
   * @return 삭제된 행 개수
   */
  int deleteByConsultno(int consultno);
}
