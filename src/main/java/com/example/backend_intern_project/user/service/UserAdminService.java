package com.example.backend_intern_project.user.service;

import com.example.backend_intern_project.user.dto.response.UserResponse;
import com.example.backend_intern_project.user.entity.User;
import com.example.backend_intern_project.user.enums.UserRole;
import com.example.backend_intern_project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse promoteToAdmin(Long userId, String role) {
        if (!"ADMIN".equals(role)) {
            throw new IllegalArgumentException("관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다.");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.changeRole(UserRole.ADMIN);
        return new UserResponse(userId, user.getUsername(), user.getNickname(), role);
    }
}
