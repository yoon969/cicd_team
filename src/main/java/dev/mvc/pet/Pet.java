package dev.mvc.pet;

import java.io.File;

// 파일 업로드 경로는 war 외부의 절대경로를 지정해야 파일이 손실되지 않습니다. 
// 만약 이렇게 안하면 war 생성시마다 업로드 경로가 초기화 되어 등록된 모든 파일이 삭제됩니다. ★
public class Pet {
  public static String getUploadDir() {
    String osName = System.getProperty("os.name").toLowerCase();
    String path = "";

    if (osName.contains("win")) { // Windows
      path = "C:\\kd\\deploy\\team1\\pet\\storage\\";
      // System.out.println("Windows: " + path);
    } else if (osName.contains("mac")) { // MacOS
      path = "/Users/yourusername/deploy/resort/pet/storage/";
      // System.out.println("MacOS: " + path);
    } else { // Linux
      path = "/home/ubuntu/deploy/resort/pet/storage/";
      // System.out.println("Linux: " + path);
    }

    return path;
  }

//    // Windows, VMWare, AWS cloud 절대 경로 설정
//    public static synchronized String getUploadDir() {
//        String path = "";
//        if (File.separator.equals("\\")) { // windows, 개발 환경의 파일 업로드 폴더
//            // path = "C:/kd/deploy/resort_v2sbm3c/pet/storage/";
//            path="C:\\kd\\deploy\\resort_v4sbm3c\\pet\\storage\\";
//            // System.out.println("Windows 10: " + path);
//            
//        } else { // Linux, AWS, 서비스용 배치 폴더 
//            // System.out.println("Linux");
//            path = "/home/ubuntu/deploy/resort_v4sbm3c/pet/storage/";
//        }
//        
//        return path;
//    }

}
