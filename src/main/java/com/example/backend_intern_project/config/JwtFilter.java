package com.example.backend_intern_project.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String url = httpRequest.getRequestURI();

        // 인증이 필요 없는 경로는 바로 통과
        if (isPermitUrl(url)) {
            chain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에서 토큰 파싱
        String bearer = httpRequest.getHeader("Authorization");
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");
            return;
        }

        try {
            String token = jwtUtil.substringToken(bearer);
            Claims claims = jwtUtil.extractClaims(token);

            String userId = claims.getSubject();
            String userRole = claims.get("userRole", String.class);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(userRole))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.");
            return;
        }

        // 마지막에 딱 1번만 호출
        chain.doFilter(request, response);
    }

    private boolean isPermitUrl(String url) {
        return url.startsWith("/signup") ||
            url.startsWith("/signin") ||
            url.startsWith("/swagger") ||
            url.startsWith("/v3/api-docs") ||
            url.startsWith("/swagger-ui") ||
            url.startsWith("/favicon.ico");
    }
}