package com.blog.quark.mapper.provider;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blog.quark.configure.GlobalConfig;
import com.blog.quark.context.resultmap.BaseResultMap;
import com.blog.quark.entity.Entity;
import com.blog.quark.entity.column.ColumnInfo;

public interface BatchProvider<T extends Entity> {
    
    static final Logger LOG = LoggerFactory.getLogger(BatchProvider.class);
    
    default String batchInsertEntities(Map<String, Object> map) {
        @SuppressWarnings("unchecked")
        List<T> list = (List<T>) map.get("param1");
        if(null == list || list.isEmpty()) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) map.get("param2");
        
        return batchInsertEntitiesCommon(list, clazz);
    }



    default String batchUpdateEntities(Map<String, Object> map) {
        @SuppressWarnings("unchecked")
        List<T> entityList = (List<T>) map.get("param1");
        if(null == entityList || entityList.isEmpty()) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) map.get("param2");
        
        return batchUpdateEntities0(entityList, null, clazz);
    }
    
    
    
    default String batchUpdateEntitiesByColumns(Map<String, Object> map) {
        @SuppressWarnings("unchecked")
        List<T> entityList = (List<T>) map.get("param1");
        if(null == entityList || entityList.isEmpty()) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        List<String> columnList = (List<String>) map.get("param2");
        
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) map.get("param3");
        
        if (null == columnList || 0 == columnList.size()) {
            // 全量更新
            return batchUpdateEntities0(entityList, null, clazz);
        } else {
            // 根据指定的列名，更新数据
            return batchUpdateEntities0(entityList, columnList, clazz);
        }
    }
    
    
    default String batchDeleteEntities(Map<String, Object> map) {
        @SuppressWarnings("unchecked")
        List<Long> idList = (List<Long>) map.get("param1");
        if(null == idList || idList.isEmpty()) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) map.get("param2");
        
        final ColumnInfo idColumnInfo = BaseResultMap.getIdColumnInfo(clazz.getName());

        StringBuilder sb = new StringBuilder(1024)
                .append("DELETE FROM `")
                .append(GlobalConfig.getMappingTableName(clazz))
                .append("` WHERE ")
                .append(idColumnInfo.getColumn())
                .append(" IN ( ")
                .append(idList.stream().map(it -> it.toString()).collect(Collectors.joining(", ")))
                .append(" )");
        
        LOG.debug("BatchProvider<{}> - batchDeleteEntities - [\"{}\"]", clazz.getName(), sb.toString());
        return sb.toString();
    }
    
    
    
    default String batchUpdateEntities0(List<T> entities, List<String> columnNames, Class<T> clazz) {
        
        // fieldList: 实体类字段列表（不含实体类ID字段）
        // columnInfoList: 与fieldList一一对应的列名列表（不含实体类ID字段对应的列名）
        List<String> fieldList = new ArrayList<>();
        List<ColumnInfo> columnInfoList = BaseResultMap.getColumnInfoByClass(clazz.getName(), fieldList, true);

        // 根据columnNames，过滤掉不需要更新的列名
        if (!(null == columnNames || columnNames.isEmpty())) {
            Map<String, ColumnInfo> fieldColumnMap = new HashMap<>();
            for (int i = 0, size = columnInfoList.size(); i < size; ++i) {
                fieldColumnMap.put(fieldList.get(i), columnInfoList.get(i));
            }
            
            final Set<String> columnSet = columnNames.stream().collect(Collectors.toSet());
            //过滤掉不在columnNames的列名
            final Stream<Entry<String, ColumnInfo>> stream = fieldColumnMap.entrySet().stream().filter(it -> columnSet.contains(it.getValue().getColumn()));
            //重新生成与columnNames中列名匹配的fieldList 和 columnInfoList
            fieldList = stream.map(it -> it.getKey()).collect(Collectors.toUnmodifiableList());
            columnInfoList = stream.map(it -> it.getValue()).collect(Collectors.toUnmodifiableList());
        }
        
        
        final ColumnInfo idColumnInfo = BaseResultMap.getIdColumnInfo(clazz.getName());
        final String idFieldName = BaseResultMap.getIdFiledName(clazz.getName());
        String formatterStr = "#'{'param1[{0}].{1}, {2}}";
        MessageFormat mf = new MessageFormat(formatterStr);
        StringBuilder joinEntityIds = new StringBuilder(1024);
        StringBuilder joinEntities = new StringBuilder(2048);
        final int entitySize = entities.size();
        for (int i = 0, fieldSize = fieldList.size(); i < fieldSize; ++i) {
            final ColumnInfo columnInfo = columnInfoList.get(i);

            joinEntities.append(columnInfo.getColumn()).append(" = CASE ").append(idColumnInfo.getColumn());
            for (int j = 0; j < entitySize; ++j) {
                joinEntities.append("  WHEN ")
                    .append(mf.format(new Object[] {j, idFieldName, idColumnInfo.getType()}))
                    .append(" THEN ")
                    .append(mf.format(new Object[] {j, fieldList.get(i), columnInfo.getType()}));
            }
            joinEntities.append(" END, ");
        }
        // 删除最后一个多余的","
        joinEntities.delete(joinEntities.lastIndexOf(","), joinEntities.length());
        
        for (int i = 0; i < entitySize; ++i) {
            joinEntityIds.append(mf.format(new Object[] {i, idFieldName, idColumnInfo.getType()})).append(", ");
        }
        // 删除最后一个多余的","
        joinEntityIds.delete(joinEntityIds.lastIndexOf(","), joinEntityIds.length());
        
        StringBuilder sb = new StringBuilder(4096)
                .append("UPDATE `")
                .append(GlobalConfig.getMappingTableName(clazz))
                .append("` SET ")
                .append(joinEntities.toString())
                .append(" WHERE ")
                .append(idColumnInfo.getColumn())
                .append(" IN ( ")
                .append(joinEntityIds.toString())
                .append(" )");
        
        LOG.debug("BatchProvider<{}> - batchUpdateEntities - [\"{}\"]", clazz.getName(), sb.toString());
        return sb.toString();
    }
    
    
    
    default String batchInsertEntitiesForMySQL(List<T> list, Class<T> clazz) {
        //INSERT INTO USER (NAME,AGE,SEX)
        //    VALUES
        //    ('val1_1', 'val1_2', 'val1_3'),
        //    ('val2_1', 'val2_2', 'val2_3'),
        //    ('val3_1', 'val3_2', 'val3_3');
        List<String> fieldList = new ArrayList<>();  // 实体类字段列表
        List<ColumnInfo> columnInfoList = BaseResultMap.getColumnInfoByClass(clazz.getName(), fieldList);
        
        // 拼接columns
        StringBuilder joinColumns = new StringBuilder(256).append(" ( ");
        final int columnSize = fieldList.size();  // 等于columnInfoList.size()
        for (int i = 0; i < columnSize; i++) {
            joinColumns.append(columnInfoList.get(i).getColumn()).append(" , ");
        }
        joinColumns.delete(joinColumns.lastIndexOf(","), joinColumns.length()).append(" )");
        
        // 拼接values
        String formatterValueStr = "#'{'param1[{0}].{1}, {2}}";
        MessageFormat mf = new MessageFormat(formatterValueStr);
        StringBuilder joinValues = new StringBuilder(2048);
        for (int i = 0, entitySize = list.size(); i < entitySize; i++) {
            joinValues.append("( ");
            for (int j = 0; j < columnSize; j++) {
                joinValues.append(mf.format(new Object[] {i, fieldList.get(j), columnInfoList.get(j).getType()})).append(", ");
            }
            // 删除最后一个多余的","
            joinValues.delete(joinValues.lastIndexOf(","), joinValues.length());
            joinValues.append("), ");
        }
        // 删除最后一个多余的","
        joinValues.delete(joinValues.lastIndexOf(","), joinValues.length());
        
        StringBuilder sb = new StringBuilder(4096)
                .append("INSERT INTO `")
                .append(GlobalConfig.getMappingTableName(clazz))
                .append("`")
                .append(joinColumns.toString())
                .append(" VALUES ")
                .append(joinValues.toString());
        
        LOG.debug("BatchProvider<{}> - batchInsertEntities - [\"{}\"]", clazz.getName(), sb.toString());
        return sb.toString();
    }
    
    
    

    default String batchInsertEntitiesCommon(List<T> list, Class<T> clazz) {
        //  INSERT INTO USER (NAME,AGE,SEX)
        //      select 'val1_1', 'val1_2', 'val1_3' from dual union all
        //      select 'val2_1', 'val2_2', 'val2_3' from dual union all
        //      select 'val3_1', 'val3_2', 'val3_3' from dual
        List<String> fieldList = new ArrayList<>();  // 实体类字段列表
        List<ColumnInfo> columnInfoList = BaseResultMap.getColumnInfoByClass(clazz.getName(), fieldList);
        
        // 拼接columns
        StringBuilder joinColumns = new StringBuilder(256).append(" ( ");
        final int columnSize = fieldList.size();  // 等于columnInfoList.size()
        for (int i = 0; i < columnSize; i++) {
            joinColumns.append(columnInfoList.get(i).getColumn()).append(" , ");
        }
        joinColumns.delete(joinColumns.lastIndexOf(","), joinColumns.length()).append(" )");
        
        // 拼接values
        String formatterValueStr = "#'{'param1[{0}].{1}, {2}}";
        MessageFormat mf = new MessageFormat(formatterValueStr);
        StringBuilder joinValues = new StringBuilder(2048);
        for (int i = 0, entitySize = list.size(); i < entitySize; i++) {
            joinValues.append(" select ");
            for (int j = 0; j < columnSize; j++) {
                joinValues.append(mf.format(new Object[] {i, fieldList.get(j), columnInfoList.get(j).getType()})).append(", ");
            }
            // 删除最后一个多余的","
            joinValues.delete(joinValues.lastIndexOf(","), joinValues.length());
            joinValues.append(" from dual union all");
        }
        // 删除最后一个多余的","
        joinValues.delete(joinValues.lastIndexOf("union all"), joinValues.length());
        
        StringBuilder sb = new StringBuilder(4096)
                .append("INSERT INTO `")
                .append(GlobalConfig.getMappingTableName(clazz))
                .append("`")
                .append(joinColumns.toString())
                .append(" ")
                .append(joinValues.toString());
        
        LOG.debug("BatchProvider<{}> - batchInsertEntities - [\"{}\"]", clazz.getName(), sb.toString());
        return sb.toString();
    }
    
}
