package kr.hlbg.patientsmeal.service;

import jakarta.transaction.Transactional;
import kr.hlbg.patientsmeal.dto.Token;
import kr.hlbg.patientsmeal.entity.User;
import kr.hlbg.patientsmeal.exception.InvalidRefreshTokenException;
import kr.hlbg.patientsmeal.exception.UserNotFoundException;
import kr.hlbg.patientsmeal.utils.JWTConstants;
import kr.hlbg.patientsmeal.utils.JWTUtil;
import kr.hlbg.patientsmeal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Transactional
    public Token reissue(String refreshToken) {
        // 토큰이 없거나, 만료되었거나, Refresh 토큰이 아닌 경우, null 반환
        if (!StringUtils.hasText(refreshToken)
                || jwtUtil.isExpired(refreshToken)
                || !jwtUtil.getCategory(refreshToken).equals(JWTConstants.REFRESH_TOKEN)) {
            return null;
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        User user = getUserByUsername(username);

        // Refresh 토큰 유효성 검사
        validateRefreshToken(user.getRefreshToken(), refreshToken);

        // Access 토큰 발급
        String accessToken = jwtUtil.createJWT(
                JWTConstants.ACCESS_TOKEN, username, role, JWTConstants.ACCESS_TOKEN_EXPIRE_TIME
        );

        // Refresh 토큰 갱신 (>>> 보안성 강화, 로그인 지속시간 연장)
        String newRefreshToken = jwtUtil.createJWT(
                JWTConstants.REFRESH_TOKEN, username, role, JWTConstants.REFRESH_TOKEN_EXPIRE_TIME
        );

        // DB에 저장된 Refresh 토큰 업데이트
        user.setRefreshToken(newRefreshToken);

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private User getUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    private void validateRefreshToken(String savedRefreshToken, String refreshToken) {
        if (!savedRefreshToken.equals(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
    }
}
