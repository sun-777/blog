package com.blog.quark.common.enumerate;

public interface CodeKeyValueEnum<T extends Enum<T> & CodeKeyValueEnum<T, C, K, V>, C, K, V> extends CodeKeyEnum<T, C, K> {
    V value();
}
