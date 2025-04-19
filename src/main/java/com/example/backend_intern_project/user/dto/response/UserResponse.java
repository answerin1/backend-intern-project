package com.example.backend_intern_project.user.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class UserResponse {
    private final Long userId;
    private final String username;
    private final String nickname;
    private final RoleResponse roles;


    public UserResponse(Long userId, String username, String nickname, String role) {
        this.userId = userId;
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
