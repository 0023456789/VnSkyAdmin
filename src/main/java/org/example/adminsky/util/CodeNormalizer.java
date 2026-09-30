package org.example.adminsky.util;

import java.util.Locale;

public final class CodeNormalizer {
    private CodeNormalizer() {}
    public static String normalize(String code) {
        return code == null ? null : code.trim().toUpperCase(Locale.ROOT);
    }
}
