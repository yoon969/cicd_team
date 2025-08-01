package dev.mvc.team1;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import dev.mvc.users.UsersProcInter;
import dev.mvc.users.UsersVO;
import jakarta.servlet.http.HttpSession;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsersProcInter usersProc;
    private final HttpSession session;

    // ✅ 생성자 주입 + @Qualifier
    public CustomOAuth2UserService(@Qualifier("dev.mvc.users.UsersProc") UsersProcInter usersProc,
                                   HttpSession session) {
        this.usersProc = usersProc;
        this.session = session;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // ex: "google", "kakao"

        String email = null;
        String name = null;
        String phone = null;
        if ("kakao".equals(registrationId)) {
            // 🔸 카카오 응답 구조: kakao_account → email, profile.nickname
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            email = (String) kakaoAccount.get("email");
            name = (String) profile.get("nickname");
        } else if ("google".equals(registrationId)) {
            // 🔹 구글은 평범하게 email, name 바로 있음
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else if ("naver".equals(registrationId)) {
          // ✅ 네이버는 attributes 안에 "response" 객체 있음
          Map<String, Object> response = (Map<String, Object>) attributes.get("response");
          email = (String) response.get("email");
          name = (String) response.get("name");
          phone = (String) response.get("mobile"); 
        } else {
            throw new IllegalArgumentException("Unsupported provider: " + registrationId);
        }

        // ✅ 회원 존재 여부 확인 후 등록
        UsersVO user = usersProc.findByEmail(email); 
        if (user == null) {
            user = new UsersVO();
            user.setEmail(email);
            user.setUsersname(name);
            user.setPasswd("social_login");
            user.setTel(phone != null ? phone : "000-0000-0000");
            user.setZipcode("");
            user.setAddress1("");
            user.setAddress2("");
            user.setRole("user");

            usersProc.create(user);
            user = usersProc.findByEmail(email);
        }

        // ✅ 세션 저장
        session.setAttribute("usersno", user.getUsersno());
        session.setAttribute("usersname", user.getUsersname());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("role", user.getRole());
        
        return oAuth2User;
    }

}
