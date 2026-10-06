package org.example.adminsky.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.adminsky.dto.request.SubscriptionCreationRequest;
import org.example.adminsky.dto.response.ApiResponse;
import org.example.adminsky.dto.response.PageResponse;
import org.example.adminsky.dto.response.SubscriptionResponse;
import org.example.adminsky.enums.SubscriptionStatus;
import org.example.adminsky.service.SubscriptionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    @Operation(summary = "Create a subscription; retries with the same idempotency key return the original result")
    ApiResponse<SubscriptionResponse> createSubscription(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody @Valid SubscriptionCreationRequest request) {
        return ApiResponse.<SubscriptionResponse>builder()
                .result(subscriptionService.createSubscription(idempotencyKey, request))
                .build();
    }

    @GetMapping
    @Operation(summary = "List subscriptions with optional MSISDN and status filters")
    ApiResponse<PageResponse<SubscriptionResponse>> getSubscriptions(
            @RequestParam(required = false) String msisdn,
            @RequestParam(required = false) SubscriptionStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        return ApiResponse.<PageResponse<SubscriptionResponse>>builder()
                .result(subscriptionService.getSubscriptions(msisdn, status, page, size, sort))
                .build();
    }

    @GetMapping("/{subscriptionId}")
    @Operation(summary = "Get subscription details")
    ApiResponse<SubscriptionResponse> getSubscription(@PathVariable Long subscriptionId) {
        return ApiResponse.<SubscriptionResponse>builder()
                .result(subscriptionService.getSubscription(subscriptionId))
                .build();
    }
}
