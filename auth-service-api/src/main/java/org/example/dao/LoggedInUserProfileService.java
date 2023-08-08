package org.example.dao;

import org.example.dto.ProfileRequest;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public interface LoggedInUserProfileService extends Serializable {

     ProfileRequest getUserProfile(String id);
}
