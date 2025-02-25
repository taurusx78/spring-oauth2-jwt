package kr.hlbg.patientsmeal.dto.response;

import kr.hlbg.patientsmeal.dto.OAuth2ProviderUser;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class UserResponse {

    public static class GoogleUser extends OAuth2ProviderUser {

        public GoogleUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
            super(oAuth2User, clientRegistration);
        }

        @Override
        public String getUsername() {
            return (String) getAttributes().get("sub");
        }

        @Override
        public String getNickname() {
            return (String) getAttributes().get("name");
        }
    }

    public static class KakaoUser extends OAuth2ProviderUser {

        public KakaoUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
            super(oAuth2User, clientRegistration);
        }

        @Override
        public String getUsername() {
            return (String) getAttributes().get("sub");
        }

        @Override
        public String getNickname() {
            return (String) getAttributes().get("nickname");
        }
    }
}
