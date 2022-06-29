package com.blog.quark.common.enumerate;

import java.util.Arrays;
import java.util.Optional;

/**
 *  See: https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1.2
 *  AdditionalBound:
 *      & InterfaceType
 * 
 *  The enum values are initialized before any other static fields.
 * 
 *  @author Sun xiaodong
 */
public interface CodeKeyEnum<T extends Enum<T> & CodeKeyEnum<T, C, K>, C, K> extends CodeEnum<T, C> {
    K key();

    Optional<T> keyOf(K k);
    
    static <T extends Enum<T> & CodeKeyEnum<T, C, K>, C, K> Optional<T> keyOf(Class<T> enumClass, K k) {
        if (k instanceof String) {   //如果是String类型，则不分大小写进行比较
            return Arrays.stream(enumClass.getEnumConstants()).filter(e -> { return ( ((String) e.key()).equalsIgnoreCase(((String) k))); }).findAny();
        } else {
            return Arrays.stream(enumClass.getEnumConstants()).filter(e -> e.key().equals(k)).findAny();
        }
    }
}
