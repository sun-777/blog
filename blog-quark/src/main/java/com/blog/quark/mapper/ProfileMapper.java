package com.blog.quark.mapper;

import static com.blog.quark.common.util.Constant.BASIC_RESULT_MAP;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import com.blog.quark.entity.Profile;
import com.blog.quark.mapper.provider.BasicProvider;
import com.blog.quark.mapper.provider.BatchProvider;

@Component
public interface ProfileMapper extends EntityMapper<Profile> {

    @Results(id = BASIC_RESULT_MAP, value = {
            @Result(column="profile_id", property="profileId", id=true, jdbcType=JdbcType.BIGINT, javaType=Long.class),
            @Result(column="profile", property="profile", jdbcType=JdbcType.VARCHAR, javaType=String.class),
            @Result(column="update_time", property="updateTime", jdbcType=JdbcType.TIMESTAMP, javaType=LocalDateTime.class),
    })
    @SelectProvider(type = Provider.class, method="getEntityById")
    @Override
    Profile get(Long id, Class<Profile> clazz);
    
    // for @one using.
    @SelectProvider(type = Provider.class, method="lazyOrEagerGetById")
    @ResultMap(value = {BASIC_RESULT_MAP})
    Profile lazyOrEagerGet(Long id);
    
    @InsertProvider(type = Provider.class, method="insertEntity")
    @Options(useGeneratedKeys = false)
    @Override
    long insert(Profile entity, Class<Profile> clazz);

    
    @UpdateProvider(type = Provider.class, method="updateEntity")
    @Override
    long update(Profile entity, Class<Profile> clazz);
    
    
    @DeleteProvider(type = Provider.class, method="deleteEntity")
    @Override
    long delete(Long id, Class<Profile> clazz);



    final class Provider implements BasicProvider<Profile>, BatchProvider<Profile> {
        
        public String lazyOrEagerGetById(Object object) {
            Long id = (Long) object;
            return getEntitById0(id, Profile.class);
        }

    }
}
