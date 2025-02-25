package kr.hlbg.patientsmeal.service;

import kr.hlbg.patientsmeal.dto.ProviderUser;
import kr.hlbg.patientsmeal.repository.UserRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends AbstractOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    public CustomOAuth2UserService(UserService userService, UserRepository userRepository) {
        super(userService, userRepository);
    }

    /**
     * 인가 서버와 통신해서 사용자 정보 가져온다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        ProviderUser providerUser = super.providerUser(clientRegistration, oAuth2User);

        if (providerUser == null) return null;

        // 회원가입 or 로그인
        String username = providerUser.getProvider() + "_" + providerUser.getUsername();
        super.register(username, providerUser);

        return oAuth2User;
    }
}
