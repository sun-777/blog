package com.blog.quark.mapper;

import java.util.List;

import com.blog.quark.entity.Entity;
import com.blog.quark.entity.column.UpdateColumnNode;

public interface EntityMapper<T extends Entity> {
    
    /**
     * 获取指定实体类的记录
     * 
     * @param clazz 实体类的类文字向量（通过clazz绑定对应的数据库表）
     * @return
     */
    List<T> getAll(Class<T> clazz);
    
    
    /**
     * 根据id，获取对应的实体类记录
     * 
     * @param id
     * @param clazz 实体类的类文字向量（通过clazz绑定对应的数据库表）
     * @return
     */
    T get(Long id, Class<T> clazz);
    
    /**
     * 查找指定id（可一个或多个）的记录
     * 
     * @param entityIds id集合
     * @param clazz 实体类的类文字向量（通过clazz绑定对应的数据库表）
     * @return
     */
    List<T> getByIds(List<Long> idList, Class<T> clazz);
    
    /**
     * 插入一条记录
     * @param entity 实体类对象
     * @param clazz 实体类的类文字向量（通过clazz绑定对应的数据库表）
     * @return
     */
    long insert(T entity, Class<T> clazz);
    
    /**
     * 更新一条数据（全量更新）
     * @param entity
     * @param clazz
     * @return
     */
    long update(T entity, Class<T> clazz);
    
    /**
     * 根据id，更新某一条记录的指定字段（可为一个或多个）
     * 与全量更新的相比，好处是：仅更新需要更新的字段，减少不必要的binlog信息
     * 
     * @param columnList UpdateColumnNode集合（UpdateColumnNode包含：列名、更新列的值等相关信息）
     * @param id 更新记录的id
     * @param clazz 实体类的类文字向量（通过clazz绑定对应的数据库表）
     * @return
     */
    long updateById(List<UpdateColumnNode<?>> columnList, Long id, Class<T> clazz);
    
    
    /**
     * 根据记录id，删除一条记录
     * 
     * @param id
     * @return
     */
    long delete(Long id, Class<T> clazz);
    
    
    
    /**============================================================
     *                     批量处理接口
     ============================================================*/
    
    long batchInsert(List<T> entities, Class<T> clazz);
    
    /**
     * 批量全量更新（不推荐使用）
     * @param entities 更新的实体类对象集合
     * @param clazz 实体类的类文字向量（通过clazz绑定对应的数据库表）
     * @return
     */
    long batchUpdate(List<T> entities, Class<T> clazz);

    /**
     * 批量更新指定的列（推荐）
     * 
     * @param entities 更新的实体类对象集合
     * @param columns 指定需要更新的列名（ 如果为null或 size==0，则全量更新，此时与batchUpdate方法功能一致）
     * @param clazz 实体类的类文字向量（通过clazz绑定对应的数据库表）
     * @return
     */
    long batchUpdateByColumns(List<T> entities, List<String> columns, Class<T> clazz);
    
    
    long batchDelete(List<Long> ids, Class<T> clazz);
    
}
