package org.example.conroller;

import jakarta.transaction.Transactional;
import org.example.Dto.*;
import org.example.Exception.AuthException;
import org.example.generics.Constants;
import org.example.generics.Response;
import org.example.model.AuthUser;
import org.example.model.UserModel;
import org.example.repository.UserRepository;
import org.example.service.AuthUserService;
import org.example.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@RestController
public class AuthUserController {

    @Autowired
    AuthUserService authUserService;

    @Value("${parking.app.reset-uri}")
    private String resetUri;

    @GetMapping("/users")
    List<AuthUser> getUsers(){
        return  authUserService.getUsers();
    }

    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<String >> authenticateUser( @RequestBody MultiValueMap<String, Object> encodedSigninData){
        String accessToken = authUserService.signin(encodedSigninData);
        List<String> messages = new ArrayList<>();
        messages.add(Constants.LOGIN_SUCCESSFULLY);
        Response<String> response = new Response<>(accessToken, messages);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<Response<Void>> registerUser(@RequestBody SignupRequest signUpRequest){

        try{
            authUserService.signup(signUpRequest);
            List<String> messages = new ArrayList<>();
            messages.add(Constants.USER_REGISTERED);
            Response<Void> response = new Response<>(null, messages);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (DataIntegrityViolationException ex) {
            throw new AuthException(ex.getMostSpecificCause().getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/signup/{userId}/activate/{activationId}")
    public  ResponseEntity<?> activateUser(@PathVariable String userId,@PathVariable String activationId) throws AuthException {

        authUserService.activateUser(userId, activationId);
        List<String> messages = new ArrayList<>();
        messages.add(Constants.USER_ACTIVATED);
        Response<Void> response = new Response<>(null, messages);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password/initiate")
    public  ResponseEntity<Response<Void>> initiateResetPassword(@RequestBody String usernameOrEmail) {
        authUserService.initiateResetPassword(usernameOrEmail);
        List<String> messages = new ArrayList<>();
        messages.add("Email have been sent to registered email-address");
        Response<Void> response = new Response<>(null, messages);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/reset-password/user/{userId}/reset/{resetId}")
    ResponseEntity<Response<Void>> resetPassword(@PathVariable("userId") String userId, @PathVariable("resetId") String resetId, @RequestBody ResetPasswordRequest resetPasswordDto){
        this.authUserService.resetPassword(userId, resetId, resetPasswordDto);
        List<String> messages = new ArrayList<>();
        messages.add("Password Reset Successfully");
        return new ResponseEntity<>(new Response<>(null, messages), HttpStatus.OK);
    }



}
