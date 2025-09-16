package com.example;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.user.SiteUser;
import com.example.user.UserRepository;
import com.example.util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        SiteUser user = null;

        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String providerId = oAuth2User.getAttribute("sub");

            user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                user = SiteUser.builder()
                        .email(email)
                        .username(name)
                        .provider("GOOGLE")
                        .providerId(providerId)
                        .build();
                userRepository.save(user);
            }
        } else {
            String username = authentication.getName();
            user = userRepository.findByusername(username)
                    .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다: " + username));
        }

        String token = jwtUtil.generateToken(user);

        Cookie cookie = new Cookie("ACCESS_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.sendRedirect("/");
    }



}
