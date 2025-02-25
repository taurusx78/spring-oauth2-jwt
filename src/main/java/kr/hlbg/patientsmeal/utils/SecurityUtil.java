package kr.hlbg.patientsmeal.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
