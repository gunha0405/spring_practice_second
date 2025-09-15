package com.example;

import com.example.user.SiteUser;
import com.example.user.UserRepository;
import com.example.util.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginJwtCookieTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtUtil jwtUtil;

    String username;

    @BeforeEach
    void setUp() {
        username = "user_" + java.util.UUID.randomUUID();
        SiteUser u = new SiteUser();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode("qwer1234"));
        u.setEmail(username + "@test.com");
        u.setCustomerId("A");
        userRepository.save(u);
    }

    @Test
    void 로그인_성공시_JWT쿠키에_tenant_클레임포함() throws Exception {
        var result = mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("password", "qwer1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().exists("ACCESS_TOKEN"))
                .andExpect(cookie().httpOnly("ACCESS_TOKEN", true))
                .andExpect(cookie().path("ACCESS_TOKEN", "/"))
                .andReturn();

        var tokenCookie = result.getResponse().getCookie("ACCESS_TOKEN");
        assertThat(tokenCookie).isNotNull();

        var claims = jwtUtil.parse(tokenCookie.getValue()).getBody();
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("tenant", String.class)).isEqualTo("A");
    }
}
