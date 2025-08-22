package com.example.hanaharmonybackend.util;

import com.example.hanaharmonybackend.domain.User;
import com.example.hanaharmonybackend.payload.code.ErrorStatus;
import com.example.hanaharmonybackend.payload.exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static User getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new CustomException(ErrorStatus.USER_NOT_FOUND);
        }
    }
}
