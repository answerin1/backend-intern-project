package com.example.backend_intern_project.auth.common;

import com.example.backend_intern_project.user.enums.UserRole;
import lombok.Getter;

@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final UserRole userRole;

    public AuthUser(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }
}
