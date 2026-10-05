package org.example.adminsky.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.adminsky.dto.request.AppCreationRequest;
import org.example.adminsky.dto.request.AppUpdateRequest;
import org.example.adminsky.dto.request.StatusRequest;
import org.example.adminsky.dto.response.ApiResponse;
import org.example.adminsky.dto.response.AppResponse;
import org.example.adminsky.dto.response.PageResponse;
import org.example.adminsky.service.AppService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apps")
@Tag(name = "Apps")
@RequiredArgsConstructor
public class AppController {
    private final AppService appService;

    @GetMapping
    ApiResponse<PageResponse<AppResponse>> getApps(@RequestParam(name = "isActive", required = false) Boolean active,
                                                   @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size,
                                                   @RequestParam(required = false) String sort) {
        return ApiResponse.<PageResponse<AppResponse>>builder().result(appService.getApps(active, page, size, sort)).build();
    }

    @PostMapping
    ApiResponse<AppResponse> createApp(@RequestBody @Valid AppCreationRequest request) {
        return ApiResponse.<AppResponse>builder().result(appService.createApp(request)).build();
    }

    @PutMapping("/{appId}")
    ApiResponse<AppResponse> updateApp(@PathVariable Long appId, @RequestBody @Valid AppUpdateRequest request) {
        return ApiResponse.<AppResponse>builder().result(appService.updateApp(appId, request)).build();
    }

    @PatchMapping("/{appId}/status")
    ApiResponse<AppResponse> updateAppStatus(@PathVariable Long appId, @RequestBody @Valid StatusRequest request) {
        return ApiResponse.<AppResponse>builder().result(appService.updateAppStatus(appId, request.getActive())).build();
    }

    @DeleteMapping("/{appId}")
    ApiResponse<String> deleteApp(@PathVariable Long appId) {
        return ApiResponse.<String>builder().result(appService.deleteApp(appId)).build();
    }
}
