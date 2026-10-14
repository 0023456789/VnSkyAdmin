package org.example.adminsky.service;

import java.util.Locale;
import org.example.adminsky.configuration.JwtProperties;
import org.example.adminsky.dto.request.LoginRequest;
import org.example.adminsky.dto.response.TokenResponse;
import org.example.adminsky.entity.StaffUser;
import org.example.adminsky.exception.AppException;
import org.example.adminsky.exception.ErrorCode;
import org.example.adminsky.repository.StaffUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    // A fixed valid BCrypt hash keeps the missing-user path doing password-hash work too.
    private static final String DUMMY_PASSWORD_HASH =
            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    private final StaffUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(StaffUserRepository users, PasswordEncoder passwordEncoder,
            JwtService jwtService, JwtProperties jwtProperties) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    public TokenResponse login(LoginRequest request) {
        String username = request.getUsername().trim().toLowerCase(Locale.ROOT);
        StaffUser user = users.findWithRolesByUsername(username).orElse(null);
        String passwordHash = user == null ? DUMMY_PASSWORD_HASH : user.getPasswordHash();
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), passwordHash);
        if (user == null || !user.isEnabled() || !passwordMatches) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        return TokenResponse.bearer(jwtService.issueAccessToken(user).getTokenValue(),
                jwtProperties.accessTtl().toSeconds());
    }
}
