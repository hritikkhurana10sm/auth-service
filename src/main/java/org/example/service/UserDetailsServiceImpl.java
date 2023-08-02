package org.example.service;

import org.example.model.AuthUser;
import org.example.model.UserModel;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final AuthUser authUser = userRepository.findByUsername(username);
        if (authUser == null) {
            throw new UsernameNotFoundException("username not found");
        }
        if (authUser.getActivationId() != null && !authUser.getActivationId().isEmpty()) {
            return new UserModel(authUser.getId(), username, authUser.getPassword(), authUser.getEmail(), Collections.emptySet(), false);
        } else {
            return new UserModel(authUser.getId(), username, authUser.getPassword(), authUser.getEmail(), Collections.emptySet(), true);
        }
    }

}
