package org.example.service;


import jakarta.transaction.Transactional;
import org.example.dto.LoginRequest;
import org.example.dto.ResetPasswordRequest;
import org.example.dto.SignupDto;
import org.example.Exception.AuthException;
import org.example.dao.AuthUserService;
import org.example.generics.Constants;
import org.example.model.AuthUser;
import org.example.model.UserModel;
import org.example.repository.UserRepository;
import org.example.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthUserServiceImpl implements AuthUserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${parking.app.reset-uri}")
    private String resetUri;

    @Override
    public void signup(SignupDto signupDto) {
        List<String> errors = new ArrayList<>();
        String username = signupDto.getUsername();
        String password = signupDto.getPassword();
        String email = signupDto.getEmail();
        if (username == null) {
            errors.add(Constants.EMPTY_USERNAME);
        }
        if (password == null) {
            errors.add(Constants.EMPTY_PASSWORD);
        }
        if (email == null) {
            errors.add(Constants.EMPTY_EMAIL);
        }
        if (!errors.isEmpty()) {
            throw new AuthException(errors, HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByUsername(signupDto.getUsername())) {
            throw new AuthException(Constants.NOTUNIQUE_USERNAME, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (userRepository.existsByEmail(signupDto.getEmail())) {
            throw new AuthException(Constants.NOTUNIQUE_EMAIL, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!Objects.equals(signupDto.getPassword(), signupDto.getConfirmPassword())) {
            throw new AuthException(Constants.UNCONFIRMED_PASSWORD, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        signupDto.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        LocalDateTime registerTime = LocalDateTime.now();
        userRepository.save(new AuthUser(UUID.randomUUID().toString(), signupDto.getUsername(), signupDto.getPassword(), signupDto.getEmail(), signupDto.getName(), UUID.randomUUID().toString(), registerTime, null));
    }

    @Override
    public String signin(MultiValueMap<String, Object> encodedSigninData) throws UnsupportedEncodingException {
        LoginRequest loginRequest = new LoginRequest();
        if (encodedSigninData.containsKey("username") && encodedSigninData.containsKey("password") && encodedSigninData.get("username").size() == 1 && encodedSigninData.get("password").size() == 1) {
            loginRequest.setUsername(encodedSigninData.get("username").get(0).toString());
            loginRequest.setPassword(encodedSigninData.get("password").get(0).toString());
        } else {
            throw new AuthException(Constants.UNPROCESSABLE_REQUEST, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        List<String> errors = new ArrayList<>();
        if (loginRequest.getUsername() == null) {
            errors.add(Constants.EMPTY_USERNAME);
        }
        if (loginRequest.getPassword() == null) {
            errors.add(Constants.EMPTY_PASSWORD);
        }
        if (!errors.isEmpty()) {
            throw new AuthException(errors, HttpStatus.BAD_REQUEST);
        }
        try{
            UserModel authUser = (UserModel) authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())).getPrincipal();

            return jwtUtils.generateToken(authUser);
        } catch (AuthenticationException ex) {
            throw new AuthException(Constants.INVALID_CREDENTIALS, HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void activateUser(String userId, String activationId) {
        AuthUser registeredUser = userRepository.findById(userId).orElseThrow(() -> new AuthException(Constants.USER_NOTFOUND, HttpStatus.NOT_FOUND));
        if (registeredUser.getActivationId() == null || registeredUser.getActivationId().isEmpty()) {
            throw new AuthException(Constants.OLD_ACTIVATION, HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(activationId, registeredUser.getActivationId())) {
            throw new AuthException(Constants.ACCESS_DENIED, HttpStatus.UNAUTHORIZED);
        }
        registeredUser.setActivationId(null);
        userRepository.save(registeredUser);
    }

    @Override
    public void initiateResetPassword(String usernameOrEmail) {
        AuthUser authUser = userRepository.findByUsernameOrEmail(usernameOrEmail);
        if (authUser == null) {
            throw new UsernameNotFoundException("username or email not found");
        }
        authUser.setResetId(UUID.randomUUID().toString());
        userRepository.save(authUser);
        String uriToBeEmail=resetUri.replace("{userId}", authUser.getId()).replace("{resetId}",authUser.getResetId());
        //TODO:send email having uriToBeEmail
        System.out.println(uriToBeEmail);
    }

    @Override
    public void resetPassword(String userId, String resetId, ResetPasswordRequest resetPasswordDto) {
        if (resetPasswordDto.getNewPassword()==null || resetPasswordDto.getConfirmPassword()==null) {
            throw new AuthException(Constants.UNPROCESSABLE_REQUEST, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        List<String> errors = new ArrayList<>();
        if (!Objects.equals(resetPasswordDto.getNewPassword(), resetPasswordDto.getConfirmPassword())) {
            errors.add(Constants.UNCONFIRMED_PASSWORD);
        }
        if (!errors.isEmpty()) {
            throw new AuthException(errors, HttpStatus.BAD_REQUEST);
        }
        AuthUser userInDb = userRepository.findById(userId).orElseThrow(() -> new AuthException(Constants.USER_NOTFOUND, HttpStatus.NOT_FOUND));
        if(!Objects.equals(userInDb.getResetId(), resetId)){
            throw new AuthException(Constants.USER_NOTFOUND,HttpStatus.NOT_FOUND);
        }
        userInDb.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        userInDb.setResetId(null);
        userRepository.save(userInDb);

    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void deleteExpiredUsers() {
        userRepository.deleteByRegisterTimeBefore(LocalDateTime.now().minusMinutes(15));
    }
}
