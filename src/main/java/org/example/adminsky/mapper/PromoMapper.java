package org.example.adminsky.mapper;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.adminsky.dto.request.PromoCreationRequest;
import org.example.adminsky.dto.request.PromoUpdateRequest;
import org.example.adminsky.dto.response.PromoResponse;
import org.example.adminsky.entity.Plan;
import org.example.adminsky.entity.PromoCode;
import org.example.adminsky.util.CodeNormalizer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        builder = @Builder(disableBuilder = true), imports = CodeNormalizer.class)
public interface PromoMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "code", expression = "java(CodeNormalizer.normalize(request.getCode()))")
    @Mapping(target = "maxDiscountAmount", source = "maxDiscountAmount")
    @Mapping(target = "minOrderAmount", source = "minOrderAmount", defaultValue = "0")
    @Mapping(target = "usedCount", constant = "0")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "appliesToAllPlans", source = "appliesToAllPlans")
    @Mapping(target = "plans", ignore = true)
    PromoCode toPromo(PromoCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "code", expression = "java(CodeNormalizer.normalize(request.getCode()))")
    @Mapping(target = "minOrderAmount", source = "minOrderAmount", defaultValue = "0")
    @Mapping(target = "usedCount", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "appliesToAllPlans", source = "appliesToAllPlans")
    @Mapping(target = "plans", ignore = true)
    void updatePromo(@MappingTarget PromoCode promo, PromoUpdateRequest request);

    @Mapping(target = "planIds", expression = "java(planIds(promo))")
    PromoResponse toPromoResponse(PromoCode promo);

    @Mapping(target = "planIds", ignore = true)
    PromoResponse toPromoListItem(PromoCode promo);

    default BigDecimal map(Long value) { return value == null ? null : BigDecimal.valueOf(value); }
    default Long map(BigDecimal value) { return value == null ? null : value.longValueExact(); }
    default Set<Long> planIds(PromoCode promo) {
        return promo.getPlans().stream().map(Plan::getId).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
