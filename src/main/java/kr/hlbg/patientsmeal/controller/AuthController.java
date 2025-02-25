package kr.hlbg.patientsmeal.controller;

import kr.hlbg.patientsmeal.dto.Token;
import kr.hlbg.patientsmeal.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody String refreshToken) {
        Token token = authService.reissue(refreshToken);

        if (token == null) {
            return new ResponseEntity<>("Refresh Token expired or does not exist.", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(authService.reissue(refreshToken), HttpStatus.OK);
    }
}
