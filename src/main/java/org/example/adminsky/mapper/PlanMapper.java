package org.example.adminsky.mapper;

import java.math.BigDecimal;
import org.example.adminsky.dto.request.AppQuotaRequest;
import org.example.adminsky.dto.request.BonusRequest;
import org.example.adminsky.dto.request.PlanCreationRequest;
import org.example.adminsky.dto.request.PlanUpdateRequest;
import org.example.adminsky.dto.response.AppQuotaResponse;
import org.example.adminsky.dto.response.BonusResponse;
import org.example.adminsky.dto.response.PlanResponse;
import org.example.adminsky.dto.response.PlanSummaryResponse;
import org.example.adminsky.entity.Plan;
import org.example.adminsky.entity.PlanAppQuota;
import org.example.adminsky.entity.PlanFirstCycleBonus;
import org.example.adminsky.util.CodeNormalizer;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        builder = @Builder(disableBuilder = true),
        imports = CodeNormalizer.class)
public interface PlanMapper {

    @Mapping(target = "code", expression = "java(CodeNormalizer.normalize(request.getCode()))")
    @Mapping(target = "name", expression = "java(request.getName().trim())")
    @Mapping(target = "voiceMinutes", expression = "java(request.getVoiceMinutes() == null ? 0 : request.getVoiceMinutes())")
    @Mapping(target = "active", expression = "java(request.getActive() == null || request.getActive())")
    @Mapping(target = "bonuses", ignore = true)
    @Mapping(target = "appQuotas", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Plan toPlan(PlanCreationRequest request);

    @Mapping(target = "code", expression = "java(CodeNormalizer.normalize(request.getCode()))")
    @Mapping(target = "name", expression = "java(request.getName().trim())")
    @Mapping(target = "voiceMinutes", expression = "java(request.getVoiceMinutes() == null ? 0 : request.getVoiceMinutes())")
    @Mapping(target = "bonuses", ignore = true)
    @Mapping(target = "appQuotas", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePlan(@MappingTarget Plan plan, PlanUpdateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plan", ignore = true)
    PlanFirstCycleBonus toEntity(BonusRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plan", ignore = true)
    @Mapping(target = "app", ignore = true)
    PlanAppQuota toEntity(AppQuotaRequest request);

    @Mapping(target = "firstCycleBonuses", source = "bonuses")
    PlanResponse toPlanResponse(Plan plan);

    BonusResponse toBonusResponse(PlanFirstCycleBonus bonus);

    @Mapping(target = "appId", source = "app.id")
    @Mapping(target = "appCode", source = "app.code")
    @Mapping(target = "appName", source = "app.name")
    AppQuotaResponse toAppQuotaResponse(PlanAppQuota quota);

    PlanSummaryResponse toPlanSummary(Plan plan);

    default BigDecimal map(Long value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }

    default Long map(BigDecimal value) {
        return value == null ? null : value.longValueExact();
    }

    default Short mapIntegerToShort(Integer value) {
        return value == null ? null : value.shortValue();
    }

    default Integer mapShortToInteger(Short value) {
        return value == null ? null : value.intValue();
    }
}
