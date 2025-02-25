package kr.hlbg.patientsmeal.dto;

import lombok.Builder;
import lombok.Getter;

@Getter // 응답 시 Token 클래스의 필드를 직렬화하기 위해 사용된다.
@Builder
public class Token {
    String accessToken;
    String refreshToken;
}