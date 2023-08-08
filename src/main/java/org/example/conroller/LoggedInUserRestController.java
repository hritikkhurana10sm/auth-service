package org.example.conroller;


import org.example.dto.ProfileRequest;
import org.example.dao.LoggedInUserController;
import org.example.dto.ProfileRequest;
import org.example.generics.Response;
import org.example.service.LoggedInProfileServiceImpl;
import org.example.util.PrincipalDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/auth")
public class LoggedInUserRestController implements LoggedInUserController {

    private final LoggedInProfileServiceImpl loggedInProfileService;

    private final PrincipalDetails principalDetails;

    public LoggedInUserRestController(LoggedInProfileServiceImpl loggedInUserProfileService, PrincipalDetails principalDetails) {
        this.loggedInProfileService = loggedInUserProfileService;
        this.principalDetails=principalDetails;
    }

    @Override
    public ResponseEntity<Response<ProfileRequest>> getUserProfile() {
        String userIdFromToken=this.principalDetails.getPrincipalDetails().getId();
        ProfileRequest userProfile=this.loggedInProfileService.getUserProfile(userIdFromToken);
        Response<ProfileRequest> response=new Response<>(userProfile,new ArrayList<>());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
