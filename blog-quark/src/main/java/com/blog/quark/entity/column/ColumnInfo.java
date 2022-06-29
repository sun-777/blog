package com.blog.quark.entity.column;

import org.apache.ibatis.type.JdbcType;

import com.blog.quark.common.util.StringUtil;

import static com.blog.quark.common.util.Constant.JDBCTYPE_PREFIX;
import static com.blog.quark.common.util.Constant.JAVATYPE_PREFIX;
import static com.blog.quark.common.util.Constant.TYPEHANDLER_PREFIX;

import java.util.Objects;


/**
 * 
 * 
 * @author Sun Xiaodong
 *
 */
public final class ColumnInfo {
    
    // 更新的属性字段映射到数据库表中的列名
    private String column;
    // 类的字段相关的jdbcType、javaType及typeHandler信息
    private String type;
    
    public ColumnInfo() {}
    
    public ColumnInfo(JdbcType jdbcType, Class<?> javaTypeClazz, Class<?> typeHandlerClazz) {
        setType(jdbcType, javaTypeClazz, typeHandlerClazz);
    }
    
    public String getColumn() {
        return column;
    }
    
    public ColumnInfo setColumn(String name) {
        this.column = name;
        return this;
    }
    
    
    public String getType() {
        return this.type;
    }
    
    
    private ColumnInfo setType(JdbcType jdbcType, Class<?> javaTypeClazz, Class<?> typeHandlerClazz) {
        type = new StringBuilder(256)
                .append(JDBCTYPE_PREFIX).append(jdbcType.name())
                .append(", ")
                .append(JAVATYPE_PREFIX).append(javaTypeClazz.getName())
                .append(", ")
                .append(TYPEHANDLER_PREFIX).append(typeHandlerClazz.getName())
                .toString();
        return this;
    }
    
    
    private void setType(JdbcType jdbcType) {
        type = new StringBuilder(256)
                .append(StringUtil.isEmptyOrWhitespaceOnly(type) ? "" : type.concat(", "))
                .append(JDBCTYPE_PREFIX).append(jdbcType.name())
                .toString();
    }
    
    
    private void setType(Class<?> clazz, boolean isJavaType) {
        type = new StringBuilder(256)
                .append(StringUtil.isEmptyOrWhitespaceOnly(type) ? "" : type.concat(", "))
                .append(isJavaType ? JAVATYPE_PREFIX : TYPEHANDLER_PREFIX)
                .append(clazz.getName())
                .toString();
    }
    
    
    public ColumnInfo setJdbcType(JdbcType jdbcType) {
        setType(jdbcType);
        return this;
    }
    
    public ColumnInfo setJavaType(Class<?> javaTypeClazz) {
        setType(javaTypeClazz, true);
        return this;
    }
    
    public ColumnInfo setTypeHandler(Class<?> typeHandlerClazz) {
        setType(typeHandlerClazz, false);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ColumnInfo))
            return false;
        ColumnInfo other = (ColumnInfo) obj;
        return Objects.equals(column, other.column) 
                && Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [column=").append(column).append(", type=").append(type).append("]");
        return builder.toString();
    }
    
}
