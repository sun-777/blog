package com.blog.quark.mapper;

import static com.blog.quark.common.util.Constant.BASIC_RESULT_MAP;

import java.time.LocalDateTime;
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

import com.blog.quark.entity.Content;
import com.blog.quark.mapper.provider.BasicProvider;
import com.blog.quark.mapper.provider.BatchProvider;


public interface ContentMapper extends EntityMapper<Content> {

    @Results(id = BASIC_RESULT_MAP, value = {
            @Result(column="content_id", property="contentId", id=true, jdbcType=JdbcType.BIGINT, javaType=Long.class),
            @Result(column="content", property="content", jdbcType=JdbcType.LONGVARCHAR, javaType=String.class),
            @Result(column="update_time", property="updateTime", jdbcType=JdbcType.DATE, javaType=LocalDateTime.class)
    })
    @SelectProvider(type = Provider.class, method="getEntityById")
    @Override
    Content get(Long id, Class<Content> clazz);

    @InsertProvider(type = Provider.class, method="insertEntity")
    @Options(useGeneratedKeys = false)
    @Override
    long insert(Content entity, Class<Content> clazz);

    @UpdateProvider(type = Provider.class, method="updateEntity")
    @Override
    long update(Content entity, Class<Content> clazz);

    @DeleteProvider(type = Provider.class, method="deleteEntity")
    @Override
    long delete(Long id, Class<Content> clazz);
    
    
    @SelectProvider(type = Provider.class, method="lazyOrEagerGetById")
    @ResultMap(value = {BASIC_RESULT_MAP})
    Content lazyOrEagerGet(Long id);

    
    /**============================================================
     *                     批量处理接口
     ============================================================*/
    @DeleteProvider(type = Provider.class, method = "batchDeleteEntities")
    @Override
    long batchDelete(List<Long> ids, Class<Content> clazz);
    final class Provider implements BasicProvider<Content>, BatchProvider<Content> {
        
        public String lazyOrEagerGetById(Object object) {
            Long id = (Long) object;
            return getEntitById0(id, Content.class);
        }
    }
}
