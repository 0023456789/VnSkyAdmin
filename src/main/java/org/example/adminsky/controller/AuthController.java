package org.example.adminsky.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.adminsky.dto.request.LoginRequest;
import org.example.adminsky.dto.response.ApiResponse;
import org.example.adminsky.dto.response.TokenResponse;
import org.example.adminsky.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }
}
