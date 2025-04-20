package com.example.backend_intern_project;

import com.example.backend_intern_project.user.dto.response.UserResponse;
import com.example.backend_intern_project.user.entity.User;
import com.example.backend_intern_project.user.enums.UserRole;
import com.example.backend_intern_project.user.repository.UserRepository;
import com.example.backend_intern_project.user.service.UserAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 관리자 권한 부여 성공
    @Test
    void promoteToAdmin_success() {
        // given
        Long userId = 1L;
        User user = User.builder()
            .id(userId)
            .username("user1")
            .nickname("nick1")
            .role(UserRole.USER)  // 기존 역할은 USER
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        UserResponse response = userAdminService.promoteToAdmin(userId, "ADMIN");

        // then
        assertThat(response.getUsername()).isEqualTo("user1");
        assertThat(response.getRoles().getRole()).isEqualTo("ADMIN");
    }

    // 관리자 권한 부여 실패 - 권한이 "ADMIN"이 아닌 경우
    @Test
    void promoteToAdmin_fail_invalid_role() {
        // given
        Long userId = 1L;
        User user = User.builder()
            .id(userId)
            .username("user1")
            .nickname("nick1")
            .role(UserRole.USER)
            .build();

        // when & then
        assertThatThrownBy(() -> userAdminService.promoteToAdmin(userId, "USER"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다.");
    }

    // 관리자 권한 부여 실패 - 사용자가 존재하지 않는 경우
    @Test
    void promoteToAdmin_fail_user_not_found() {
        // given
        Long userId = 999L;  // 존재하지 않는 사용자 ID
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userAdminService.promoteToAdmin(userId, "ADMIN"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }
}

