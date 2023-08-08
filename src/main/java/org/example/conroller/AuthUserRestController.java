package org.example.conroller;

import org.example.Exception.AuthException;
import org.example.dao.AuthUserController;
import org.example.dto.ResetPasswordRequest;
import org.example.dto.SignupDto;
import org.example.generics.Constants;
import org.example.generics.Response;
import org.example.service.AuthUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/auth")
public class AuthUserRestController implements AuthUserController {

    @Autowired
    AuthUserServiceImpl authUserService;

    @Value("${parking.app.reset-uri}")
    private String resetUri;

    @Override
    public ResponseEntity<Response<Void>> signupUser(SignupDto signupDto) {
        try{
            authUserService.signup(signupDto);
            List<String> messages = new ArrayList<>();
            messages.add(Constants.USER_REGISTERED);
            Response<Void> response = new Response<>(null, messages);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (DataIntegrityViolationException ex) {
            throw new AuthException(ex.getMostSpecificCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Response<String>> signinUser(MultiValueMap<String, Object> encodedSigninData) throws UnsupportedEncodingException {
        String accessToken = authUserService.signin(encodedSigninData);
        List<String> messages = new ArrayList<>();
        messages.add(Constants.LOGIN_SUCCESSFULLY);
        Response<String> response = new Response<>(accessToken, messages);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response<Void>> activateUser(String userId, String activationId) {
        authUserService.activateUser(userId, activationId);
        List<String> messages = new ArrayList<>();
        messages.add(Constants.USER_ACTIVATED);
        Response<Void> response = new Response<>(null, messages);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response<Void>> initiateResetPassword(String usernameOrEmail) {
        authUserService.initiateResetPassword(usernameOrEmail);
        List<String> messages = new ArrayList<>();
        messages.add("Email have been sent to registered email-address");
        Response<Void> response = new Response<>(null, messages);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response<Void>> resetPassword(String userId, String resetId, ResetPasswordRequest resetPasswordDto) {
        authUserService.resetPassword(userId, resetId, resetPasswordDto);
        List<String> messages = new ArrayList<>();
        messages.add("Password Reset Successfully");
        return new ResponseEntity<>(new Response<>(null, messages), HttpStatus.OK);
    }



}
