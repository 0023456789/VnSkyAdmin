package org.example.adminsky.configuration;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class JwtConfig {
    @Bean
    SecretKey jwtSecretKey(JwtProperties properties) {
        byte[] secret = properties.secret().getBytes(StandardCharsets.UTF_8);
        if (secret.length < 32) throw new IllegalStateException("JWT_SECRET must contain at least 32 bytes");
        if (properties.accessTtl() == null || properties.accessTtl().isZero() || properties.accessTtl().isNegative()) {
            throw new IllegalStateException("JWT access TTL must be positive");
        }
        return new SecretKeySpec(secret, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey jwtSecretKey, JwtProperties properties) {
        var decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS256).build();
        var issuerValidator = (org.springframework.security.oauth2.core.OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt>)
                jwt -> properties.issuer().equals(jwt.getClaimAsString("iss"))
                        ? OAuth2TokenValidatorResult.success()
                        : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid issuer", null));
        decoder.setJwtValidator(new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(), issuerValidator));
        return decoder;
    }
}
