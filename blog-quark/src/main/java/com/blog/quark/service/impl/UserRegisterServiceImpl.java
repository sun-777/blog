package com.blog.quark.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.quark.entity.Profile;
import com.blog.quark.entity.User;
import com.blog.quark.mapper.ProfileMapper;
import com.blog.quark.mapper.UserMapper;
import com.blog.quark.service.UserRegisterService;


@Service
public class UserRegisterServiceImpl implements UserRegisterService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProfileMapper profileMapper;
    
    
    @Transactional
    @Override
    public long register(User user) throws RuntimeException {
        long ret = 0;
        if (null == user || null == user.getProfile()) {
            return ret;
        }
        
        if (1 != (ret = profileMapper.insert(user.getProfile(), Profile.class))) {
            throw new RuntimeException(String.format("写入数据{ %s }失败", user.getProfile()));
        }
        
        user.setProfileId(user.getProfile().getProfileId());
        
        if (1 != (ret = userMapper.insert(user, User.class))) {
            throw new RuntimeException(String.format("写入数据{ %s }失败", user));
        }
        return ret;
    }

}
