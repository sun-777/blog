package com.blog.quark.id.generator;

public interface IdGenerator<T> {
    /**
     * ID生成类型
     * 
     * @return type
     */
    Class<T> getType();
    
    /**
     * 生成Id.
     * 
     * @return generated Id
     */
    long generateId();
}
