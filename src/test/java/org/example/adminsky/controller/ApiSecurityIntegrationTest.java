package org.example.adminsky.controller;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import org.example.adminsky.configuration.JwtConfig;
import org.example.adminsky.configuration.SecurityConfig;
import org.example.adminsky.exception.ApiAccessDeniedHandler;
import org.example.adminsky.exception.ApiAuthenticationEntryPoint;
import org.example.adminsky.exception.ApiErrorWriter;
import org.example.adminsky.service.AppService;
import org.example.adminsky.util.ConstraintErrorResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AppController.class)
@Import({SecurityConfig.class, JwtConfig.class, ApiErrorWriter.class,
        ApiAuthenticationEntryPoint.class, ApiAccessDeniedHandler.class})
class ApiSecurityIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtEncoder jwtEncoder;

    @MockBean
    AppService appService;

    @MockBean
    ConstraintErrorResolver constraintErrorResolver;

    @Test
    void protectedEndpointWithoutTokenReturnsJson401() throws Exception {
        mockMvc.perform(get("/apps"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Bearer"))
                .andExpect(jsonPath("$.code").value(1601))
                .andExpect(jsonPath("$.message").value("Authentication is required"));
    }

    @Test
    void tamperedTokenReturnsJson401() throws Exception {
        String token = issueToken(Instant.now().plusSeconds(300));
        String tamperedToken = tamperSignature(token);

        mockMvc.perform(get("/apps").header("Authorization", "Bearer " + tamperedToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(1601));
    }

    @Test
    void expiredTokenReturnsJson401() throws Exception {
        String token = issueToken(Instant.now().minusSeconds(300));

        mockMvc.perform(get("/apps").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(1601));
    }

    @Test
    void validJwtAuthenticatesAndMapsRoleClaim() throws Exception {
        String token = issueToken(Instant.now().plusSeconds(300));

        mockMvc.perform(get("/apps").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(authenticated().withAuthorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    private String issueToken(Instant expiresAt) {
        Instant issuedAt = expiresAt.isBefore(Instant.now()) ? expiresAt.minusSeconds(300) : Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("adminsky")
                .subject("test-admin")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("roles", List.of("ADMIN"))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    private String tamperSignature(String token) {
        int signatureStart = token.lastIndexOf('.') + 1;
        char firstSignatureCharacter = token.charAt(signatureStart);
        char replacement = firstSignatureCharacter == 'A' ? 'B' : 'A';
        return token.substring(0, signatureStart) + replacement + token.substring(signatureStart + 1);
    }
}
