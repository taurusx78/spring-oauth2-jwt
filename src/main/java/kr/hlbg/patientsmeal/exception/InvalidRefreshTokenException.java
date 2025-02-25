package kr.hlbg.patientsmeal.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException() {
        super("Refresh 토큰이 일치하지 않습니다.");
    }
}
