package dev.mvc.team1;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository; // ✅ 주입 필요

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        
         // ✅ CSRF 설정: hospital/save는 제외
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/hospital/save","/api/**")
                
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())  // ✅ 여기 추가!
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/users/**",
                    "/css/**", "/js/**", "/images/**", "/oauth2/**",
                    "/api/**",              // ✅ React API 경로 허용
                    "/react/**",            // ✅ 정적 빌드 파일 경로 허용 (예: index.html)
                    "/static/**", "/assets/**",
                    "/news",
                    "/news/",
                    "/news/index.html",
                    "/news/assets/**",
                    "/news/*.js",
                    "/news/*.css",
                    "/news/**", 
                    "/notice/**",
                    "/notice/index.html", // ✅ 이 라인 추가
                    "/forward:/notice/index.html" // ✅ 이것도 함께 테스트용으로 추가
                ).permitAll()
                .anyRequest().permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/users/login")
                .failureHandler((request, response, exception) -> {
                  exception.printStackTrace();  // ✅ 콘솔에 강제 출력
                  response.sendRedirect("/users/login?error=oauth2");
                })
                .authorizationEndpoint(endpoint -> endpoint
                    .authorizationRequestResolver(customAuthorizationRequestResolver())  // ✅ 연결
                )
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }

    // ✅ CustomAuthorizationRequestResolver Bean 등록
    @Bean
    public CustomAuthorizationRequestResolver customAuthorizationRequestResolver() {
        return new CustomAuthorizationRequestResolver(clientRegistrationRepository);
    }
    
}
