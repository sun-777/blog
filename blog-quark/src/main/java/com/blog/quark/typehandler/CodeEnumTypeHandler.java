package com.blog.quark.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.blog.quark.common.enumerate.CodeEnum;
import com.blog.quark.common.enumerate.GenderEnum;
import com.blog.quark.common.util.StringUtil;



/**
 *  CodeEnumTypeHandler：用于处理数据库的字段 与 实体类中CodeEnum类型的成员属性的映射封装。 
 *  默认使用CodeEnum类的code属性映射数据库字段。
 *  不使用MyBatis提供的枚举类TypeHandler的原因： 
 *      1、EnumOrdinalTypeHandler： 是按索引值写入数据库，万一枚举类中的枚举对象顺序被误修改，数据库中的写入字段就对应不上了。
 *      2、EnumTypeHandler： 按枚举对象名字写入数据库，不一定满足实际需求。
 *      
 * @author Sun xiaodong
 * 
 */
@MappedTypes({GenderEnum.class})
public class CodeEnumTypeHandler<T extends Enum<T> & CodeEnum<T, C>, C> extends BaseTypeHandler<T> {
    @SuppressWarnings("unused")
    private final Class<T> type;
    private final T[] enums;
    
    public CodeEnumTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        
        this.type = type;
        this.enums = type.getEnumConstants();
        
        if (null == this.enums) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
    }
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        final C c = parameter.code();
        if (jdbcType == null) {
            //判断泛型声明C的类型，调用不同的PreparedStatement方法写入参数。
            if (String.class.isInstance(c)) {
                ps.setString(i, (String) c);
            } else if (Integer.class.isInstance(c)) {
                ps.setInt(i, (int) c);
            } else if (Long.class.isInstance(c)) {
                ps.setLong(i, (long) c);
            } else if (Short.class.isInstance(c)) {
                ps.setShort(i, (short) c);
            } else {
                //throw new IllegalArgumentException("Cannot set parameter because an unknown jdbcType. ");
                //默认写入String类型数据
                ps.setString(i, (String) c);
            }
        } else {
            ps.setObject(i, c, jdbcType.TYPE_CODE);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        final String s = rs.getString(columnName);
        final Optional<T> opt = findEnumByCode(s);
        return opt.isPresent() ? opt.get() : null;
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        final String s = rs.getString(columnIndex);
        final Optional<T> opt = findEnumByCode(s);
        return opt.isPresent() ? opt.get() : null;
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        final String s = cs.getString(columnIndex);
        final Optional<T> opt = findEnumByCode(s);
        return opt.isPresent() ? opt.get() : null;
    }
    
    
    /**
     * 根据字段，与枚举对象中的code属性比较是否相同，来查找对应的枚举对象
     * @param s
     * @return
     */
    @SuppressWarnings("unchecked")
    private Optional<T> findEnumByCode(String s) {
        Optional<T> opt = Optional.empty();
        if (!StringUtil.isEmptyOrWhitespaceOnly(s)) {
            // 构造函数有判断T是否有Enum对象，所以能确保t有值。
            final T t = this.enums[0];
            final C c = t.code();
            
            if (String.class.isInstance(c)) { //C是String类型
                opt = t.codeOf((C) s);
            } else if (Integer.class.isInstance(c)) { //C是Integer类型
                final Integer i = Integer.valueOf(s);
                opt = t.codeOf((C) i);
            } else if (Long.class.isInstance(c)) { //C是Long类型
                final Long l = Long.valueOf(s);
                opt = t.codeOf((C) l);
            } else if (Character.class.isInstance(c)){ //C是Character类型
                final Character ch = Character.valueOf(s.charAt(0));
                opt = t.codeOf((C) ch);
            } else if (Short.class.isInstance(c)){ //C是Short类型
                final Short st = Short.valueOf(s);
                opt = t.codeOf((C) st);
            } else { // C是其它类型，那么默认使用String类型
                //throw new IllegalArgumentException("Cannot convert " + s + " to " + type.getSimpleName() + " by code value.");
                opt = t.codeOf((C) s);
            }
            
        }
        return opt;
    }
    
}
