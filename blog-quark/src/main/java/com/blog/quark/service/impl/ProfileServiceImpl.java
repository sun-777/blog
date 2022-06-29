package com.blog.quark.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.quark.entity.Profile;
import com.blog.quark.mapper.ProfileMapper;
import com.blog.quark.service.ProfileService;


@Service
public class ProfileServiceImpl implements ProfileService {
    
    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public Profile get(Long profileId) {
        return profileMapper.get(profileId, Profile.class);
    }

    @Transactional
    @Override
    public long add(Profile profile) throws RuntimeException {
        long ret = 0;
        if (null == profile) {
            return ret;
        }
        
        if (1 != (ret = profileMapper.insert(profile, Profile.class))) {
            throw new RuntimeException(String.format("写入数据{ %s }失败", profile));
        }
        return ret;
    }

    @Transactional
    @Override
    public long update(Profile profile) throws RuntimeException {
        long ret = 0;
        if (null == profile) {
            return ret;
        }
        
        if (1 != (ret = profileMapper.update(profile, Profile.class))) {
            throw new RuntimeException(String.format("更新数据{ %s }失败", profile));
        }
        return ret;
    }

    
    @Transactional
    @Override
    public long delete(Long profileId) throws RuntimeException {
        long ret = 0;
        if (null == profileId) {
            return ret;
        }
        
        if (1 != (ret = profileMapper.delete(profileId, Profile.class))) {
            throw new RuntimeException(String.format("删除数据失败：{id： %s }", profileId));
        }
        return ret;
    }

}
