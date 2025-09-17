package com.example.util;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null) {
            try {
                Claims claims = jwtUtil.parse(token).getBody();

                String username = claims.getSubject(); 
                String tenant = claims.get("tenant", String.class);

                @SuppressWarnings("unchecked")
                var roles = (List<String>) claims.get("roles");  // 토큰에서 roles 꺼냄

                var authorities = (roles == null ? List.of("USER") : roles).stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .toList();

                var auth = new UsernamePasswordAuthenticationToken(
                        username, null, authorities
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

                TenantContext.set(tenant);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) return auth.substring(7);

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(c.getName())) return c.getValue();
            }
        }
        return null;
    }
}
