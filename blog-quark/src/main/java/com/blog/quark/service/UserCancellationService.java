package com.blog.quark.service;

import java.util.List;

/**
 * 注销用户信息（删除用户信息）
 * 
 * @author Sun Xiaodong
 *
 */
public interface UserCancellationService {
    long delete(Long id, List<String> paths) throws RuntimeException;
}
