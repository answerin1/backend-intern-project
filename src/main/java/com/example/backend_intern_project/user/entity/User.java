package com.example.backend_intern_project.user.entity;

import com.example.backend_intern_project.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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
