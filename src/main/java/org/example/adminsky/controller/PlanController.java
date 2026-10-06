package org.example.adminsky.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.adminsky.dto.request.PlanCreationRequest;
import org.example.adminsky.dto.request.PlanStatusRequest;
import org.example.adminsky.dto.request.PlanUpdateRequest;
import org.example.adminsky.dto.response.ApiResponse;
import org.example.adminsky.dto.response.PageResponse;
import org.example.adminsky.dto.response.PlanResponse;
import org.example.adminsky.dto.response.PlanSummaryResponse;
import org.example.adminsky.service.PlanService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plans")
@Tag(name = "Plans")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PlanController {
    PlanService planService;

    @PostMapping
    @Operation(summary = "Create a plan with its bonus and app quota configuration")
    ApiResponse<PlanResponse> createPlan(@RequestBody @Valid PlanCreationRequest request) {
        return ApiResponse.<PlanResponse>builder().result(planService.createPlan(request)).build();
    }

    @GetMapping
    @Operation(summary = "List plans with optional active, keyword, and duration filters")
    ApiResponse<PageResponse<PlanSummaryResponse>> getPlans(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer durationMonths,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        return ApiResponse.<PageResponse<PlanSummaryResponse>>builder()
                .result(planService.getPlans(isActive, keyword, durationMonths, page, size, sort)).build();
    }

    @GetMapping("/{planId}")
    @Operation(summary = "Get plan details including bonuses and app quotas")
    ApiResponse<PlanResponse> getPlan(@PathVariable Long planId) {
        return ApiResponse.<PlanResponse>builder().result(planService.getPlan(planId)).build();
    }

    @PutMapping("/{planId}")
    @Operation(summary = "Replace plan fields, bonuses, and app quotas")
    ApiResponse<PlanResponse> updatePlan(@PathVariable Long planId,
                                         @RequestBody @Valid PlanUpdateRequest request) {
        return ApiResponse.<PlanResponse>builder().result(planService.updatePlan(planId, request)).build();
    }

    @PatchMapping("/{planId}/status")
    @Operation(summary = "Activate or deactivate a plan")
    ApiResponse<PlanResponse> updatePlanStatus(@PathVariable Long planId,
                                               @RequestBody @Valid PlanStatusRequest request) {
        return ApiResponse.<PlanResponse>builder()
                .result(planService.updatePlanStatus(planId, request.getActive())).build();
    }

    @DeleteMapping("/{planId}")
    @Operation(summary = "Delete a plan; referenced plans cannot be deleted")
    ApiResponse<String> deletePlan(@PathVariable Long planId) {
        return ApiResponse.<String>builder().result(planService.deletePlan(planId)).build();
    }
}
