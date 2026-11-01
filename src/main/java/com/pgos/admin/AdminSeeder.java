package com.pgos.admin;

import com.pgos.user.AccountStatus;
import com.pgos.user.Role;
import com.pgos.user.User;
import com.pgos.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.phone}")
    private String phone;

    @Value("${app.admin.password}")
    private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (password == null || password.length() < 8) {
            return; // no admin configured
        }
        if (userRepository.existsByEmail(email)) {
            return; // already seeded
        }
        User admin = new User("Platform Admin", phone, email, Role.ADMIN, AccountStatus.ACTIVE);
        admin.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(admin);
    }
}