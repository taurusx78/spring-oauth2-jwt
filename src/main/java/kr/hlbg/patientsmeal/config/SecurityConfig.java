package kr.hlbg.patientsmeal.config;

import kr.hlbg.patientsmeal.filter.CustomLogoutFilter;
import kr.hlbg.patientsmeal.filter.JWTFilter;
import kr.hlbg.patientsmeal.handler.CustomAuthenticationSuccessHandler;
import kr.hlbg.patientsmeal.repository.UserRepository;
import kr.hlbg.patientsmeal.service.CustomOAuth2UserService;
import kr.hlbg.patientsmeal.service.CustomOidcUserService;
import kr.hlbg.patientsmeal.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration // 해당 어노테이션 없으면, OAuth2ClientConfig 적용 안됨..
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final List<String> ALLOWED_URIS = List.of(
            "http://localhost:8080"
            // Todo - 허용 URL 추가하기
    );

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    /**
     * 정적 자원에 인증 없이 접근할 수 있도록 설정
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        /*
         * WebSecurityCustomizer 인터페이스의 단일 추상 메서드 customize()를 구현하는 람다 표현식.
         * WebSecurityCustomizer 클래스 생성 후 리턴한다.
         */
        return (web) -> web.ignoring().requestMatchers("/static/js/**", "/static/images/**", "/static/css/**", "/static/scss/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CORS 설정
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();

            configuration.setAllowedOrigins(ALLOWED_URIS);
            configuration.setAllowedMethods(Collections.singletonList("*"));
            configuration.setAllowCredentials(true);

            configuration.setAllowedHeaders(Collections.singletonList("*"));
            configuration.setExposedHeaders(Collections.singletonList("*"));

            return configuration;
        }));

        // JWT 설정
        http
                // 1. CSRF disable
                .csrf((auth) -> auth.disable())
                // 2. Form 로그인 방식 disable
                .formLogin((auth) -> auth.disable())
                // 3. HTTP Basic 인증 방식 disable
                .httpBasic((auth) -> auth.disable())
                // 4. 세션 STATELESS 설정
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 필터 등록
        http
                // JWT 검증 필터
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                // 로그아웃 필터
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, userRepository), LogoutFilter.class);

        http.authorizeHttpRequests(request -> request
                        .requestMatchers("/", "/reissue").permitAll()
                        .requestMatchers("/api/user").hasAnyRole("SCOPE_profile", "SCOPE_email")
                        .requestMatchers("/api/oidc").hasAnyRole("SCOPE_openid")
                        .anyRequest().authenticated())
                .logout(logout -> logout.logoutSuccessUrl("/"));

        // OAuth2 설정
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(config -> config
                        .userService(customOAuth2UserService)
                        .oidcUserService(customOidcUserService))
                // 로그인 성공 시 핸들러 호출
                .successHandler(new CustomAuthenticationSuccessHandler(jwtUtil, userRepository)));

        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper customAuthoritiesMapper() {
        return new CustomAuthorityMapper();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
