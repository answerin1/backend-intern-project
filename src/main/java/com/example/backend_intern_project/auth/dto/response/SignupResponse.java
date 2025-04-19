package com.example.backend_intern_project.auth.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class SignupResponse {

    private final String username;
    private final String nickname;
    private final RoleResponse roles;

    public SignupResponse(String username, String nickname, String role) {
        this.username = username;
        this.nickname = nickname;
        this.roles = new RoleResponse(role);
    }

    @Getter
    public static class RoleResponse {
        private final String role;

        public RoleResponse(String role) {
            this.role = role;
        }
    }
}
