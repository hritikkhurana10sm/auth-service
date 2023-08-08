package org.example.util;

import org.example.Exception.AuthException;
import org.example.generics.Constants;
import org.example.model.UserModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PrincipalDetails {

    public UserModel getPrincipalDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return (UserModel) auth.getPrincipal();
        } else {
            throw new AuthException(Constants.AUTH_FAILED_MESSAGE, HttpStatus.FORBIDDEN);
        }
    }
}
