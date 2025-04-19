package com.example.backend_intern_project.auth.controller;

import com.example.backend_intern_project.auth.dto.request.SigninRequest;
import com.example.backend_intern_project.auth.dto.request.SignupRequest;
import com.example.backend_intern_project.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        @PostMapping("/signup")
        public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
            return ResponseEntity.ok(authService.signup(request));
        }

        @PostMapping("/signin")
        public ResponseEntity<?> signin(@Valid @RequestBody SigninRequest request) {
            return ResponseEntity.ok(authService.signin(request));
        }
    }
