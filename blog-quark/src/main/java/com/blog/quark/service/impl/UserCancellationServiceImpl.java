package com.blog.quark.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.quark.entity.Profile;
import com.blog.quark.entity.User;
import com.blog.quark.mapper.ProfileMapper;
import com.blog.quark.mapper.UserMapper;
import com.blog.quark.service.UserCancellationService;


@Service
public class UserCancellationServiceImpl implements UserCancellationService {
    
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProfileMapper profileMapper;
    
    @Transactional
    @Override
    public long delete(Long userId, List<String> paths) throws RuntimeException {
        long ret = 0;
        if (null == userId) {
            return ret;
        }
        
        User user = userMapper.get(userId, User.class);
        if (null != user) {
            // 删除用户头像信息
            Profile profile = user.getProfile();
            if (null != profile) {
                
                paths.add(profile.getProfile());
                
                if (1 != (ret = profileMapper.delete(profile.getProfileId(), Profile.class))) {
                    throw new RuntimeException(String.format("删除用户头像失败：{id： %s }", userId));
                }
            }
            
            // 2.删除用户信息
            if (1 != (ret = userMapper.delete(userId, User.class))) {
                throw new RuntimeException(String.format("删除数据失败：{id： %s }", userId));
            }
            
            
            // 3.从文件服务器中删除用户头像文件， 查看头像文件是否为默认
            // ... 与数据库事务操作无关的代码执行，放在在service外面删除;
        }
        return ret;
    }

}
