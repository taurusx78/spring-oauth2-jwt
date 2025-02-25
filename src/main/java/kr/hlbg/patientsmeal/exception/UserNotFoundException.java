package kr.hlbg.patientsmeal.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super("존재하지 않는 사용자입니다. : " + message);
    }
}

