package com.blog.quark.common.enumerate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.blog.quark.common.util.StringUtil;

public enum CodeMsg implements CodeKeyEnum<CodeMsg, Integer, String>{
	// 通用错误码
	UNKNOWN_ERROR(-1,"未知错误"),
	ERROR(0, "错误"),
	SUCCESS(200,"成功"),
	RESOURCE_NOT_FOUND(1001,"没有找到相关资源"),
	PARAMETER_IS_NULL(1002,"参数为空"),
	PARAMETER_MISSING(1003,"缺失必要的参数"),
	SERVER_ERROR(1010,"服务端异常"),
	
	REQUEST_ILLEGAL(1021,"非法请求"),
	ACCESS_LIMIT_REACHED(1022,"访问太频繁"),
	
	// 登陆模块错误码
	SESSION_NOT_EXISTS(1051, "会话不存在"),
	SESSION_EXPIRED(1052, "会话失效"),
	USERNAME_ILLEGAL(1053,"无效用户名"),
	USERNAME_NOT_EXISTS(1054,"用户名不存在"),
	PASSWORD_EMPTY(1055,"登录密码不能为空"),
	PASSWORD_ERROR(1056,"密码错误"),
	USER_NOT_LOGIN(1057,"用户未登录"),
	USER_TOKEN_EXPIRED(1058,"token失效"),
	
	// 
	MYSQL_EXCEPTION(1100, "数据库异常"),
	REDIS_EXCEPTION(1105, "Redis异常");
	
	private Integer code;
	private String key;
	
	
    private final static Map<Integer, CodeMsg> CODE_IMMUTABLEMAP;
    private final static Map<String, CodeMsg> KEY_IMMUTABLEMAP;
    
    static {
        CODE_IMMUTABLEMAP = Collections.unmodifiableMap(Arrays.stream(CodeMsg.values()).collect(Collectors.toMap(CodeMsg::code, Function.identity())));
        KEY_IMMUTABLEMAP = Collections.unmodifiableMap(Arrays.stream(CodeMsg.values()).collect(Collectors.toMap(k -> k.key().toLowerCase(), Function.identity())));
    }

	private CodeMsg(int code, String key) {
		this.code = code;
		this.key = key;
	}
	
	
    @Override
    public Integer code() {
        return this.code;
    }

    @Override
    public Optional<CodeMsg> codeOf(Integer c) {
        return Optional.ofNullable(null == c ? null : CODE_IMMUTABLEMAP.get(c));
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public Optional<CodeMsg> keyOf(String k) {
        return Optional.ofNullable(StringUtil.isEmptyOrWhitespaceOnly(k) ? null : KEY_IMMUTABLEMAP.get(k.toLowerCase()));
    }
	
}
