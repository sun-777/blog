package com.blog.quark.common.enumerate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.blog.quark.common.util.StringUtil;


/**
 * PreferenceEnum.code不可修改，因为在数据库表中，以code为主键；
 * 
 * @author Sun Xiaodong
 *
 */
public enum PreferenceEnum implements CodeKeyValueEnum<PreferenceEnum, Long, String, String>{
    // ***** 约定：目录路径末尾不加"/" *****
    //deafult: d:/blog
    UPLOAD_ROOT_PATH_WIN(1L, "upload_root_path_win", "d:/blog/upload"),
    //default: /blog
    UPLOAD_ROOT_PATH_LINUX(2L, "upload_root_path_linux", "/opt/blog/upload"),
    //default: /img
    IMAGE_UPLOAD_PATH(3L, "image_upload_path", "/img"),
    //default: .bmp;.png;.jpg;.gif;.webp
    IMAGE_SUPPORTED_FORMAT(4L, "image_supported_format", ".png;.jpg;.jpeg;.gif;.webp");
    
    
    private final static Map<Long, PreferenceEnum> CODE_IMMUTABLEMAP;
    private final static Map<String, PreferenceEnum> KEY_IMMUTABLEMAP;
    
    static {
        CODE_IMMUTABLEMAP = Collections.unmodifiableMap(Arrays.stream(PreferenceEnum.values()).collect(Collectors.toMap(PreferenceEnum::code, Function.identity())));
        KEY_IMMUTABLEMAP = Collections.unmodifiableMap(Arrays.stream(PreferenceEnum.values()).collect(Collectors.toMap(k -> k.key().toLowerCase(), Function.identity())));
    }
    
    
    
    private final Long code;
    private final String key;
    private final String value;
    
    private PreferenceEnum(Long code, String key, String value) {
        this.code = code;
        this.key = key;
        this.value = value;
    }
    @Override
    public Long code() {
        return this.code;
    }

    
    @Override
    public String value() {
        return this.value;
    }

    @Override
    public String key() {
        return this.key;
    }
    
    
    @Override
    public Optional<PreferenceEnum> codeOf(Long c) {
        return Optional.ofNullable(null == c ? null : CODE_IMMUTABLEMAP.get(c));
    }


    @Override
    public Optional<PreferenceEnum> keyOf(String k) {
        return Optional.ofNullable(StringUtil.isEmptyOrWhitespaceOnly(k) ? null : KEY_IMMUTABLEMAP.get(k.toLowerCase()));
    }


}
