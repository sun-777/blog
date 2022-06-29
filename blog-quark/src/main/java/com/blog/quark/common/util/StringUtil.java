package com.blog.quark.common.util;

import java.util.Optional;

public final class StringUtil {
    
    private StringUtil() {}
    
    public static boolean isEmptyOrWhitespaceOnly(Optional<String> optional) {
        return isEmptyOrWhitespaceOnly(optional.isEmpty() ? null : optional.get());
    }
    
    public static boolean isEmptyOrWhitespaceOnly(String str) {
        if (null == str || str.isEmpty()) {
            return true;
        }
        final int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    
    public static boolean hasText(Optional<String> optional) {
        return hasText(optional.isEmpty() ? null : optional.get());
    }
    
    public static boolean hasText(String str) {
        return !isEmptyOrWhitespaceOnly(str);
    }
}
