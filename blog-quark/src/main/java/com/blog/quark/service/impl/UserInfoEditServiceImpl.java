package com.blog.quark.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.quark.entity.User;
import com.blog.quark.entity.column.UpdateColumnNode;
import com.blog.quark.mapper.UserMapper;
import com.blog.quark.service.UserInfoEditService;


@Service
public class UserInfoEditServiceImpl implements UserInfoEditService {
    @Autowired
    private UserMapper userMapper;
    

    @Transactional
    @Override
    public long update(User user) throws RuntimeException {
        long ret = 0;
        if (null == user) {
            return ret;
        }
        
        if (1 != (ret = userMapper.update(user, User.class))) {
            throw new RuntimeException(String.format("更新数据{ %s }失败", user));
        }
        return ret;
    }

    
    @Transactional
    @Override
    public long updateById(List<UpdateColumnNode<?>> columnList, Long userId) throws RuntimeException {
        long ret = 0;
        if (null == columnList || null == userId) {
            return ret;
        }
        
        if (1 != (ret = userMapper.updateById(columnList, userId, User.class))) {
            String info = columnList.stream()
                .map(u -> new StringBuilder(256).append(u.getColumnInfo().getColumn()).append(": ").append(u.getValue()).toString())
                .collect(Collectors.joining(", "));
            info = userId.toString().concat(" : ").concat(info);
            throw new RuntimeException(String.format("更新数据{ %s }失败", info));
        }
        return ret;
    }

    


}
