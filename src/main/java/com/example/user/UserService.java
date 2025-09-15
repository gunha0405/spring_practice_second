package com.example.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password, String customerId) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCustomerId(customerId);
        this.userRepository.save(user);
        return user;
    }

}
