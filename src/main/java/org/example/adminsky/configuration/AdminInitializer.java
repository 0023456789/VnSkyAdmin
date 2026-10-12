package org.example.adminsky.configuration;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.adminsky.entity.StaffUser;
import org.example.adminsky.repository.StaffRoleRepository;
import org.example.adminsky.repository.StaffUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.bootstrap.enabled", matchIfMissing = true)
public class AdminInitializer implements ApplicationRunner {
    private final StaffUserRepository users;
    private final StaffRoleRepository roles;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.security.bootstrap.username:admin}") private String username;
    @Value("${app.security.bootstrap.password:}") private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (users.count() != 0) return;
        String normalizedUsername = username.trim().toLowerCase();
        int passwordBytes = password.getBytes(StandardCharsets.UTF_8).length;
        if (!normalizedUsername.matches("[a-z0-9._-]{3,50}") || passwordBytes < 10 || passwordBytes > 72
                || !password.matches(".*[A-Za-z].*") || !password.matches(".*[0-9].*")
                || password.equalsIgnoreCase(normalizedUsername)) {
            throw new IllegalStateException("Bootstrap admin credentials are missing or do not meet policy");
        }
        var adminRole = roles.findByCode("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role is missing from the database"));
        users.save(StaffUser.builder().username(normalizedUsername).passwordHash(passwordEncoder.encode(password))
                .fullName("Administrator").enabled(true).roles(Set.of(adminRole)).build());
    }
}
