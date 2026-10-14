package org.example.adminsky.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Optional;
import org.example.adminsky.configuration.JwtProperties;
import org.example.adminsky.dto.request.LoginRequest;
import org.example.adminsky.entity.StaffUser;
import org.example.adminsky.exception.AppException;
import org.example.adminsky.exception.ErrorCode;
import org.example.adminsky.repository.StaffUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock StaffUserRepository users;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock Jwt jwt;

    @InjectMocks AuthService authService;

    @Test
    void loginNormalizesUsernameAndReturnsBearerToken() {
        StaffUser user = StaffUser.builder().id(7L).username("admin").passwordHash("stored-hash").enabled(true).build();
        when(users.findWithRolesByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "stored-hash")).thenReturn(true);
        when(jwtService.issueAccessToken(user)).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("signed-token");

        var result = new AuthService(users, passwordEncoder, jwtService,
                new JwtProperties("01234567890123456789012345678901", Duration.ofMinutes(15), "adminsky"))
                .login(LoginRequest.builder().username("  ADMIN ").password("secret123").build());

        assertEquals("signed-token", result.accessToken());
        assertEquals("Bearer", result.tokenType());
        assertEquals(900, result.expiresIn());
        verify(users).findWithRolesByUsername("admin");
        verify(jwtService).issueAccessToken(user);
    }

    @Test
    void missingUserStillChecksPasswordAgainstDummyHashAndUsesGenericError() {
        when(users.findWithRolesByUsername("unknown")).thenReturn(Optional.empty());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> authService.login(
                LoginRequest.builder().username("unknown").password("bad-password").build()));

        assertSame(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
        verify(passwordEncoder).matches(anyString(), anyString());
        verifyNoInteractions(jwtService);
    }

    @Test
    void disabledUserUsesTheSameGenericError() {
        StaffUser user = StaffUser.builder().username("admin").passwordHash("stored-hash").enabled(false).build();
        when(users.findWithRolesByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "stored-hash")).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> authService.login(
                LoginRequest.builder().username("admin").password("secret123").build()));

        assertSame(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
        verifyNoInteractions(jwtService);
    }

    @Test
    void incorrectPasswordUsesTheSameGenericError() {
        StaffUser user = StaffUser.builder().username("admin").passwordHash("stored-hash").enabled(true).build();
        when(users.findWithRolesByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad-password", "stored-hash")).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> authService.login(
                LoginRequest.builder().username("admin").password("bad-password").build()));

        assertSame(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
        verifyNoInteractions(jwtService);
    }
}
