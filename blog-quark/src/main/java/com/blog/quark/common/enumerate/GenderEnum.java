package com.blog.quark.common.enumerate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.blog.quark.common.util.StringUtil;


public enum GenderEnum implements CodeKeyEnum<GenderEnum, String, String>{
    MALE("M", "男性"),
    FEMALE("F", "女性"),
    THIRDSEX("X", "第三性");
    
    private final String code;
    private final String key;

    private final static Map<String, GenderEnum> CODE_IMMUTABLEMAP;
    private final static Map<String, GenderEnum> KEY_IMMUTABLEMAP;
    
    static {
        CODE_IMMUTABLEMAP = Collections.unmodifiableMap(Arrays.stream(GenderEnum.values()).collect(Collectors.toMap(c -> c.code().toLowerCase(), Function.identity())));
        KEY_IMMUTABLEMAP = Collections.unmodifiableMap(Arrays.stream(GenderEnum.values()).collect(Collectors.toMap(k -> k.key().toLowerCase(), Function.identity())));
    }
    
    private GenderEnum(String code, String key) {
        this.code = code;
        this.key = key;
    }

    @Override
    public String code() {
        return this.code;
    }
    

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public Optional<GenderEnum> codeOf(String c) {
        return Optional.ofNullable(StringUtil.isEmptyOrWhitespaceOnly(c) ? null : CODE_IMMUTABLEMAP.get(c.toLowerCase()));
    }

    @Override
    public Optional<GenderEnum> keyOf(String k) {
        return Optional.ofNullable(StringUtil.isEmptyOrWhitespaceOnly(k) ? null : KEY_IMMUTABLEMAP.get(k.toLowerCase()));
    }
    
}