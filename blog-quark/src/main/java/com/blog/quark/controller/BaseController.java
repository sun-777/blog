package com.blog.quark.controller;

import static com.blog.quark.common.util.Constant.TOKEN_JWT;
import static com.blog.quark.common.util.Constant.TOKEN_JWT_PREFIX;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.blog.quark.common.util.JwtUtil;

/**
 * 获取请求对象或响应对象，线程安全
 * 相比基类中自动注入HttpServletRequest、HttpServletResponse，BaseController可以允许实现的子类，继承其它的类
 * 
 * @author Sun Xiaodong
 *
 */

public interface BaseController {
    
    static HttpServletRequest request() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
    
    
    static HttpServletResponse response() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }
    
    
    /**
     * 从Token中获取用户Id、用户昵称信息
     * @return
     */
    static Map<String, Object> getFromToken() {
        String token = Objects.requireNonNull(BaseController.request().getHeader(TOKEN_JWT)).replace(TOKEN_JWT_PREFIX, "").strip();
        final Map<String, Object> map = new HashMap<>();
        Arrays.asList(JwtUtil.CLAIM_KEY_USERID, JwtUtil.CLAIM_KEY_NICKNAME)
              .forEach(u -> map.put(u, null));
        return JwtUtil.get(token, map);
    }
    
    
    /**
     * 获取Session（allowCreate：如果没有Session，是否创建一个HttpSession）
     * @param allowCreate
     * @return
     */
    static HttpSession session(boolean allowCreate) {
        return request().getSession(allowCreate);
    }
    
}
