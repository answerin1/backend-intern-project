package com.example.backend_intern_project.auth.service;

import com.example.backend_intern_project.auth.dto.request.SigninRequest;
import com.example.backend_intern_project.auth.dto.request.SignupRequest;
import com.example.backend_intern_project.auth.dto.response.SigninResponse;
import com.example.backend_intern_project.auth.dto.response.SignupResponse;
import com.example.backend_intern_project.auth.exception.SigninException;
import com.example.backend_intern_project.auth.exception.SignupException;
import com.example.backend_intern_project.config.JwtUtil;
import com.example.backend_intern_project.user.entity.User;
import com.example.backend_intern_project.user.enums.UserRole;
import com.example.backend_intern_project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new SignupException( "이미 가입된 사용자입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        User newUser = new User(
            signupRequest.getUsername(),
            encodedPassword,
            signupRequest.getNickname(),
            UserRole.USER
        );

        userRepository.save(newUser);

        return new SignupResponse(newUser.getUsername(), newUser.getNickname(), newUser.getRole().name());
    }

    @Transactional
    public SigninResponse signin(SigninRequest request) throws SigninException {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new SigninException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new SigninException("Invalid credentials");
        }

        return new SigninResponse(jwtUtil.createToken(user.getId(),user.getRole()));

    }
}
