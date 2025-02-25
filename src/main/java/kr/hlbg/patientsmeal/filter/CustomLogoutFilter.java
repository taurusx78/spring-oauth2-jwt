package kr.hlbg.patientsmeal.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hlbg.patientsmeal.entity.User;
import kr.hlbg.patientsmeal.exception.UserNotFoundException;
import kr.hlbg.patientsmeal.repository.UserRepository;
import kr.hlbg.patientsmeal.utils.JWTUtil;
import kr.hlbg.patientsmeal.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 요청 경로와 HTTP Method 검증
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();
        if (!requestUri.matches("^/logout$") || !requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 현재 로그인한 유저의 Refresh 토큰 null 로 업데이트
        deleteRefreshToken(SecurityUtil.currentUsername());
    }

    private void deleteRefreshToken(String username) {
        User user = Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UserNotFoundException(username));
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}
