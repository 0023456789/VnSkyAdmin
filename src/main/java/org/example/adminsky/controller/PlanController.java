package org.example.adminsky.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PlanController {
    PlanService planService;

    @PostMapping
    ApiResponse<PlanResponse> createPlan(@RequestBody @Valid PlanCreationRequest request) {
        return ApiResponse.<PlanResponse>builder().result(planService.createPlan(request)).build();
    }

    @GetMapping
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
    ApiResponse<PlanResponse> getPlan(@PathVariable Long planId) {
        return ApiResponse.<PlanResponse>builder().result(planService.getPlan(planId)).build();
    }

    @PutMapping("/{planId}")
    ApiResponse<PlanResponse> updatePlan(@PathVariable Long planId,
                                         @RequestBody @Valid PlanUpdateRequest request) {
        return ApiResponse.<PlanResponse>builder().result(planService.updatePlan(planId, request)).build();
    }

    @PatchMapping("/{planId}/status")
    ApiResponse<PlanResponse> updatePlanStatus(@PathVariable Long planId,
                                                @RequestBody @Valid PlanStatusRequest request) {
        return ApiResponse.<PlanResponse>builder()
                .result(planService.updatePlanStatus(planId, request.getActive())).build();
    }

    @DeleteMapping("/{planId}")
    ApiResponse<String> deletePlan(@PathVariable Long planId) {
        return ApiResponse.<String>builder().result(planService.deletePlan(planId)).build();
    }
}
