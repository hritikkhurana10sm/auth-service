package org.example.conroller;


import jakarta.security.auth.message.AuthException;
import org.example.Dto.ProfileRequest;
import org.example.service.LoggedInProfileService;
import org.example.util.PrincipalDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.generics.Response;

import java.util.ArrayList;

@RestController
public class LoggedInUserController {

    private final LoggedInProfileService loggedInProfileService;

    private final PrincipalDetails principalDetails;

    public LoggedInUserController(LoggedInProfileService loggedInUserProfileService, PrincipalDetails principalDetails) {
        this.loggedInProfileService = loggedInUserProfileService;
        this.principalDetails=principalDetails;
    }

    @GetMapping("/profile")
    ResponseEntity<?> getUserProfile() throws AuthException {
        String userIdFromToken=this.principalDetails.getPrincipalDetails().getId();
        ProfileRequest userProfile=this.loggedInProfileService.getUserProfile(userIdFromToken);
        Response<ProfileRequest> response=new Response<>(userProfile,new ArrayList<>());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
