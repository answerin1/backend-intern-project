package com.example.backend_intern_project.auth.service;

import com.example.backend_intern_project.auth.dto.request.SigninRequest;
import com.example.backend_intern_project.auth.dto.request.SignupRequest;
import com.example.backend_intern_project.auth.dto.response.SigninResponse;
import com.example.backend_intern_project.auth.dto.response.SignupResponse;
import com.example.backend_intern_project.auth.exception.AuthException;
import com.example.backend_intern_project.auth.exception.InvalidRequestException;
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
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        User newUser = new User(
            signupRequest.getEmail(),
            encodedPassword,
            userRole
        );
        User savedUser = userRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getEmail(), String.valueOf(userRole));

        return new SignupResponse(bearerToken);
    }

    public SigninResponse signin(SigninRequest signinRequest) {
        User user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(
            () -> new InvalidRequestException("가입되지 않은 유저입니다."));

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new AuthException("잘못된 비밀번호입니다.");
        }

        String bearerToken = jwtUtil.createToken(user.getEmail(),
            String.valueOf(user.getUserRole()));

        return new SigninResponse(bearerToken);
    }
}
