package com.example.backend_intern_project.user.controller;

import com.example.backend_intern_project.auth.annotation.Auth;
import com.example.backend_intern_project.auth.common.AuthUser;
import com.example.backend_intern_project.user.dto.request.UserChangePasswordRequest;
import com.example.backend_intern_project.user.dto.response.UserResponse;
import com.example.backend_intern_project.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(@Auth AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }
}
