package com.blog.quark.mapper;

import static com.blog.quark.common.util.Constant.BASIC_RESULT_MAP;

import java.util.List;

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

import com.blog.quark.entity.Preference;
import com.blog.quark.entity.column.UpdateColumnNode;
import com.blog.quark.mapper.provider.BasicProvider;
import com.blog.quark.mapper.provider.BatchProvider;

@Component
public interface PreferenceMapper extends EntityMapper<Preference> {

    @Results(id = BASIC_RESULT_MAP, value = {
            @Result(column="preference_id", property="preferenceId", id=true, jdbcType=JdbcType.BIGINT, javaType=Long.class),
            @Result(column="preference_key", property="preferenceKey", jdbcType=JdbcType.VARCHAR, javaType=String.class),
            @Result(column="preference_value", property="preferenceValue", jdbcType=JdbcType.VARCHAR, javaType=String.class),
    })
    @SelectProvider(type = Provider.class, method="getEntities")
    @Override
    List<Preference> getAll(Class<Preference> clazz);

    
    @SelectProvider(type = Provider.class, method="getEntityById")
    @ResultMap(value = { BASIC_RESULT_MAP })
    @Override
    Preference get(Long id, Class<Preference> clazz);

    
    @SelectProvider(type=Provider.class, method="getEntitiesByIds")
    @ResultMap(value = { BASIC_RESULT_MAP })
    @Override
    List<Preference> getByIds(List<Long> idList, Class<Preference> clazz);

    
    @InsertProvider(type = Provider.class, method="insertEntity")
    @Options(useGeneratedKeys = false)
    @Override
    long insert(Preference entity, Class<Preference> clazz);
    

    @UpdateProvider(type = Provider.class, method="updateEntity")
    @Override
    long update(Preference entity, Class<Preference> clazz);

    
    @UpdateProvider(type = Provider.class, method="updateEntityById")
    @Override
    long updateById(List<UpdateColumnNode<?>> columnList, Long id, Class<Preference> clazz);

    
    @DeleteProvider(type = Provider.class, method="deleteEntity")
    @Override
    long delete(Long id, Class<Preference> clazz);


    
    /**============================================================
     *                     批量处理接口
     ============================================================*/
    @InsertProvider(type = Provider.class, method="batchInsertEntities")
    @Options(useGeneratedKeys = false)
    @Override
    long batchInsert(List<Preference> entities, Class<Preference> clazz);

    
    @UpdateProvider(type = Provider.class, method="batchUpdateEntities")
    @Override
    long batchUpdate(List<Preference> entities, Class<Preference> clazz);

    
    @UpdateProvider(type = Provider.class, method="batchUpdateEntitiesByColumns")
    @Override
    long batchUpdateByColumns(List<Preference> entities, List<String> columns, Class<Preference> clazz);

    
    @DeleteProvider(type = Provider.class, method = "batchDeleteEntities")
    @Override
    long batchDelete(List<Long> ids, Class<Preference> clazz);


    final class Provider implements BasicProvider<Preference>, BatchProvider<Preference> {
        
    }
}
