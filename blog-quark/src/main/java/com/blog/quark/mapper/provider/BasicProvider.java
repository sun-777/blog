package com.blog.quark.mapper.provider;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blog.quark.configure.GlobalConfig;
import com.blog.quark.context.resultmap.BaseResultMap;
import com.blog.quark.entity.Entity;
import com.blog.quark.entity.column.ColumnInfo;
import com.blog.quark.entity.column.UpdateColumnNode;


public interface BasicProvider<T extends Entity> {
    
    static final Logger LOG = LoggerFactory.getLogger(BasicProvider.class);
    
    
    // ParamNameResolver::getNamedParams
    // 当 *Mapper类的某个方法【只有一个参数】的时候：
    //    @1、若【参数Args有@Param注解】标记:
    //          其在@*Provider注解中 method指定的方法参数类型是：Map<String, Object> map，此时的Args可通过 map.get("param1") 获取
    //    @2、若【参数Args没有@Param注解】标记：
    //          其在@*Provider注解中 method指定的方法参数类型是：Object obj，此时的Args可通过 直接强转obj 获取;
    default String getEntities(Object obj) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) obj;
        if (Entity.class.isAssignableFrom(clazz)) {
            StringBuilder sb = new StringBuilder(256)
                    .append("select ")
                    .append(getJoinColumns(clazz))
                    .append(" from ")
                    .append(GlobalConfig.getMappingTableName(clazz));
            
            LOG.debug("BasicProvider<{}> - getEntities - [\"{}\"]", clazz.getName(), sb.toString());
            return sb.toString();
        }
        return null;
    }
    
    
    default String getEntityById (Map<String, Object> map) {
        Long id = (Long) map.get("param1");
        if (null == id) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) map.get("param2");
        return getEntitById0(id, clazz);
    }
    
    

    default String getEntitiesByIds(Map<String, Object> map) {
        @SuppressWarnings("unchecked")
        List<Long> list = (List<Long>) map.get("param1");
        if(null == list || list.isEmpty()) {
            // 如果参数非法，则返回sql语句 "SELECT 1 FROM DUAL" 让程序正常执行，防止抛出异常
            // log.warning("");
            return null;
        }
        
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) map.get("param2");
        
        final String idColumnName = BaseResultMap.getIdColumnName(clazz.getName());
        StringBuilder sb = new StringBuilder(256)
                .append("select ")
                .append(getJoinColumns(clazz))
                .append(" from ")
                .append(GlobalConfig.getMappingTableName(clazz))
                .append(" as u where u.")
                .append(idColumnName);
        if (1 == list.size()) {
            sb.append(" = ").append(list.get(0));
        } else {
            sb.append(" IN ( ");
            list.forEach(e -> sb.append(e).append(" , "));
            // 删除最后一个多余的","，最后补上" )"
            sb.delete(sb.lastIndexOf(","), sb.length()).append(" )");
        }
        
        LOG.debug("BasicProvider<{}> - getEntitiesByIds - [\"{}\"]", clazz.getName(), sb.toString());
        return sb.toString();
    }
    
    
    
    default String insertEntity(Map<String, Object> map) {
        @SuppressWarnings("unchecked")
        T entity = (T) map.get("param1");
        if (null == entity) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) map.get("param2");
        
        List<String> fieldList = new ArrayList<>();  // 实体类字段列表
        List<ColumnInfo> columnInfoList = BaseResultMap.getColumnInfoByClass(clazz.getName(), fieldList);
        
        StringBuilder joinColumns = new StringBuilder(256).append(" ( ");
        StringBuilder joinValues = new StringBuilder(1024).append(" ( ");
        
        String formatterStr = "#'{'param1.{0}, {1}}";
        MessageFormat mf = new MessageFormat(formatterStr);
        // 拼接columns和values
        for (int i = 0, size = fieldList.size(); i < size; ++i) {
            final ColumnInfo columnInfo = columnInfoList.get(i);
            joinColumns.append(columnInfo.getColumn()).append(" , ");
            joinValues.append(mf.format(new Object[] {fieldList.get(i), columnInfo.getType()})).append(", ");
        }
        // 删除最后一个多余的","，并在最后补上" )"
        joinColumns.delete(joinColumns.lastIndexOf(","), joinColumns.length()).append(" )");
        joinValues.delete(joinValues.lastIndexOf(","), joinValues.length()).append(" )");
        
        StringBuilder sb = new StringBuilder(256)
                .append("INSERT INTO `")
                .append(GlobalConfig.getMappingTableName(clazz))
                .append("`")
                .append(joinColumns.toString())
                .append(" VALUES ")
                .append(joinValues.toString());
        
        LOG.debug("BasicProvider<{}> - insertEntity - [\"{}\"]", clazz.getName(), sb.toString());
        return sb.toString();
    }
    
    
    default String updateEntity(Map<String, Object> map) {
        @SuppressWarnings("unchecked")
        T entity = (T) map.get("param1");
        if (null == entity) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) map.get("param2");
        
        List<String> fieldList = new ArrayList<>();  // 实体类字段列表
        List<ColumnInfo> columnInfoList = BaseResultMap.getColumnInfoByClass(clazz.getName(), fieldList, true);
        
        String formatterStr = "{0}=#'{'param1.{1}, {2}}";
        MessageFormat mf = new MessageFormat(formatterStr);
        StringBuilder joinEntityUpdateFields = new StringBuilder(1024);
        for (int i = 0, size = fieldList.size(); i < size; ++i) {
            final ColumnInfo columnInfo = columnInfoList.get(i);
            // 格式化后的字符串类似于: user_id=#{param1.userId, javaType=java.lang.Long, jdbcType=BIGINT, typeHandler=org.apache.ibatis.type.LongTypeHandler}                        
            joinEntityUpdateFields.append(mf.format(new Object[] {columnInfo.getColumn(),  fieldList.get(i), columnInfo.getType()})).append(", ");
        }
        // 删除最后一个多余的","
        joinEntityUpdateFields.delete(joinEntityUpdateFields.lastIndexOf(","), joinEntityUpdateFields.length());
        
        final ColumnInfo idColumnInfo = BaseResultMap.getIdColumnInfo(clazz.getName());
        final String idFieldName = BaseResultMap.getIdFiledName(clazz.getName());
        StringBuilder sb = new StringBuilder(1024)
                .append("UPDATE `")
                .append(GlobalConfig.getMappingTableName(clazz))
                .append("` SET ")
                .append(joinEntityUpdateFields.toString())
                .append(" WHERE ")
                //格式化ID信息
                .append(mf.format(new Object[] {idColumnInfo.getColumn(),  idFieldName, idColumnInfo.getType()}));
        
        LOG.debug("BasicProvider<{}> - updateEntity - [\"{}\"]", clazz.getName(), sb.toString());
        return sb.toString();

    }
    
    
    default String updateEntityById(Map<String, Object> map) {
        @SuppressWarnings("unchecked")
        final List<UpdateColumnNode<?>> columnList = (List<UpdateColumnNode<?>>) map.get("param1");
        Long entityId = (Long) map.get("param2");
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) map.get("param3");
        
        if (null == columnList || columnList.isEmpty() || null == entityId) {
            return null;
        }
        
        String formatterStr = "$'{'param1[{0}].columnInfo.column}=#'{'param1[{0}].value, $'{'param1[{0}].columnInfo.type}}";
        MessageFormat mf = new MessageFormat(formatterStr);
        StringBuilder joinUpdateColumnNodes = new StringBuilder(1024);
        for (int i = 0, size = columnList.size(); i < size; ++i) {
            joinUpdateColumnNodes.append(mf.format(new Object[] {i})).append(", ");
        }
        // 删除最后一个多余的","
        joinUpdateColumnNodes.delete(joinUpdateColumnNodes.lastIndexOf(","), joinUpdateColumnNodes.length());
        
        String idColumnName = BaseResultMap.getIdColumnName(clazz.getName());
        StringBuilder sb = new StringBuilder(2048)
                .append("UPDATE `")
                .append(GlobalConfig.getMappingTableName(clazz))
                .append("` SET ")
                .append(joinUpdateColumnNodes.toString())
                .append(" WHERE ")
                .append(idColumnName)
                .append(" = ")
                .append(entityId);
        
        LOG.debug("BasicProvider<{}> - updateEntityById - [\"{}\"]", clazz.getName(), sb.toString());
        return sb.toString();
    }
    
    
    
    default String deleteEntity(Map<String, Object> map) {
        Long entityId = (Long) map.get("param1");
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) map.get("param2");
        //不允许删除全表记录
        if (null == entityId) {
            return null;
        }
        
        String idColumnName = BaseResultMap.getIdColumnName(clazz.getName());
        StringBuilder sb = new StringBuilder(256)
                .append("DELETE FROM ")
                .append(GlobalConfig.getMappingTableName(clazz))
                .append(" WHERE ")
                .append(idColumnName)
                .append(" = ")
                .append(entityId);
        
        LOG.debug("BasicProvider<{}> - updateEntityById - [\"{}\"]", clazz.getName(), sb.toString());
        return sb.toString();
    }
    
    


    default String getEntitById0(Long id, Class<T> clazz) {
        final String idColumnName = BaseResultMap.getIdColumnName(clazz.getName());
        StringBuilder sb = new StringBuilder(256)
                .append("select ")
                .append(getJoinColumns(clazz))
                .append(" from ")
                .append(GlobalConfig.getMappingTableName(clazz))
                .append(" as uxd where uxd.")
                .append(idColumnName)
                .append(" = ")
                .append(id);
        LOG.debug("BasicProvider<{}> - getEntityById - [\"{}\"]", clazz.getName(), sb.toString());
        return sb.toString();
    }
    
    
    default String getJoinColumns(Class<T> clazz) {
        return BaseResultMap.getColumnInfoByClass(clazz.getName()).stream().map(it -> it.getColumn()).collect(Collectors.joining(", "));
    }
}
