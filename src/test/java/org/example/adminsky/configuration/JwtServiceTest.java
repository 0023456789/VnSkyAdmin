package org.example.adminsky.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import javax.crypto.SecretKey;
import org.example.adminsky.entity.StaffRole;
import org.example.adminsky.entity.StaffUser;
import org.example.adminsky.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;

class JwtServiceTest {
    private static final String SECRET = "01234567890123456789012345678901";

    @Test
    void issuesAndDecodesHs256TokenWithExpectedClaims() {
        Instant now = Instant.now().minusSeconds(10).truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        Clock clock = Clock.fixed(now, ZoneOffset.UTC);
        JwtProperties properties = new JwtProperties(SECRET, Duration.ofMinutes(15), "adminsky");
        JwtConfig config = new JwtConfig();
        SecretKey key = config.jwtSecretKey(properties);
        JwtEncoder encoder = config.jwtEncoder(key);
        JwtDecoder decoder = config.jwtDecoder(key, properties);
        var user = StaffUser.builder().id(7L).username("admin").roles(Set.of(
                StaffRole.builder().code("VIEWER").build(), StaffRole.builder().code("ADMIN").build())).build();

        String token = new JwtService(encoder, properties, clock).issueAccessToken(user).getTokenValue();
        Jwt decoded = decoder.decode(token);

        assertThat(decoded.getSubject()).isEqualTo("admin");
        assertThat(decoded.getClaims().get("uid")).isEqualTo(7L);
        assertThat(decoded.getClaimAsStringList("roles")).containsExactly("ADMIN", "VIEWER");
        assertThat(decoded.getClaimAsString("iss")).isEqualTo("adminsky");
        assertThat(decoded.getIssuedAt()).isEqualTo(now);
        assertThat(decoded.getExpiresAt()).isEqualTo(now.plus(Duration.ofMinutes(15)));
        assertThat(decoded.getId()).isNotBlank();

        SecretKey wrongKey = config.jwtSecretKey(new JwtProperties(
                "abcdef0123456789abcdef0123456789", Duration.ofMinutes(15), "adminsky"));
        assertThatThrownBy(() -> config.jwtDecoder(wrongKey, properties).decode(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsExpiredTokensAndWrongIssuer() {
        JwtConfig config = new JwtConfig();
        JwtProperties expected = new JwtProperties(SECRET, Duration.ofMinutes(15), "adminsky");
        SecretKey key = config.jwtSecretKey(expected);
        JwtDecoder decoder = config.jwtDecoder(key, expected);
        var user = StaffUser.builder().id(1L).username("admin").roles(Set.of()).build();
        JwtProperties wrongIssuer = new JwtProperties(SECRET, Duration.ofMinutes(15), "wrong-issuer");
        JwtService wrongIssuerService = new JwtService(config.jwtEncoder(key), wrongIssuer, Clock.systemUTC());

        assertThatThrownBy(() -> decoder.decode(wrongIssuerService.issueAccessToken(user).getTokenValue()))
                .isInstanceOf(JwtValidationException.class);

        JwtService expiredService = new JwtService(config.jwtEncoder(key), expected,
                Clock.fixed(Instant.now().minus(Duration.ofHours(1)), ZoneOffset.UTC));
        assertThatThrownBy(() -> decoder.decode(expiredService.issueAccessToken(user).getTokenValue()))
                .isInstanceOf(JwtValidationException.class);
    }

    @Test
    void requiresAtLeast32SecretBytes() {
        assertThatThrownBy(() -> new JwtConfig().jwtSecretKey(
                new JwtProperties("too-short", Duration.ofMinutes(15), "adminsky")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("32 bytes");
    }
}
