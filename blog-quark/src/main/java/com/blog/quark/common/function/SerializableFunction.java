package com.blog.quark.common.function;

import java.io.Serializable;


@FunctionalInterface
public interface SerializableFunction<T> extends Serializable {
    Serializable apply(T t);
}
