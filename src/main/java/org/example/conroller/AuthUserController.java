package org.example.conroller;

import jakarta.transaction.Transactional;
import org.example.Dto.*;
import org.example.Exception.AuthException;
import org.example.generics.Constants;
import org.example.model.AuthUser;
import org.example.model.UserModel;
import org.example.repository.UserRepository;
import org.example.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Value("${parking.app.reset-uri}")
    private String resetUri;

    @GetMapping("/users")
    List<AuthUser> getUsers(){
        return  userRepository.findAll();
    }

    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticateUser( @RequestBody MultiValueMap<String, Object> encodedSigninData) throws UnsupportedEncodingException {
        LoginRequest loginRequest = new LoginRequest();
        if (encodedSigninData.containsKey("username") && encodedSigninData.containsKey("password") && encodedSigninData.get("username").size() == 1 && encodedSigninData.get("password").size() == 1) {
            loginRequest.setUsername(encodedSigninData.get("username").get(0).toString());
            loginRequest.setPassword(encodedSigninData.get("password").get(0).toString());
        } else {
            throw new AuthException(Constants.UNPROCESSABLE_REQUEST, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        UserModel authUser = (UserModel) authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())).getPrincipal();

        final String jwt = jwtUtils.generateToken(authUser);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody SignupRequest signUpRequest) throws AuthException {

        List<String> errors = new ArrayList<>();
        String username = signUpRequest.getUsername();
        String password = signUpRequest.getPassword();
        String email = signUpRequest.getEmail();
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
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new AuthException(Constants.NOTUNIQUE_USERNAME, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new AuthException(Constants.NOTUNIQUE_EMAIL, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        LocalDateTime registerTime = LocalDateTime.now();
        userRepository.save(new AuthUser(UUID.randomUUID().toString(), signUpRequest.getUsername(), signUpRequest.getPassword(), signUpRequest.getEmail(), UUID.randomUUID().toString(), registerTime, null));
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }

    @PutMapping("/signup/{userId}/activate/{activationId}")
    public  ResponseEntity<?> activateUser(@PathVariable String userId,@PathVariable String activationId) throws AuthException {
        AuthUser registeredUser = userRepository.findById(userId).orElseThrow(() -> new AuthException(Constants.USER_NOTFOUND, HttpStatus.NOT_FOUND));
        if (registeredUser.getActivationId() == null || registeredUser.getActivationId().isEmpty()) {
            throw new AuthException(Constants.OLD_ACTIVATION, HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(activationId, registeredUser.getActivationId())) {
            throw new AuthException(Constants.ACCESS_DENIED, HttpStatus.UNAUTHORIZED);
        }
        registeredUser.setActivationId(null);
        userRepository.save(registeredUser);
        return ResponseEntity.ok(new ApiResponse(true,"User activated successfully"));
    }

    @PostMapping("/reset-password/initiate")
    public  ResponseEntity<?> initiateResetPassword(@RequestBody String usernameOrEmail) {
        AuthUser authUser = userRepository.findByUsernameOrEmail(usernameOrEmail);
        if (authUser == null) {
            throw new UsernameNotFoundException("username or email not found");
        }
        authUser.setResetId(UUID.randomUUID().toString());
        userRepository.save(authUser);
        String uriToBeEmail=resetUri.replace("{userId}", authUser.getId()).replace("{resetId}",authUser.getResetId());
        //TODO:send email having uriToBeEmail
        System.out.println(uriToBeEmail);
        return ResponseEntity.ok(new ApiResponse(true,"Email has been sent to reistered email address"));
    }

    @PostMapping("/reset-password/user/{userId}/reset/{resetId}")
    ResponseEntity<?> resetPassword(@PathVariable("userId") String userId, @PathVariable("resetId") String resetId, @RequestBody ResetPasswordRequest resetPasswordDto){
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
        return ResponseEntity.ok(new ApiResponse(true,"Password reset successfully"));
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void deleteExpiredUsers() {
        userRepository.deleteByRegisterTimeBefore(LocalDateTime.now().minusMinutes(15));
    }

}
