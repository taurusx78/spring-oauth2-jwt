package kr.hlbg.patientsmeal.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hlbg.patientsmeal.utils.JWTConstants;
import kr.hlbg.patientsmeal.utils.JWTUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT를 검증하기 위한 커스텀 필터.
 * JWT가 존재하는 경우 이를 검증하고 강제로 SecurityContextHolder에 세션을 생성한다.
 * (세션은 STATELESS 상태로 관리되기 때문에 요청이 끝나면 소멸된다.)
 */
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Access Token 추출
        String accessToken = extractAccessToken(request);

        // 토큰이 없는 경우, 다음 필터로 넘긴다.
        if (!StringUtils.hasText(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 만료된 경우, 다음 필터로 넘기지 않는다.
        if (jwtUtil.isExpired(accessToken)) {
            // Response Body 구성
            response.getWriter().print("Access Token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Access 토큰이 아닌 경우, 다음 필터로 넘기지 않는다.
        if (!jwtUtil.getCategory(accessToken).equals(JWTConstants.ACCESS_TOKEN)) {
            // Response Body 구성
            response.getWriter().print("Invalid Access Token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 2. 토큰 검증
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        Authentication authentication = getAuthentication(username, role);

        // 세션 생성
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private static Authentication getAuthentication(String username, String role) {
        if (username == null || role == null) {
            throw new IllegalArgumentException("Invalid access token: username or role is null");
        }

        // UserDetails 객체에 User 정보 담기
        User principal = new User(
                username,
                null,
                List.of(new SimpleGrantedAuthority(role))
        );

        // 스프링 시큐리티 인증 토큰 생성 및 반환
        return new UsernamePasswordAuthenticationToken(principal, "", null);
    }

    private String extractAccessToken(HttpServletRequest request) {
        var token = request.getHeader(JWTConstants.AUTHORIZATION_HEADER);

        // 토큰이 null 또는 공백이 아니고 Bearer 로 시작하는 경우, 토큰 값 리턴
        if (StringUtils.hasText(token) && token.startsWith(JWTConstants.BEARER_PREFIX)) {
            return token.substring(JWTConstants.BEARER_PREFIX.length());
        }

        return null;
    }
}
