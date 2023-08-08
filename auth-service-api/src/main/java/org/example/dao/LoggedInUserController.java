package org.example.dao;

import org.example.dto.ProfileRequest;
import org.example.generics.Response;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface LoggedInUserController {

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Response<ProfileRequest>> getUserProfile();
}
