package com.blog.quark.service;

import com.blog.quark.entity.User;


/**
 * 用户注册服务
 * 
 * @author Sun Xiaodong
 *
 */
public interface UserRegisterService {
    long register(User user) throws RuntimeException;
}
