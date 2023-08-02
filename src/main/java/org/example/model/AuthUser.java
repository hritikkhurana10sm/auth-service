package org.example.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
    @Id
    private String id;
    private String username;

    private String password;

    private String email;

    private String activationId;

    private LocalDateTime registerTime;
    private String resetId;
}
