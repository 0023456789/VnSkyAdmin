package org.example.adminsky.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import org.example.adminsky.configuration.SecurityConfig;
import org.example.adminsky.dto.request.LoginRequest;
import org.example.adminsky.dto.response.TokenResponse;
import org.example.adminsky.exception.ApiAccessDeniedHandler;
import org.example.adminsky.exception.ApiAuthenticationEntryPoint;
import org.example.adminsky.exception.ApiErrorWriter;
import org.example.adminsky.service.AuthService;
import org.example.adminsky.support.WithAdmin;
import org.example.adminsky.util.ConstraintErrorResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, ApiErrorWriter.class, ApiAuthenticationEntryPoint.class,
        ApiAccessDeniedHandler.class})
class AuthControllerTest {
    @MockBean
    AuthService authService;
    @MockBean
    ConstraintErrorResolver constraintErrorResolver;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithAdmin
    void loginReturnsWrappedBearerToken() throws Exception {
        when(authService.login(org.mockito.ArgumentMatchers.any(LoginRequest.class)))
                .thenReturn(TokenResponse.bearer("signed-token", 900));

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.accessToken").value("signed-token"))
                .andExpect(jsonPath("$.result.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.result.expiresIn").value(900));
    }

    @Test
    void loginRequiresUsernameAndPassword() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\" \",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
