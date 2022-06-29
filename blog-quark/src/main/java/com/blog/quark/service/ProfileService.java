package com.blog.quark.service;

import com.blog.quark.entity.Profile;


/**
 * 用户头像服务（用户注册时新增头像，用户注销账号时删除头像，用户资料修改时，更新头像）
 * 
 * @author SUNXDEN
 *
 */
public interface ProfileService {
    Profile get(Long profileId);
    long add(Profile profile) throws RuntimeException;
    long update(Profile profile) throws RuntimeException;
    long delete(Long profileId) throws RuntimeException;
}
