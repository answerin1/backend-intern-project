package com.example.backend_intern_project;

import com.example.backend_intern_project.auth.dto.request.SigninRequest;
import com.example.backend_intern_project.auth.dto.request.SignupRequest;
import com.example.backend_intern_project.auth.dto.response.SigninResponse;
import com.example.backend_intern_project.auth.dto.response.SignupResponse;
import com.example.backend_intern_project.auth.service.AuthService;
import com.example.backend_intern_project.config.JwtUtil;
import com.example.backend_intern_project.user.entity.User;
import com.example.backend_intern_project.user.enums.UserRole;
import com.example.backend_intern_project.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 회원가입 성공
    @Test
    void signup_success() {
        // given
        SignupRequest request = new SignupRequest("testuser", "password123", "닉네임");
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // when
        SignupResponse response = authService.signup(request);

        // then
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getNickname()).isEqualTo("닉네임");
        assertThat(response.getRoles().getRole()).isEqualTo("USER");

        verify(userRepository).save(any(User.class));
    }

    // 회원가입 실패 - 이미 존재하는 사용자
    @Test
    void signup_fail_duplicate_user() {
        // given
        SignupRequest request = new SignupRequest("existingUser", "pass", "닉");
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 가입된 사용자입니다.");
    }

    // 로그인 성공
    @Test
    void signin_success() {
        // given
        SigninRequest request = new SigninRequest("user1", "pass123");
        User user = User.builder()
            .id(1L)
            .username("user1")
            .password("encodedPass")
            .nickname("nick1")
            .role(UserRole.USER)
            .build();


        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "encodedPass")).thenReturn(true);
        when(jwtUtil.createToken(1L, UserRole.USER)).thenReturn("jwt-token");

        // when
        SigninResponse response = authService.signin(request);

        // then
        assertThat(response.getBearerToken()).isEqualTo("jwt-token");
    }

    // 로그인 실패 - 유저 없음
    @Test
    void signin_fail_user_not_found() {
        // given
        SigninRequest request = new SigninRequest("notfound", "pass");
        when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.signin(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 존재하는 사용자입니다.");
    }

    // 로그인 실패 - 비밀번호 불일치
    @Test
    void signin_fail_wrong_password() {
        // given
        SigninRequest request = new SigninRequest("user1", "wrongpass");
        User user = new User("user1", "encodedPass", "nick", UserRole.USER);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encodedPass")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.signin(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
}
