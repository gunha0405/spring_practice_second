package com.example.util;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter{
	
	private final JwtUtil jwtUtil;
	private final UserDetailsService uds;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = resolveToken(request); 
        if (token != null) {
            try {
                var claims = jwtUtil.parse(token).getBody();
                String username = claims.getSubject();
                String tenant = claims.get("tenant", String.class);

                var ud = uds.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

                TenantContext.set(tenant);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        try { filterChain.doFilter(request, response); }
        finally { TenantContext.clear(); }
		
	}
	
	private String resolveToken(HttpServletRequest request) {
        // 1) Authorization 헤더
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) return auth.substring(7);

        // 2) 쿠키
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(c.getName())) return c.getValue();
            }
        }
        return null;
    }
	
}
