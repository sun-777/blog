package com.blog.quark.service;

import java.util.List;

import com.blog.quark.entity.User;
import com.blog.quark.entity.column.UpdateColumnNode;

/**
 * 用户资料修改服务
 * 
 * @author SUNXDEN
 *
 */
public interface UserInfoEditService {
    long update(User user) throws RuntimeException;
    long updateById(List<UpdateColumnNode<?>> columnList, Long id) throws RuntimeException;
    
}
