package org.example.dao;

import org.example.dto.ResetPasswordRequest;
import org.example.dto.SignupDto;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

@Service
public interface AuthUserService extends Serializable {

    void signup(SignupDto signupDto);

    String signin(MultiValueMap<String, Object> encodedSigninData) throws UnsupportedEncodingException;

    void activateUser(String userId, String activationId);

    void initiateResetPassword(String usernameOrEmail);

    void resetPassword(String userId, String resetId, ResetPasswordRequest resetPasswordRequest);
}
