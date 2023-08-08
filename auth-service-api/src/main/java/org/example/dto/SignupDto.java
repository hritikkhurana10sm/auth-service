package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SignupDto implements Serializable {
    private String username;
    private String email;
    private String name;
    private String password;
    private String confirmPassword;
}
