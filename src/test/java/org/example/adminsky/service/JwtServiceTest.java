package org.example.adminsky.service;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import javax.crypto.spec.SecretKeySpec;
import org.example.adminsky.configuration.JwtProperties;
import org.example.adminsky.entity.StaffRole;
import org.example.adminsky.entity.StaffUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

class JwtServiceTest {
    private static final byte[] SECRET = "01234567890123456789012345678901".getBytes();
    private final JwtProperties properties = new JwtProperties(new String(SECRET), Duration.ofMinutes(15), "adminsky");
    private final JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(new SecretKeySpec(SECRET, "HmacSHA256")));
    private final JwtDecoder decoder = NimbusJwtDecoder.withSecretKey(new SecretKeySpec(SECRET, "HmacSHA256")).build();

    @Test
    void issuesVerifiableTokenWithExpectedClaims() {
        var service = new JwtService(encoder, properties, Clock.systemUTC());
        var user = StaffUser.builder().id(7L).username("admin").roles(Set.of(
                StaffRole.builder().code("VIEWER").build(), StaffRole.builder().code("ADMIN").build())).build();

        var jwt = service.issueAccessToken(user);
        var decoded = decoder.decode(jwt.getTokenValue());
        assertThat(decoded.getSubject()).isEqualTo("admin");
        assertThat((Long) decoded.getClaims().get("uid")).isEqualTo(7L);
        assertThat(decoded.getClaimAsStringList("roles")).containsExactly("ADMIN", "VIEWER");
        assertThat(decoded.getClaimAsString("iss")).isEqualTo("adminsky");
    }
}
