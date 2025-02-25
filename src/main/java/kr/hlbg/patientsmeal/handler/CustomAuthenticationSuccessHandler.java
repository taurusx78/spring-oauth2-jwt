package kr.hlbg.patientsmeal.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hlbg.patientsmeal.dto.Token;
import kr.hlbg.patientsmeal.entity.User;
import kr.hlbg.patientsmeal.exception.UserNotFoundException;
import kr.hlbg.patientsmeal.utils.JWTConstants;
import kr.hlbg.patientsmeal.utils.JWTUtil;
import kr.hlbg.patientsmeal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * OAuth2 로그인 성공 후 호출되는 커스텀 핸들러.
 * 다중 토큰(Access Token, Refresh Token) 생성 후 Response Body 에 담아 사용자에게 응답하는 역할을 수행한다.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 사용자 정보 가져오기
        String username = authentication.getName();
        GrantedAuthority auth = authentication.getAuthorities().iterator().next();
        String role = auth.getAuthority();

        // 다중 토큰 생성
        String accessToken = jwtUtil.createJWT(JWTConstants.ACCESS_TOKEN, username, role, JWTConstants.ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = jwtUtil.createJWT(JWTConstants.REFRESH_TOKEN, username, role, JWTConstants.REFRESH_TOKEN_EXPIRE_TIME);

        // DB에 Refresh 토큰 저장 & 마지막 로그인 일시 업데이트
        saveRefreshTokenAndUpdateLastLogin(username, refreshToken);

        // Response Body 에 토큰 담아서 응답
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(
                Token.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build()
        ));
        response.getWriter().flush();
    }

    private void saveRefreshTokenAndUpdateLastLogin(String username, String refreshToken) {
        User user = Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UserNotFoundException(username));
        user.setRefreshToken(refreshToken);
        user.setLastLoggedAt();
        userRepository.save(user);
    }
}
