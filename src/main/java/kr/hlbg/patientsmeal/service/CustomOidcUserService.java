package kr.hlbg.patientsmeal.service;

import kr.hlbg.patientsmeal.dto.ProviderUser;
import kr.hlbg.patientsmeal.repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends AbstractOAuth2UserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    public CustomOidcUserService(UserService userService, UserRepository userRepository) {
        super(userService, userRepository);
    }

    /**
     * 인가 서버와 통신해서 사용자 정보 가져온다.
     */
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService = new OidcUserService();
        OidcUser oidcUser = oidcUserService.loadUser(userRequest);

        ProviderUser providerUser = super.providerUser(clientRegistration, oidcUser);

        // 회원가입
        String username = providerUser.getProvider() + "_" + providerUser.getUsername();
        super.register(username, providerUser);

        return oidcUser;
    }
}
