package com.blog.quark.context.resultmap;

import static com.blog.quark.common.util.Constant.BASIC_RESULT_MAP;
import static com.blog.quark.common.util.Constant.ENTITY_FIELD_ID;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;

import com.blog.quark.common.util.StringUtil;
import com.blog.quark.entity.column.ColumnInfo;


/**
 * 
 * 将所有 Mapper接口（或者*Mapper.xml）配置的basicResultMap中的 实体类字段 和 数据库表列名 映射相关信息加载到columnInfoMap中
 * 
 * @author Sun Xiaodong
 *
 */

public final class BaseResultMap {
    
    // 执行初始化
    public static void initialize(final Configuration configuration) {
        BaseResultMapHolder.initialize(Objects.requireNonNull(configuration));
    }

    
    public static ColumnInfo getColumnInfo(final String fullyQualifiedFieldName) {
        if (StringUtil.isEmptyOrWhitespaceOnly(fullyQualifiedFieldName) || !fullyQualifiedFieldName.contains(".")) {
            return null;
        }
        return Objects.requireNonNull(BaseResultMapHolder.getColumnInfoMap().get(fullyQualifiedFieldName));
    }
    
    
    // 返回basicResultMap中定义的实体类的所有的ColumnInfo（包含 Id ColumnInfo）
    public static List<ColumnInfo> getColumnInfoByClass(final String entityFullyQualifiedClassName) {
        return getColumnInfoByClass(entityFullyQualifiedClassName, null, false);
    }
    
    
    // 返回basicResultMap中定义的实体类的所有的ColumnInfo（包含 Id ColumnInfo）
    // 同时返回对应顺序的实体类字段
    public static List<ColumnInfo> getColumnInfoByClass(final String entityFullyQualifiedClassName, List<String> entityFieldList) {
        return getColumnInfoByClass(entityFullyQualifiedClassName, entityFieldList, false);
    }
    
    
    // 返回basicResultMap中定义的实体类的所有的ColumnInfo（若：参数isExcludeColumnInfoy=true，则不包含 Id ColumnInfo）
    // 同时返回对应顺序的实体类字段
    public static List<ColumnInfo> getColumnInfoByClass(final String entityFullyQualifiedClassName, List<String> entityFieldList, boolean isExcludeColumnInfo) {
        if (StringUtil.isEmptyOrWhitespaceOnly(entityFullyQualifiedClassName) || !entityFullyQualifiedClassName.contains(".")) {
            return null;
        }
        
        String keyId = new StringBuilder(entityFullyQualifiedClassName).append(".").append(ENTITY_FIELD_ID).toString();
        String idColumnName = getColumnInfo(keyId).getColumn();

        List<Entry<String,ColumnInfo>> list = Objects.requireNonNull(BaseResultMapHolder.getColumnInfoMap().entrySet()).parallelStream()
                .filter(it -> it.getKey().contains(entityFullyQualifiedClassName))
                .collect(Collectors.toList());
        
        // 在 initBaseResultMap方法中，引入了一个key { 实体类的全限定名 + ".id" }
        // e.g.: 实体类的全限定名为："com.entity.User.id"
        //      A、如果实体类User的ID字段是"userId"，那么在获取实体类在COLUMN_INFO_MAP中的所有字段时，应该删除"com.entity.User.id"，保留"com.entity.User.userId";
        //      B、如果实体类User的ID字段是"id", 与引入的key相同，那么应保留"com.entity.User.id"
        // -------- START: 根据ColumnInfo.column去重逻辑代码实现 ---------
        boolean isDeleted = false;
        Entry<String, ColumnInfo> reservedEntry = null;
        Iterator<Entry<String, ColumnInfo>> iter = list.iterator();
        while (iter.hasNext()) {
            Entry<String, ColumnInfo> entry = iter.next();
            if (entry.getValue().getColumn().equals(idColumnName)) {
                if (entry.getKey().equals(keyId)) {
                    if (!isDeleted) {
                        isDeleted = true;
                        reservedEntry = entry;
                    }
                    iter.remove();
                } else {
                    if (!isDeleted) {
                        isDeleted = true;
                        reservedEntry = entry;
                        iter.remove();
                    } else {
                        reservedEntry = null;
                    }
                }
            }
        }
        if (isDeleted && null != reservedEntry && !isExcludeColumnInfo) {
            list.add(reservedEntry);
        }
        // -------- END: 根据ColumnInfo.column去重逻辑代码实现 ---------
        
        if (null != entityFieldList) {
            entityFieldList.addAll(list.stream().map(it -> {
                String key = it.getKey();
                final int index = key.lastIndexOf(".");
                return key.substring(index + 1);
            }).collect(Collectors.toList()));
        }
        
        return list.stream().map(it -> it.getValue()).collect(Collectors.toUnmodifiableList());
    }
    
    
    public static ColumnInfo getIdColumnInfo(final String entityFullyQualifiedClassName) {
        if (StringUtil.isEmptyOrWhitespaceOnly(entityFullyQualifiedClassName) || !entityFullyQualifiedClassName.contains(".")) {
            return null;
        }
        final String keyId = new StringBuilder(entityFullyQualifiedClassName).append(".").append(ENTITY_FIELD_ID).toString();
        return getColumnInfo(keyId);
    }
    
    
    public static String getIdColumnName(final String entityFullyQualifiedClassName) {
        return Objects.requireNonNull(getIdColumnInfo(entityFullyQualifiedClassName)).getColumn();
    }
    
    
    public static String getIdFiledName(final String entityFullyQualifiedClassName) {
        if (StringUtil.isEmptyOrWhitespaceOnly(entityFullyQualifiedClassName) || !entityFullyQualifiedClassName.contains(".")) {
            return null;
        }
        
        return Objects.requireNonNull(BaseResultMapHolder.getClassIdFieldMap().get(entityFullyQualifiedClassName));
    }
    
    
    // 使用私有构造器
    private BaseResultMap() {}
    
    
    static final class BaseResultMapHolder {
        // 存储实体类属性名 和 此属性对应的数据库表中的列名, e.g.: <key, value> = <com.example.mybatis.entity.User.name, t_name>
        private static volatile Map<String, ColumnInfo> COLUMN_INFO_MAP;
        
