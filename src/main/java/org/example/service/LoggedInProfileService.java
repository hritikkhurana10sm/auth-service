package org.example.service;

import jakarta.security.auth.message.AuthException;
import org.example.model.AuthUser;
import org.example.Dto.ProfileRequest;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggedInProfileService{

    @Autowired
    private UserRepository userRepository;

    public ProfileRequest getUserProfile(String id) throws AuthException {
        AuthUser authUser = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));
        return new ProfileRequest(authUser.getId(), authUser.getUsername(), authUser.getEmail());
    }
}
