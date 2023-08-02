package org.example.util;

import jakarta.security.auth.message.AuthException;
import org.example.model.UserModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PrincipalDetails {

    public UserModel getPrincipalDetails() throws AuthException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return (UserModel) auth.getPrincipal();
        } else {
            throw new AuthException("Failed Authentication");
        }
    }
}