        // 存储实体类的Id字段（ Class<? extends Entity> class; ）
        // 格式为：<class.getName(), entityIdField>
        private static volatile Map<String, String> CLASS_ID_FIELD_MAP;
        
        private static final Map<String, String> getClassIdFieldMap(){
            return CLASS_ID_FIELD_MAP;
        }
        
        private static final Map<String, ColumnInfo> getColumnInfoMap() {
            return COLUMN_INFO_MAP;
        }
        
        private static void initialize(final Configuration configuration) {
            if (null == COLUMN_INFO_MAP) {
                synchronized (BaseResultMapHolder.class) {
                    if (null == COLUMN_INFO_MAP) {
                        COLUMN_INFO_MAP = new ConcurrentHashMap<>(1024);
                        CLASS_ID_FIELD_MAP = new ConcurrentHashMap<>();
                        initBaseResultMap(configuration, COLUMN_INFO_MAP);
                    }
                }
            }
        }
        
        
        private static void initBaseResultMap(final Configuration configuration, final Map<String, ColumnInfo> columnInfoMap) {
            //configuration.getResultMapNames().forEach(u -> System.out.println("----------------------" + u));
            // 获取 Mapper类字面常量（或者 *Mapper.xml中<mapper>节点的namespace属性表示的Mapper类字面常量）
            Objects.requireNonNull(configuration).getMapperRegistry().getMappers().forEach(clazz -> {
                final String fullyQualifiedClassName = clazz.getName();
                final String basicResultMapName = fullyQualifiedClassName + "." + BASIC_RESULT_MAP;
                if (configuration.hasResultMap(basicResultMapName)) {
                    // 获取 ResultMap 信息
                    final ResultMap basicResultMap = configuration.getResultMap(basicResultMapName);
                    //System.out.println("------------------------- id:" + basicResultMap.getId());
                    // 获取Mapper对应的实体类类名（或者 *Mapper.xml中<resultMap>节点的type属性表示的实体类类名）
                    final String entityFullyQualifiedClassName = basicResultMap.getType().getName();
                    // 获得basicResultMap中实体类的属性字段 和 数据库表的列名 映射信息
                    final List<ResultMapping> resultMappings = Objects.requireNonNull(basicResultMap).getResultMappings();
                    resultMappings.forEach(mapping -> {
                        // key: 由实体类的全限定名 + "." + 实体类属性名 组成
                        final String key = new StringBuilder(entityFullyQualifiedClassName).append(".").append(mapping.getProperty()).toString();
                        // 获取定义的数据库表中的列名前缀
                        final String columnPrefix = mapping.getColumnPrefix();
                        // value: 由 列名前缀（columnPrefix） + 列名（column） 组成
                        final String value = new StringBuilder(null == columnPrefix ? "" : columnPrefix).append(mapping.getColumn()).toString();
                        
                        if (null != mapping.getTypeHandler()) {
                            final ColumnInfo columnInfo = new ColumnInfo()
                                    .setColumn(value)
                                    .setJavaType(mapping.getJavaType())
                                    .setJdbcType(mapping.getJdbcType())
                                    .setTypeHandler(mapping.getTypeHandler().getClass());
                            
                            columnInfoMap.putIfAbsent(key, columnInfo);
                            //System.out.println("------- " + key + "  --  " + columnInfo.toString() + "");
                            // 如果当前ResultMapping对象被标记为ID（主键），则新增一个key: 实体类的全限定名 + "." + ENTITY_FIELD_ID
                            if (mapping.getFlags().stream().anyMatch(it -> { return it == ResultFlag.ID; })) {
                                CLASS_ID_FIELD_MAP.putIfAbsent(entityFullyQualifiedClassName, mapping.getProperty());
                                final String keyId = new StringBuilder(entityFullyQualifiedClassName).append(".").append(ENTITY_FIELD_ID).toString();
                                columnInfoMap.putIfAbsent(keyId, columnInfo);
                            }
                        }
                    });
                }
            });
        }
    }
    
}
