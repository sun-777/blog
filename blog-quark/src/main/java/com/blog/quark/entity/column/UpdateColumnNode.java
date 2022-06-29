package com.blog.quark.entity.column;


/**
 * 当向数据库更新数据时，类字段对应的数据库列名相关信息
 * @author Sun xiaodong
 * 
 */
public class UpdateColumnNode<T> {
    
    // 指定字段对应的数据库列名信息
    private ColumnInfo columnInfo;
    // 字段对应的列名 要更新的值
    private T value;
    
    
    public UpdateColumnNode () {}
    
    public UpdateColumnNode (ColumnInfo columnInfo, T value) {
        this.columnInfo = columnInfo;
        this.value = value;
    }
    
    
    public T getValue() {
        return value;
    }
    
    public UpdateColumnNode<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    public UpdateColumnNode<T> setColumnInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
        return this;
    }
    
}
