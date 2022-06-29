package com.blog.quark.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.quark.entity.Preference;
import com.blog.quark.mapper.PreferenceMapper;
import com.blog.quark.service.PreferenceService;


@Service
public class PreferenceServiceImpl implements PreferenceService {
    
    @Autowired
    private PreferenceMapper preferenceMapper;

    @Override
    public List<Preference> getAll() {
        return preferenceMapper.getAll(Preference.class);
    }

    @Transactional
    @Override
    public long add(List<Preference> list) throws RuntimeException {
        long ret = 0;
        if (null == list || list.isEmpty()) {
            return ret;
        }
        
        final int size = list.size();
        if (1 == size) {
            if (1 != (ret = preferenceMapper.insert(list.get(0), Preference.class))) {
                throw new RuntimeException(String.format("写入数据{ %s }失败", list.get(0)));
            }
        } else {
            if (size != (ret = preferenceMapper.batchInsert(list, Preference.class))) {
                throw new RuntimeException(String.format("写入%d条数据失败{ %s }", size, list));
            }
        }
        return ret;
    }

    @Transactional
    @Override
    public long delete(List<Preference> list) throws RuntimeException {
        long ret = 0;
        if (null == list || list.isEmpty()) {
            return ret;
        }
        
        final int size = list.size();
        if (1 == size) {
            if (1 != (ret = preferenceMapper.delete(list.get(0).getPreferenceId(), Preference.class))) {
                throw new RuntimeException(String.format("删除1条数据失败{ id: %s }", list.get(0).getPreferenceId()));
            }
        } else {
            final List<Long> idList = list.stream().map(it -> it.getPreferenceId()).collect(Collectors.toUnmodifiableList());
            if (size != (ret = preferenceMapper.batchDelete(idList, Preference.class))) {
                throw new RuntimeException(String.format("删除%d条数据失败{ id: %s }", size, list.stream().map(u -> u.getPreferenceId().toString()).collect(Collectors.joining(", "))));
            }
        }
        return ret;
    }

    
}
