package com.example.backend_intern_project.user.controller;

import com.example.backend_intern_project.user.dto.response.UserResponse;
import com.example.backend_intern_project.user.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    @PatchMapping("/admin/users/{userId}/roles")
    public ResponseEntity<UserResponse> promoteToAdmin(@PathVariable Long userId, Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        return ResponseEntity.ok(userAdminService.promoteToAdmin(userId, role));
    }
}
