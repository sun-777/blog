package com.blog.quark.common.enumerate;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.blog.quark.common.util.StringUtil;



public enum StatusEnum implements CodeKeyEnum<StatusEnum, Integer, String>{
        CREATE(1, "create"),
        PAYED(2, "payed"),
        CONFIRM(3, "confirm"),
        RETURN(4, "return"),
        ARCHIVE(5, "archive");

    
    private final Integer code;
    private final String key;
    
    private final static Map<Integer, StatusEnum> CODE_IMMUTABLEMAP;
    private final static Map<String, StatusEnum> KEY_IMMUTABLEMAP;
    
    static {
        CODE_IMMUTABLEMAP = Arrays.stream(StatusEnum.values()).collect(Collectors.toUnmodifiableMap(StatusEnum::code, Function.identity()));
        KEY_IMMUTABLEMAP = Arrays.stream(StatusEnum.values()).collect(Collectors.toUnmodifiableMap(statusEnum -> statusEnum.key().toLowerCase(), Function.identity()));
    }
    
    
    private StatusEnum(Integer code, String key) {
        this.code = code;
        this.key  = key;
    }

    @Override
    public Integer code() {
        return this.code;
    }

    @Override
    public Optional<StatusEnum> codeOf(Integer c) {
        return Optional.ofNullable(null == c ? null : CODE_IMMUTABLEMAP.get(c));
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public Optional<StatusEnum> keyOf(String k) {
        return Optional.ofNullable(StringUtil.isEmptyOrWhitespaceOnly(k) ? null : KEY_IMMUTABLEMAP.get(k.toLowerCase()));
    }
    

}
