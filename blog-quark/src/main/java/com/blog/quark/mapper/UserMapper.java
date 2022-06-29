package com.blog.quark.mapper;

import static com.blog.quark.common.util.Constant.BASIC_RESULT_MAP;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

import com.blog.quark.common.Password;
import com.blog.quark.common.enumerate.GenderEnum;
import com.blog.quark.configure.GlobalConfig;
import com.blog.quark.context.resultmap.BaseResultMap;
import com.blog.quark.entity.User;
import com.blog.quark.entity.column.UpdateColumnNode;
import com.blog.quark.entity.field.EntityField;
import com.blog.quark.mapper.provider.BasicProvider;
import com.blog.quark.mapper.provider.BatchProvider;

@Component
public interface UserMapper extends EntityMapper<User> {
    
    @Results(id = BASIC_RESULT_MAP, value = {
            @Result(column="user_id", property="userId", id=true, jdbcType=JdbcType.BIGINT, javaType=Long.class),
            @Result(column="password", property="password", jdbcType=JdbcType.VARCHAR, javaType=Password.class),
            @Result(column="nickname", property="nickname", jdbcType=JdbcType.VARCHAR, javaType=String.class),
            @Result(column="gender", property="gender", jdbcType = JdbcType.VARCHAR, javaType = GenderEnum.class),
            @Result(column="birth", property="birth", jdbcType=JdbcType.TIMESTAMP, javaType=LocalDate.class),
            @Result(column="email", property="email", jdbcType=JdbcType.VARCHAR, javaType=String.class),
            @Result(column="introduction", property="introduction", jdbcType=JdbcType.VARCHAR, javaType=String.class),
            @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP, javaType=LocalDateTime.class),
            @Result(column="profile_id", property = "profileId", jdbcType=JdbcType.BIGINT, javaType=Long.class),
            @Result(column = "profile_id", property = "profile", one=@One(select = "com.blog.quark.mapper.ProfileMapper.lazyOrEagerGet", fetchType=FetchType.LAZY))
    })
    @SelectProvider(type = Provider.class, method="getEntitiesByIds")
    @Override
    List<User> getByIds(List<Long> idList, Class<User> clazz);
    
    
    
    @SelectProvider(type = Provider.class, method="getEntityById")
    @ResultMap(value = { BASIC_RESULT_MAP })
    @Override
    User get(Long id, Class<User> clazz);

    
    
    @SelectProvider(type = Provider.class, method="getEntities")
    @ResultMap(value = { BASIC_RESULT_MAP })
    @Override
    List<User> getAll(Class<User> clazz);
    
    
    @Override
    @InsertProvider(type = Provider.class, method="insertEntity")
    @Options(useGeneratedKeys = false)
    long insert(User entity, Class<User> clazz);
    
    
    @UpdateProvider(type = Provider.class, method="updateEntity")
    @Override
    long update(User entity, Class<User> clazz);
    
    
    
    @UpdateProvider(type = Provider.class, method="updateEntityById")
    @Override
    long updateById(List<UpdateColumnNode<?>> columnList, Long id, Class<User> clazz);
    
    
    
    @DeleteProvider(type = Provider.class, method="deleteEntity")
    @Override
    long delete(Long id, Class<User> clazz);

    
    /**============================================================
     *                     批量处理接口
     ============================================================*/
    @InsertProvider(type = Provider.class, method="batchInsertEntities")
    @Options(useGeneratedKeys = false)
    @Override
    long batchInsert(List<User> entities, Class<User> clazz);

    
    @UpdateProvider(type = Provider.class, method="batchUpdateEntities")
    @Override
    long batchUpdate(List<User> entities, Class<User> clazz);

    
    @UpdateProvider(type = Provider.class, method="batchUpdateEntitiesByColumns")
    @Override
    long batchUpdateByColumns(List<User> entities, List<String> columns, Class<User> clazz);


    @DeleteProvider(type = Provider.class, method = "batchDeleteEntities")
    @Override
    long batchDelete(List<Long> ids, Class<User> clazz);

    
    /**============================================================
     *              User表相关的接口，非通用的查询接口
     ============================================================*/
    @SelectProvider(type = Provider.class, method="getEntityByEmail")
    @ResultMap(value = { BASIC_RESULT_MAP })
    User getByEmail(String email);
    
    final class Provider implements BasicProvider<User>, BatchProvider<User> {
        
        public String getEntityByEmail(Object object) {
            String email = (String) object;
            String fullyQualifiedEmailField = EntityField.getQualifiedFieldName(User::getEmail);
            StringBuilder sb = new StringBuilder(256)
                    .append("select ")
                    .append(getJoinColumns(User.class))
                    .append(" from ")
                    .append(GlobalConfig.getMappingTableName(User.class))
                    .append(" as u where u.")
                    .append(BaseResultMap.getColumnInfo(fullyQualifiedEmailField).getColumn())
                    .append(" = '")
                    // email中有特殊字符"@"，所以使用''号将email包裹起来，防止SQLSyntaxErrorException异常
                    .append(email)
                    .append("'");
            return sb.toString();
        }
        
    }
}
