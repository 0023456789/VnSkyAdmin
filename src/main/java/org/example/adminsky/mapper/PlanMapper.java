package org.example.adminsky.mapper;

import org.example.adminsky.dto.request.AppQuotaRequest;
import org.example.adminsky.dto.request.BonusRequest;
import org.example.adminsky.dto.request.PlanCreationRequest;
import org.example.adminsky.dto.request.PlanUpdateRequest;
import org.example.adminsky.dto.response.PlanResponse;
import org.example.adminsky.dto.response.BonusResponse;
import org.example.adminsky.dto.response.AppQuotaResponse;
import org.example.adminsky.dto.response.PlanSummaryResponse;
import org.example.adminsky.entity.Plan;
import org.example.adminsky.entity.PlanAppQuota;
import org.example.adminsky.entity.PlanFirstCycleBonus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import java.math.BigDecimal;
import org.example.adminsky.util.CodeNormalizer;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = CodeNormalizer.class)
public interface PlanMapper {
    @Mapping(target = "code", expression = "java(CodeNormalizer.normalize(request.getCode()))")
    @Mapping(target = "name", expression = "java(request.getName().trim())")
    @Mapping(target = "bonuses", ignore = true)
    @Mapping(target = "appQuotas", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Plan toPlan(PlanCreationRequest request);

    @Mapping(target = "code", expression = "java(CodeNormalizer.normalize(request.getCode()))")
    @Mapping(target = "name", expression = "java(request.getName().trim())")
    @Mapping(target = "bonuses", ignore = true)
    @Mapping(target = "appQuotas", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updatePlan(@MappingTarget Plan plan, PlanUpdateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plan", ignore = true)
    PlanFirstCycleBonus toEntity(BonusRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plan", ignore = true)
    @Mapping(target = "app", ignore = true)
    PlanAppQuota toEntity(AppQuotaRequest request);

    PlanResponse toPlanResponse(Plan plan);
    BonusResponse toBonusResponse(PlanFirstCycleBonus bonus);

    @Mapping(target = "appId", source = "app.id")
    @Mapping(target = "appCode", source = "app.code")
    @Mapping(target = "appName", source = "app.name")
    AppQuotaResponse toAppQuotaResponse(PlanAppQuota quota);

    @Mapping(target = "appQuotaCount", source = "appQuotaCount")
    @Mapping(target = "hasBonus", source = "hasBonus")
    PlanSummaryResponse toPlanSummary(Plan plan, long appQuotaCount, boolean hasBonus);

    default BigDecimal map(Long value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }

    default Short map(Integer value) {
        return value == null ? null : value.shortValue();
    }
}
