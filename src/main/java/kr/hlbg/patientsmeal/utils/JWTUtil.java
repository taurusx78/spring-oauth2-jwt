package kr.hlbg.patientsmeal.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {

    private String secretKey;

    public JWTUtil(@Value("${spring.jwt.secret") String secret) {
        // secret 문자열을 바이트 배열로 변환 후, Base64 형식의 문자열로 인코딩
        secretKey = Base64.getEncoder().encodeToString(secret.getBytes());

    }

    public String getCategory(String token) {
        return parseClaims(token).get("category", String.class);
    }

    public String getUsername(String token) {
        return parseClaims(token).get("username", String.class);
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    public String createJWT(String category, String username, String role, long expiredMs) {
        return Jwts.builder()
                .addClaims(
                        Map.of(
                                "category", category,
                                "username", username,
                                "role", role
                        )
                )
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(SignatureAlgorithm.HS256, secretKey) // HS256 알고리즘으로 암호화하여 비밀키 생성
                .compact();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
