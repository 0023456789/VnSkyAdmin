package org.example.adminsky.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.adminsky.dto.request.PromoCreationRequest;
import org.example.adminsky.dto.request.PromoUpdateRequest;
import org.example.adminsky.dto.request.PromoValidateRequest;
import org.example.adminsky.dto.request.StatusRequest;
import org.example.adminsky.dto.response.ApiResponse;
import org.example.adminsky.dto.response.PageResponse;
import org.example.adminsky.dto.response.PromoResponse;
import org.example.adminsky.dto.response.PromoValidateResponse;
import org.example.adminsky.service.PromoCodeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/promo-codes")
@Tag(name = "Promo codes")
@RequiredArgsConstructor
public class PromoCodeController {
    private final PromoCodeService promoCodeService;

    @PostMapping
    ApiResponse<PromoResponse> createPromo(@RequestBody @Valid PromoCreationRequest request) {
        return ApiResponse.<PromoResponse>builder().result(promoCodeService.createPromo(request)).build();
    }

    @GetMapping
    ApiResponse<PageResponse<PromoResponse>> getPromos(@RequestParam(name = "isActive", required = false) Boolean active,
                                                       @RequestParam(required = false) String keyword, @RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) {
        return ApiResponse.<PageResponse<PromoResponse>>builder()
                .result(promoCodeService.getPromos(active, keyword, page, size, sort)).build();
    }

    @GetMapping("/{promoId}")
    ApiResponse<PromoResponse> getPromo(@PathVariable Long promoId) {
        return ApiResponse.<PromoResponse>builder().result(promoCodeService.getPromo(promoId)).build();
    }

    @PutMapping("/{promoId}")
    ApiResponse<PromoResponse> updatePromo(@PathVariable Long promoId, @RequestBody @Valid PromoUpdateRequest request) {
        return ApiResponse.<PromoResponse>builder().result(promoCodeService.updatePromo(promoId, request)).build();
    }

    @PatchMapping("/{promoId}/status")
    ApiResponse<PromoResponse> updatePromoStatus(@PathVariable Long promoId, @RequestBody @Valid StatusRequest request) {
        return ApiResponse.<PromoResponse>builder()
                .result(promoCodeService.updatePromoStatus(promoId, request.getActive())).build();
    }

    @DeleteMapping("/{promoId}")
    ApiResponse<String> deletePromo(@PathVariable Long promoId) {
        return ApiResponse.<String>builder().result(promoCodeService.deletePromo(promoId)).build();
    }

    @PostMapping("/validate")
    @Operation(description = "Advisory validation only; no usage is consumed. When supplied, msisdn is checked against its promo usage limit.")
    ApiResponse<PromoValidateResponse> validatePromo(@RequestBody @Valid PromoValidateRequest request) {
        return ApiResponse.<PromoValidateResponse>builder().result(promoCodeService.validatePromo(request)).build();
    }
}
