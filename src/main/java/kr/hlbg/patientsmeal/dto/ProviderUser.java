package kr.hlbg.patientsmeal.dto;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Map;

public interface ProviderUser {

    String getUsername();

    String getNickname();

    String getEmail();

    String getProvider(); // Oauth2 제공자 (Ex. Google, Kakao 등)

    List<? extends GrantedAuthority> getAuthorities(); // 사용자 권한

    Map<String, Object> getAttributes(); // 사용자 속성
}
