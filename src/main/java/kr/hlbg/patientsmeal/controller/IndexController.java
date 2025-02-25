package kr.hlbg.patientsmeal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class IndexController {

    @GetMapping("/")
    public ResponseEntity<String> index(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User) {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        if(oAuth2AuthenticationToken != null) {
            Map<String, Object> attributes = oAuth2User.getAttributes();
            return new ResponseEntity<>((String) attributes.get("name"), HttpStatus.OK);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @GetMapping("/api/user")
    public ResponseEntity<Authentication> user(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User) {
        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }

    @GetMapping("/api/oidc")
    public ResponseEntity<Authentication> oidc(Authentication authentication, @AuthenticationPrincipal OidcUser oidcUser) {
        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }
}
