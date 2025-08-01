package dev.mvc.team1;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        if (req == null) return null;

        String uri = request.getRequestURI(); // 예: /oauth2/authorization/google
        String registrationId = uri.substring(uri.lastIndexOf("/") + 1); // → google, kakao, naver

        return customize(req, registrationId);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
        if (req == null) return null;

        return customize(req, clientRegistrationId);
    }

    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest req, String registrationId) {
        Map<String, Object> extraParams = new HashMap<>(req.getAdditionalParameters());

        switch (registrationId) {
            case "google" -> extraParams.put("prompt", "select_account"); // ✅ 구글 계정 선택창 강제
            case "kakao" -> extraParams.put("prompt", "login");           // ✅ 카카오 로그인 강제
        }

        return OAuth2AuthorizationRequest.from(req)
                .additionalParameters(extraParams)
                .build();
    }
}
