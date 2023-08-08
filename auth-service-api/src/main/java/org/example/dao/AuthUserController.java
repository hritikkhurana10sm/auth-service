package org.example.dao;

import org.example.dto.ResetPasswordRequest;
import org.example.dto.SignupDto;
import org.example.generics.Response;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;

public interface AuthUserController {

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Response<Void>> signupUser(@RequestBody SignupDto signupDto);

    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Response<String>> signinUser(@RequestBody MultiValueMap<String, Object> encodedData) throws UnsupportedEncodingException;

    @PutMapping(value = "/signup/{userId}/activate/{activationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Response<Void>> activateUser(@PathVariable("userId") String userId, @PathVariable("activationId") String activationId);

    @PostMapping(value = "/reset-password/initiate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Response<Void>> initiateResetPassword(@RequestBody String usernameOrEmail);

    @PostMapping(value = "/reset-password/user/{userId}/reset/{resetId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Response<Void>> resetPassword(@PathVariable("userId") String userID, @PathVariable("resetId") String resetId, @RequestBody ResetPasswordRequest resetPassword);
}
