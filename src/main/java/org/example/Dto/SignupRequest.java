package org.example.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SignupRequest implements Serializable {
    private String username;
    private String email;
    private String password;
}
