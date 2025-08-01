package dev.mvc.pet;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.mvc.post.PostProc;
import dev.mvc.species.SpeciesProcInter;
import dev.mvc.species.SpeciesVO;
import dev.mvc.tool.Tool;
import dev.mvc.tool.Upload;
import dev.mvc.users.UsersProc;
import dev.mvc.users.UsersVO;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/pet")
public class PetCont {

  @Autowired
  @Qualifier("dev.mvc.pet.PetProc")
  private PetProcInter petProc;
  
  @Autowired
  @Qualifier("dev.mvc.species.SpeciesProc")
  private SpeciesProcInter speciesProc;

  @Autowired 
  @Qualifier("dev.mvc.users.UsersProc")
  private UsersProc usersProc;
  
  @Autowired 
  @Qualifier("dev.mvc.post.PostProc")
  private PostProc postProc;

  /**
   * 반려동물 등록 폼
   * @param session
   * @param model
   * @return
   */
  @GetMapping("/create")
  public String createForm(HttpSession session, Model model) {
    Integer usersno = (Integer) session.getAttribute("usersno");
    if (usersno == null) return "redirect:/users/login";

    PetVO petVO = new PetVO();
    petVO.setUsersno(usersno);
    model.addAttribute("petVO", petVO);

    model.addAttribute("speciesList", speciesProc.list_all());

    return "pet/create"; // create.html
  }


//  /**
//   * 반려동물 등록 처리
//   * @param petVO
//   * @param session
//   * @return
//   */
//  @PostMapping("/create")
//  public String create(@ModelAttribute PetVO petVO, HttpSession session) {
//    Integer usersno = (Integer) session.getAttribute("usersno");
//    petProc.create(petVO);
//    petVO.setUsersno(usersno);
//    return "redirect:/users/mypage";
//  }
  
  /**
   * 반려동물 등록 처리 (이미지 업로드 포함)
   */
  @PostMapping("/create")
  public String create(@ModelAttribute PetVO petVO, HttpSession session, RedirectAttributes ra) {
    Integer usersno = (Integer) session.getAttribute("usersno");

    // ---------------------------------------
    // 파일 업로드 처리
    // ---------------------------------------
    String file1 = "";
    String file1saved = "";
    String thumb1 = "";

    String upDir = Pet.getUploadDir(); // 파일 저장 폴더
    MultipartFile mf = petVO.getFile1MF(); // 업로드된 파일
    file1 = mf.getOriginalFilename(); // 원본 파일명
    long size1 = mf.getSize(); // 파일 크기

    if (size1 > 0) { // 업로드된 파일이 있는 경우
      if (Tool.checkUploadFile(file1)) {
        file1saved = Upload.saveFileSpring(mf, upDir);

        if (Tool.isImage(file1saved)) {
          thumb1 = Tool.preview(upDir, file1saved, 200, 150);
        }

        petVO.setFile1(file1);
        petVO.setFile1saved(file1saved);
        petVO.setThumb1(thumb1);
        petVO.setSize1(size1);
      } else {
        ra.addFlashAttribute("code", Tool.UPLOAD_FILE_CHECK_FAIL);
        ra.addFlashAttribute("cnt", 0);
        ra.addFlashAttribute("url", "/pet/msg");
        return "redirect:/pet/msg";
      }
    } else {
      System.out.println("-> 이미지 없이 등록됨");
    }

    // ---------------------------------------
    // DB 저장
    // ---------------------------------------
    petVO.setUsersno(usersno); // 세션 사용자 번호 설정
    petProc.create(petVO);

    return "redirect:/users/mypage";
  }
  
  /**
   * 조회
   * @param petno
   * @param model
   * @return
   */
  @GetMapping("/read")
  public String read(@RequestParam("petno") int petno, Model model) {
    PetJoinVO pet = petProc.readJoin(petno);
    model.addAttribute("petVO", pet);
    
    return "pet/read"; // pet/read.html
  }

  /**
   * 수정 폼
   * @param petno
   * @param model
   * @return
   */
  @GetMapping("/update")
  public String updateForm(@RequestParam("petno") int petno, Model model) {
    PetVO petVO = petProc.read(petno);
    model.addAttribute("petVO", petVO);
    
    model.addAttribute("speciesList", speciesProc.list_all());
    return "pet/update"; // pet/update.html
  }

  /**
   * 수정 처리
   * @param petVO
   * @return
   */
  @PostMapping("/update")
  public String update(PetVO petVO) {
    petProc.update(petVO);
    return "redirect:/pet/read?petno=" + petVO.getPetno();
  }

  /**
   * 삭제 처리
   * @param petno
   * @return
   */
  @GetMapping("/delete")
  public String delete(@RequestParam("petno") int petno) {
    petProc.delete(petno);
    return "redirect:/users/mypage";
  }
}
