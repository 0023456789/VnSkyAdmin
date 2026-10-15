package org.example.adminsky.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

class ApiSecurityErrorHandlerTest {
    private ApiErrorWriter apiErrorWriter;

    @BeforeEach
    void setUp() {
        apiErrorWriter = new ApiErrorWriter(new ObjectMapper());
    }

    @Test
    void authenticationEntryPointWritesJson401AndBearerChallenge() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new ApiAuthenticationEntryPoint(apiErrorWriter).commence(new MockHttpServletRequest(), response,
                new BadCredentialsException("invalid token"));

        var body = new ObjectMapper().readTree(response.getContentAsString(StandardCharsets.UTF_8));
        assertEquals(401, response.getStatus());
        assertTrue(MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.parseMediaType(response.getContentType())));
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertEquals("Bearer", response.getHeader("WWW-Authenticate"));
        assertEquals(ErrorCode.UNAUTHENTICATED.getCode(), body.get("code").asInt());
        assertEquals(ErrorCode.UNAUTHENTICATED.getMessage(), body.get("message").asText());
    }

    @Test
    void accessDeniedHandlerWritesJson403WithoutExposingExceptionMessage() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new ApiAccessDeniedHandler(apiErrorWriter).handle(new MockHttpServletRequest(), response,
                new AccessDeniedException("internal authorization details"));

        var body = new ObjectMapper().readTree(response.getContentAsString(StandardCharsets.UTF_8));
        assertEquals(403, response.getStatus());
        assertTrue(MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.parseMediaType(response.getContentType())));
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertEquals(ErrorCode.UNAUTHORIZED.getCode(), body.get("code").asInt());
        assertEquals(ErrorCode.UNAUTHORIZED.getMessage(), body.get("message").asText());
        assertEquals(false, body.has("result"));
    }
}
