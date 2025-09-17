package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.user.CustomOAuth2UserService;
import com.example.util.JwtFilter;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtFilter jwtFilter;
    private final JwtLoginSuccessHandler jwtLoginSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
	
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http
        .headers(headers -> headers
            .addHeaderWriter(new XFrameOptionsHeaderWriter(
                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
        .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", 
                        "/user/login", "/user/signup", 
                        "/login/**", "/oauth2/**", 
                        "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
        )
        .formLogin(form -> form
                .loginPage("/user/login")
                .loginProcessingUrl("/login")
                .successHandler(jwtLoginSuccessHandler)
         )
        .oauth2Login(oauth2 -> oauth2
                .loginPage("/user/login")
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(jwtLoginSuccessHandler)
         )
        .logout(logout -> logout
        	    .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
        	    .logoutSuccessHandler((request, response, authentication) -> {
        	        Cookie cookie = new Cookie("ACCESS_TOKEN", null);
        	        cookie.setMaxAge(0);    
        	        cookie.setPath("/");     
        	        response.addCookie(cookie);

        	        response.sendRedirect("/"); 
        	    })
        	    .invalidateHttpSession(true)
        );

    	http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    	return http.build();
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}