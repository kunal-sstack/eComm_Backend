package com.projectX.backend.Payloads;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginCredentials {

    @Column(unique = true, nullable = false)
    private String email;
    private String password;
}
