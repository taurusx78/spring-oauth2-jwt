package kr.hlbg.patientsmeal.service;

import kr.hlbg.patientsmeal.dto.ProviderUser;
import kr.hlbg.patientsmeal.entity.User;
import kr.hlbg.patientsmeal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void register(String username, ProviderUser providerUser) {
        userRepository.save(User.builder()
                .username(username)
                .nickname(providerUser.getNickname())
                .email(providerUser.getEmail())
                .role("ROLE_USER")
                .build()
        );
    }
}
