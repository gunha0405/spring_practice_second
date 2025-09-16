package com.example.user;

import java.util.Optional;
import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.DataNotFoundException;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final MailService mailService;

    public SiteUser create(String username, String email, String password, String customerId) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCustomerId(customerId);
        this.userRepository.save(user);
        return user;
    }
    
    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
    
 // 임시 비밀번호 발송
    public String sendTemporaryPassword(String email) throws MessagingException {
        Optional<SiteUser> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            throw new IllegalArgumentException("등록된 이메일이 없습니다.");
        }

        SiteUser user = optUser.get();

        // 임시 비밀번호 생성
        String tempPassword = generateTempPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        // 이메일 전송
        mailService.sendMail(user.getEmail(), "임시 비밀번호 발급",
                "임시 비밀번호는 [" + tempPassword + "] 입니다. 로그인 후 반드시 변경해주세요.");

        return "임시 비밀번호가 이메일로 발송되었습니다.";
    }

    // 현재 비밀번호 확인 후 변경
    public void changePassword(String username, String currentPassword,
                               String newPassword, String confirmPassword) {
        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 임시 비밀번호 생성 유틸
    private String generateTempPassword() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
