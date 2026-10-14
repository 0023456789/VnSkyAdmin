package org.example.adminsky.service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.example.adminsky.configuration.JwtProperties;
import org.example.adminsky.entity.StaffUser;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtEncoder encoder;
    private final JwtProperties properties;
    private final Clock clock;

    public JwtService(JwtEncoder encoder, JwtProperties properties, Clock clock) {
        this.encoder = encoder;
        this.properties = properties;
        this.clock = clock;
    }

    public Jwt issueAccessToken(StaffUser user) {
        Instant issuedAt = clock.instant();
        var claims = JwtClaimsSet.builder().issuer(properties.issuer()).subject(user.getUsername())
                .claim("uid", user.getId())
                .claim("roles", user.getRoles().stream().map(role -> role.getCode()).sorted().toList())
                .id(UUID.randomUUID().toString()).issuedAt(issuedAt).expiresAt(issuedAt.plus(properties.accessTtl()))
                .build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims));
    }
}
