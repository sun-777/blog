package com.blog.quark.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.quark.common.util.StringUtil;
import com.blog.quark.entity.User;
import com.blog.quark.mapper.UserMapper;
import com.blog.quark.service.UserQueryService;


@Service
public class UserQueryServiceImpl implements UserQueryService {
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User get(Long userId) {
        if (null == userId) {
            return null;
        }
        return userMapper.get(userId, User.class);
    }

    @Override
    public List<User> get(List<Long> userIds) {
        if (null == userIds || userIds.isEmpty()) {
            return null;
        }
        return userMapper.getByIds(userIds, User.class);
    }

    @Override
    public User getByEmail(String email) {
        if (StringUtil.isEmptyOrWhitespaceOnly(email)) {
            return null;
        }
        
        return userMapper.getByEmail(email);
    }
}
