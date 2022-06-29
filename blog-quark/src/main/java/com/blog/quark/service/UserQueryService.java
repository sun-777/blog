package com.blog.quark.service;

import java.util.List;

import com.blog.quark.entity.User;

public interface UserQueryService {
    User get(Long userId);
    List<User> get(List<Long> userIds);
    User getByEmail(String email);
}
