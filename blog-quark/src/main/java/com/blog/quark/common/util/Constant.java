package com.blog.quark.common.util;

public final class Constant {

    public static final String BASIC_RESULT_MAP = "basicResultMap";
    public static final String ENTITY_FIELD_ID = "id";
    
    public static final String JAVATYPE_PREFIX = "javaType=";
    public static final String JDBCTYPE_PREFIX = "jdbcType=";
    public static final String TYPEHANDLER_PREFIX = "typeHandler=";
    
    public static final String WINDOWS = "windows";
    public static final String LINUX = "linux";
    public static final String UNIX = "unix";
    
    public static final String FILE_SEPARATOR = "/";
    public static final String MINUS = "-";
    
    // 系统头像图片tag， 有此tag的头像文件不会从文件服务器上删除
    public static final String SYS_PROFILE_TAG = "@default";
    
    
    // TOKEN
    public final static String TOKEN_JWT = "auth";
    // TOKEN头字段（空格不可省略）
    public final static String TOKEN_JWT_PREFIX = "Bearer ";
    
    public final static String RELOGIN = "relogin";
    
    private Constant() {}
}
