package com.example.backend_intern_project.user.entity;

import com.example.backend_intern_project.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User(String username, String encodedPassword, String nickname, UserRole role) {
        this.username = username;
        this.password = encodedPassword;
        this.nickname = nickname;
        this.role = role;
    }

    public void changeRole(UserRole newRole) {
        this.role = newRole;
    }

}
