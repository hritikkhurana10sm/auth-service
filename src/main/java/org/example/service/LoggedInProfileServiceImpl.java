package org.example.service;

import org.example.Exception.AuthException;
import org.example.dao.LoggedInUserProfileService;
import org.example.generics.Constants;
import org.example.model.AuthUser;
import org.example.dto.ProfileRequest;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LoggedInProfileServiceImpl implements LoggedInUserProfileService {

    @Autowired
    private UserRepository userRepository;

    public ProfileRequest getUserProfile(String id) {
        AuthUser authUser = userRepository.findById(id)
                .orElseThrow(() -> new AuthException(Constants.USER_NOTFOUND, HttpStatus.INTERNAL_SERVER_ERROR));
        return new ProfileRequest(authUser.getId(), authUser.getUsername(), authUser.getEmail());
    }
}
