package com.example.backend_intern_project;

import com.example.backend_intern_project.config.JwtUtil;
import com.example.backend_intern_project.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.rmi.ServerException;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // 테스트용 secret key 설정 (Base64 인코딩된 문자열)
        String testSecret = "testtesttesttesttesttesttesttest"; // 최소 32바이트
        String encodedKey = java.util.Base64.getEncoder().encodeToString(testSecret.getBytes());

        // @Value로 주입받는 필드를 수동으로 주입
        ReflectionTestUtils.setField(jwtUtil, "secretKey", encodedKey);

        // init() 호출해서 key 초기화
        jwtUtil.init();
    }

    // 토큰 생성 및 파싱 테스트
    @Test
    void createAndParseToken_success() {
        // given
        Long userId = 1L;
        UserRole role = UserRole.ADMIN;

        // when
        String token = jwtUtil.createToken(userId, role);
        String pureToken = null;
        try {
            pureToken = jwtUtil.substringToken(token);
        } catch (ServerException e) {
            fail("토큰 파싱 실패", e);
        }

        Claims claims = jwtUtil.extractClaims(pureToken);

        // then
        assertThat(claims.getSubject()).isEqualTo(String.valueOf(userId));
        assertThat(claims.get("userRole")).isEqualTo("ADMIN");
        assertThat(claims.getExpiration()).isAfter(new Date()); // 만료 시간이 현재보다 미래여야 함
    }

    // "Bearer "가 없는 잘못된 토큰 예외 처리
    @Test
    void substringToken_fail_invalidFormat() {
        // given
        String token = "InvalidTokenFormat";

        // when & then
        assertThatThrownBy(() -> jwtUtil.substringToken(token))
            .isInstanceOf(ServerException.class)
            .hasMessageContaining("Not Found Token");
    }

    // 잘못된 토큰 형식 예외 처리
    @Test
    void extractClaims_fail_malformedToken() {
        // given
        String invalidToken = "this.is.not.jwt";

        // when & then
        assertThatThrownBy(() -> jwtUtil.extractClaims(invalidToken))
            .isInstanceOf(MalformedJwtException.class);
    }

    // 만료된 토큰 테스트
    @Test
    void expired_token_should_throw_exception() throws ServerException {
        // 만료된 토큰 직접 생성
        String expiredToken = "Bearer " + Jwts.builder()
            .setSubject("1")
            .claim("username", "user1")
            .claim("userRole", UserRole.USER.name())
            .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2))
            .setExpiration(new Date(System.currentTimeMillis() - 1000))
            .signWith((Key) ReflectionTestUtils.getField(jwtUtil, "key"), SignatureAlgorithm.HS256)
            .compact();

        String pureToken = jwtUtil.substringToken(expiredToken);

        // then: 만료된 토큰으로 JWT를 파싱하려고 하면 ExpiredJwtException이 발생해야 함
        assertThatThrownBy(() -> jwtUtil.extractClaims(pureToken))
            .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }
}

