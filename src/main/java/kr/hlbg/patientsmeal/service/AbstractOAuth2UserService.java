package kr.hlbg.patientsmeal.service;

import kr.hlbg.patientsmeal.dto.ProviderUser;
import kr.hlbg.patientsmeal.dto.response.UserResponse;
import kr.hlbg.patientsmeal.entity.User;
import kr.hlbg.patientsmeal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class AbstractOAuth2UserService {

    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * 어떤 Oauth2 제공자의 사용자인가?
     */
    public ProviderUser providerUser(ClientRegistration clientRegistration, OAuth2User oAuth2User) {
        String registrationId = clientRegistration.getRegistrationId();

        if (registrationId.equals("kakao")) {
            return new UserResponse.KakaoUser(oAuth2User, clientRegistration);
        } else if (registrationId.equals("google")) {
            return new UserResponse.GoogleUser(oAuth2User, clientRegistration);
        }

        return null;
    }

    public void register(String username, ProviderUser providerUser) {
        User user = userRepository.findByUsername(username);

        if(user == null) {
            System.out.println("회원가입이 완료되었습니다.");
            userService.register(username, providerUser);
        } else {
            System.out.println("이미 가입된 회원입니다.");
        }
    }
}
