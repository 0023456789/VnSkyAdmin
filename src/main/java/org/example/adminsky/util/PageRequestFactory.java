package org.example.adminsky.util;

import org.example.adminsky.exception.AppException;
import org.example.adminsky.exception.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Map;

public final class PageRequestFactory {
    private PageRequestFactory() {}

    public static Pageable of(int page, int size, String sort, Map<String, String> allowlist) {
        if (page < 0 || size < 1 || size > 100) throw new AppException(ErrorCode.INVALID_PAGE_REQUEST);
        String[] parts = (sort == null || sort.isBlank() ? "createdAt,desc" : sort).split(",", -1);
        if (parts.length != 2 || !allowlist.containsKey(parts[0]) || !isDirection(parts[1])) {
            throw new AppException(ErrorCode.INVALID_PAGE_REQUEST);
        }
        return PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(parts[1]), allowlist.get(parts[0])));
    }

    private static boolean isDirection(String value) {
        return "asc".equalsIgnoreCase(value) || "desc".equalsIgnoreCase(value);
    }
}
