package org.example.adminsky.configuration;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
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
@ConditionalOnProperty(name = "app.security.bootstrap.enabled", matchIfMissing = true)
public class AdminInitializer implements ApplicationRunner {
    private final StaffUserRepository users;
    private final StaffRoleRepository roles;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminInitializer(StaffUserRepository users, StaffRoleRepository roles, PasswordEncoder passwordEncoder,
            @Value("${app.security.bootstrap.username:admin}") String username,
            @Value("${app.security.bootstrap.password:}") String password) {
        this.users = users;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (users.count() > 0) return;
        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
        int passwordBytes = password.getBytes(StandardCharsets.UTF_8).length;
        if (!normalizedUsername.matches("[a-z0-9._-]{3,50}") || passwordBytes < 10 || passwordBytes > 72
                || !password.matches(".*[A-Za-z].*") || !password.matches(".*[0-9].*")
                || password.equalsIgnoreCase(normalizedUsername)) {
            throw new IllegalStateException("Bootstrap admin credentials are missing or do not meet password policy");
        }
        var adminRole = roles.findByCode("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role is missing; run the staff auth migration"));
        users.save(StaffUser.builder().username(normalizedUsername).passwordHash(passwordEncoder.encode(password))
                .fullName("Administrator").enabled(true).roles(Set.of(adminRole)).build());
    }
}
