package org.example.adminsky.mapper;

import org.example.adminsky.dto.request.AppCreationRequest;
import org.example.adminsky.dto.request.AppUpdateRequest;
import org.example.adminsky.dto.response.AppResponse;
import org.example.adminsky.entity.App;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        builder = @Builder(disableBuilder = true))
public interface AppMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "code", expression = "java(org.example.adminsky.util.CodeNormalizer.normalize(request.getCode()))")
    @Mapping(target = "name", expression = "java(request.getName().trim())")
    @Mapping(target = "active", expression = "java(request.getActive() == null || request.getActive())")
    App toApp(AppCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "code", expression = "java(org.example.adminsky.util.CodeNormalizer.normalize(request.getCode()))")
    @Mapping(target = "name", expression = "java(request.getName().trim())")
    @Mapping(target = "active", ignore = true)
    void updateApp(@MappingTarget App app, AppUpdateRequest request);

    @Mapping(target = "warnings", ignore = true)
    AppResponse toAppResponse(App app);
}
