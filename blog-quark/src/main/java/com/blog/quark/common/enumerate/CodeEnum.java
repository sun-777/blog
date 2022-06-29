package com.blog.quark.common.enumerate;

import java.util.Arrays;
import java.util.Optional;

/**
 *  See: https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1.2
 *  AdditionalBound:
 *      & InterfaceType
 * 
 *  The enum values are initialized before any other static fields.
 * 
 *  @author Sun xiaodong
 */
public interface CodeEnum<T extends Enum<T> & CodeEnum<T, C>, C> {
    C code();
    
    /**
     * 根据CodeEnum的code()值，枚举对象
     * @param code
     * @return
     */
    Optional<T> codeOf(C c);
    
    /**
     * 根据CodeEnum的code()值，枚举对象
     * @param <T> 泛型声明
     * @param <C> 泛型声明
     * @param enumClass 枚举类型类T
     * @param c  
     * @return 如果枚举类T中有c值，则返回c值对应的枚举类T的Optional对象； 没有则返回Optional的空的实例。
     */
    static <T extends Enum<T> & CodeEnum<T, C>, C> Optional<T> codeOf(Class<T> enumClass, C c) {
        /**
         *  String.class.isInstance(c) 表示： 对象c能否强转为String类
         *  此处是泛型对象，只能使用isInstance判断。（isInstance方法是instanceof运算符的 动态等效方法。）
         */
        if (String.class.isInstance(c)) {   //如果是String类型，则不分大小写进行比较
            return Arrays.stream(enumClass.getEnumConstants()).filter(e -> { return ( ((String) e.code()).equalsIgnoreCase(((String) c))); }).findAny();
        } else {
            return Arrays.stream(enumClass.getEnumConstants()).filter(e -> e.code().equals(c)).findAny();
        }
    }
}
