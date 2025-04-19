package com.example.backend_intern_project.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret-key}")
    private String secretKey;

    // 토큰 생성
    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);

        // 만료 1시간 설정
        long expiration = 1000L * 60 * 60;
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
            .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 인증 객체 반환
    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        return new UsernamePasswordAuthenticationToken(username, "", null);
    }

    // 토큰에서 사용자 이름 추출
    public String getUsername(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    // 요청에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }
}
