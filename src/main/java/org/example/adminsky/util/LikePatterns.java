package org.example.adminsky.util;

/** Thoát ký tự wildcard để nội dung tìm kiếm được hiểu theo nghĩa đen trong truy vấn LIKE. */
public final class LikePatterns {

    private LikePatterns() {
    }

    public static String escape(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
