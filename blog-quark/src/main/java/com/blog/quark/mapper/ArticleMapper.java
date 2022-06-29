package com.blog.quark.mapper;

import static com.blog.quark.common.util.Constant.BASIC_RESULT_MAP;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import com.blog.quark.configure.GlobalConfig;
import com.blog.quark.context.resultmap.BaseResultMap;
import com.blog.quark.entity.Article;
import com.blog.quark.entity.column.UpdateColumnNode;
import com.blog.quark.entity.field.EntityField;
import com.blog.quark.mapper.provider.BasicProvider;
import com.blog.quark.mapper.provider.BatchProvider;


@Component
public interface ArticleMapper extends EntityMapper<Article>{


    @Results(id = BASIC_RESULT_MAP, value = {
            @Result(column="article_id", property="articleId", id=true, jdbcType=JdbcType.BIGINT, javaType=Long.class),
            @Result(column="title", property="title", jdbcType=JdbcType.VARCHAR, javaType=String.class),
            @Result(column="author", property="author", jdbcType=JdbcType.BIGINT, javaType=Long.class),
            @Result(column="description", property="description", jdbcType=JdbcType.VARCHAR, javaType=String.class),
            @Result(column="content_id", property="contentId", jdbcType=JdbcType.BIGINT, javaType=Long.class),
            @Result(column="create_time", property="createTime", jdbcType=JdbcType.DATE, javaType=LocalDateTime.class),
            @Result(column="content_id", property="content", one=@One(select = "com.blog.quark.mapper.ContentMapper.lazyOrEagerGet", fetchType=FetchType.LAZY))
    })
    @SelectProvider(type = Provider.class, method="getEntities")
    @Override
    List<Article> getAll(Class<Article> clazz);

    
    @SelectProvider(type = Provider.class, method="getEntityById")
    @ResultMap(value = { BASIC_RESULT_MAP })
    @Override
    Article get(Long id, Class<Article> clazz);

    
    @SelectProvider(type = Provider.class, method="getEntitiesByIds")
    @ResultMap(value = { BASIC_RESULT_MAP })
    @Override
    List<Article> getByIds(List<Long> idList, Class<Article> clazz);

    
    @InsertProvider(type = Provider.class, method="insertEntity")
    @Options(useGeneratedKeys = false)
    @Override
    long insert(Article entity, Class<Article> clazz);

    
    @UpdateProvider(type = Provider.class, method="updateEntity")
    @Override
    long update(Article entity, Class<Article> clazz);

    
    @UpdateProvider(type = Provider.class, method="updateEntityById")
    @Override
    long updateById(List<UpdateColumnNode<?>> columnList, Long id, Class<Article> clazz);

    
    @DeleteProvider(type = Provider.class, method="deleteEntity")
    @Override
    long delete(Long id, Class<Article> clazz);
    

    /**============================================================
     *                     批量处理接口
     ============================================================*/
    @DeleteProvider(type = Provider.class, method = "batchDeleteEntities")
    @Override
    long batchDelete(List<Long> ids, Class<Article> clazz);
    
    
    
    /**============================================================
     *                    Article表相关的接口，非通用的查询接口
     ============================================================*/
    @SelectProvider(type = Provider.class, method="getEntitiesByAuthorId")
    @ResultMap(value = { BASIC_RESULT_MAP })
    List<Article> getByAuthorId(Long authorId);
    
    @SelectProvider(type = Provider.class, method="getEntitiesByOffset")
    @ResultMap(value = { BASIC_RESULT_MAP })
    List<Article> getRange(Long offset, Long limit, String orderByColumn, boolean isAsc);
    
    @SelectProvider(type = Provider.class, method="getCount")
    Long count();
    
    
    
    final class Provider implements BasicProvider<Article>, BatchProvider<Article> {
        
        public String getEntitiesByAuthorId(Object object) {
            Long authorId = (Long) object;
            String fullyQualifiedAuthorIdField = EntityField.getQualifiedFieldName(Article::getAuthor);
            StringBuilder sb = new StringBuilder(512)
                    .append("select ")
                    .append(getJoinColumns(Article.class))
                    .append(" from ")
                    .append(GlobalConfig.getMappingTableName(Article.class))
                    .append(" as art where art.")
                    .append(BaseResultMap.getColumnInfo(fullyQualifiedAuthorIdField).getColumn())
                    .append(" = ")
                    .append(authorId);
            return sb.toString();
        }
        
        
        public String getEntitiesByOffset(Map<String, Object> map) {
            Long offset = (Long) map.get("param1");
            Long limit = (Long) map.get("param2");
            String orderByColumn = (String) map.get("param3");
            boolean isAsc = (boolean) map.get("param4");
            
            StringBuilder sb = new StringBuilder(512)
                    .append("select ")
                    .append(getJoinColumns(Article.class))
                    .append(" from ")
                    .append(GlobalConfig.getMappingTableName(Article.class))
                    .append(" order by ")
                    .append(orderByColumn)
                    .append(isAsc ? " asc " : " desc ")
                    .append(" limit ")
                    .append(limit)
                    .append(" offset ")
                    .append(offset);
            return sb.toString();
        }
        
        
        public String getCount() {
            StringBuilder sb = new StringBuilder(128)
                    .append("select count(1) from ")
                    .append(GlobalConfig.getMappingTableName(Article.class));
            return sb.toString();
        }
    }
}
