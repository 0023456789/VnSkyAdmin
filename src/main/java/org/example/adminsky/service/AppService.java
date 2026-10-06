package org.example.adminsky.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.constant.SortFields;
import org.example.adminsky.dto.request.AppCreationRequest;
import org.example.adminsky.dto.request.AppUpdateRequest;
import org.example.adminsky.dto.response.AppResponse;
import org.example.adminsky.dto.response.PageResponse;
import org.example.adminsky.enums.AppWarningCode;
import org.example.adminsky.entity.App;
import org.example.adminsky.exception.AppException;
import org.example.adminsky.exception.ErrorCode;
import org.example.adminsky.mapper.AppMapper;
import org.example.adminsky.repository.AppRepository;
import org.example.adminsky.repository.PlanAppQuotaRepository;
import org.example.adminsky.util.CodeNormalizer;
import org.example.adminsky.util.PageRequestFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppService {
    AppRepository appRepository;
    PlanAppQuotaRepository planAppQuotaRepository;
    AppMapper appMapper;

    @Transactional(readOnly = true)
    public PageResponse<AppResponse> getApps(Boolean active, Integer page, Integer size, String sort) {
        Pageable pageable = PageRequestFactory.of(page == null ? 0 : page, size == null ? 20 : size,
                sort == null ? SortFields.DEFAULT_SORT : sort, SortFields.APP);
        Page<App> apps = active == null ? appRepository.findAll(pageable) : appRepository.findByActive(active, pageable);
        PageResponse<AppResponse> response = PageResponse.from(apps.map(appMapper::toAppResponse));
        List<Long> appIds = response.getContent().stream().map(AppResponse::getId).toList();
        if (appIds.isEmpty()) return response;

        Map<Long, List<String>> planCodesByAppId = new HashMap<>();
        for (PlanAppQuotaRepository.AppPlanCodeView row : planAppQuotaRepository.findPlanCodesByAppIds(appIds)) {
            planCodesByAppId.computeIfAbsent(row.getAppId(), ignored -> new ArrayList<>()).add(row.getPlanCode());
        }
        response.getContent().forEach(app -> app.setPlanCodes(planCodesByAppId.getOrDefault(app.getId(), List.of())));
        return response;
    }

    @Transactional
    public AppResponse createApp(AppCreationRequest request) {
        String code = CodeNormalizer.normalize(request.getCode());
        if (appRepository.existsByCode(code)) throw new AppException(ErrorCode.APP_CODE_EXISTED);
        App app = appMapper.toApp(request);
        return appMapper.toAppResponse(appRepository.saveAndFlush(app));
    }

    @Transactional
    public AppResponse updateApp(Long id, AppUpdateRequest request) {
        App app = findOrThrow(id);
        String code = CodeNormalizer.normalize(request.getCode());
        if (appRepository.existsByCodeAndIdNot(code, id)) throw new AppException(ErrorCode.APP_CODE_EXISTED);
        appMapper.updateApp(app, request);
        return appMapper.toAppResponse(appRepository.saveAndFlush(app));
    }

    @Transactional
    public AppResponse updateAppStatus(Long id, boolean active) {
        App app = findOrThrow(id);
        app.setActive(active);
        AppResponse response = appMapper.toAppResponse(app);
        if (!active && planAppQuotaRepository.existsByAppId(id)) {
            response.setWarnings(java.util.List.of(AppWarningCode.APP_STILL_ASSIGNED_TO_PLANS));
        }
        return response;
    }

    @Transactional
    public String deleteApp(Long id) {
        App app = findOrThrow(id);
        if (planAppQuotaRepository.existsByAppId(id)) throw new AppException(ErrorCode.APP_IN_USE);
        appRepository.delete(app);
        appRepository.flush();
        return "App has been deleted";
    }

    private App findOrThrow(Long id) {
        return appRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.APP_NOT_FOUND));
    }
//    private App justNervous(App deployedApp) {
//        int inexperiences = 10;
//        boolean FirstDemo = true;
//        Date time  = new Date('tomorrow');
//        if (FirstDemoFail) {
//           Set problems = {learnMore, noWork, continueWithPersonalProject, bored};
//        }
//        else{
//            Set benefits = {learnMore, haveProperWork, aChanceToWorkWithOther, gettingScoldedButLessWorkALone}
//        }
//    }
}
