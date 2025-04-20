package com.example.backend_intern_project.auth.service;

import com.example.backend_intern_project.auth.dto.request.SigninRequest;
import com.example.backend_intern_project.auth.dto.request.SignupRequest;
import com.example.backend_intern_project.auth.dto.response.SigninResponse;
import com.example.backend_intern_project.auth.dto.response.SignupResponse;
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
            throw new IllegalArgumentException( "이미 가입된 사용자입니다.");
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
    public SigninResponse signin(SigninRequest request) throws IllegalArgumentException {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("이미 존재하는 사용자입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return new SigninResponse(jwtUtil.createToken(user.getId(),user.getRole()));

    }
}
