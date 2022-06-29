package com.blog.quark.entity.field;

import java.beans.Introspector;
import java.io.File;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

import com.blog.quark.common.function.SerializableFunction;


/**
 * 通过实体类的getter成员方法引用，获取getter方法对应的fieldName
 * 
 * @author Sun Xiaodong
 *
 */
public final class EntityField {
    //private static final Logger LOG = LoggerFactory.getLogger(ClassFields.class);
    private static final String GET_PREFIX = "get";
    private static final String IS_PREFIX = "is";
    private static final String LAMBDA_PREFIX = "lambda$";
    private static final String WRITE_REPLACE_METHOD_NAME = "writeReplace";
    private static final Map<String, String> CACHE = new ConcurrentHashMap<>(32);
    
    private EntityField() {}
    
    public static <T> String getQualifiedFieldName(SerializableFunction<T> serializable) {
        return getQualifiedFieldName0(serializable);
    }
    
    
    // 获取 SerializedLambda 对象
    private static SerializedLambda getSerializedLambda(Serializable serializable) throws ReflectiveOperationException, RuntimeException {
        final Method method = serializable.getClass().getDeclaredMethod(WRITE_REPLACE_METHOD_NAME);
        method.setAccessible(true);
        return (SerializedLambda) method.invoke(serializable);
    }
    
    
    // 获取全限定字段名：全限定类名 + "." + 类的字段名
    private static <T> String getQualifiedFieldName0(SerializableFunction<T> serializable) {
        try {
            final SerializedLambda serializedLambda = getSerializedLambda(serializable);
            String fieldName = null;
            String implMethodName = serializedLambda.getImplMethodName();
            if (implMethodName.startsWith(GET_PREFIX) ) {
                fieldName = Introspector.decapitalize(implMethodName.replace(GET_PREFIX, ""));
            } else if (implMethodName.startsWith(IS_PREFIX)) {
                fieldName = Introspector.decapitalize(implMethodName.replace(IS_PREFIX, ""));
            } else if (implMethodName.startsWith(LAMBDA_PREFIX)) {
                throw new IllegalArgumentException("SerializableFunction can't passing Lambda expressions，can only use method reference.");
            } else {
                throw new IllegalArgumentException(implMethodName + " is not a JavaBean getter method reference.");
            }
            
            final String declaredClass = serializedLambda.getImplClass();
            if (!CACHE.containsKey(declaredClass)) {
                final int lastSeparatorIndex = declaredClass.lastIndexOf(File.separator);
                // 确定当前的路径分隔符（"\\" 或 "/"），并替换为"."
                final String currentSeparator = -1 == lastSeparatorIndex ? ("/".equals(File.separator) ? "\\" : "/") : File.separator;
                String implClassName = declaredClass.replaceAll(Matcher.quoteReplacement(currentSeparator), Matcher.quoteReplacement("."));
                CACHE.putIfAbsent(declaredClass, implClassName);
            }
            // 返回全限定字段名。
            //     e.g.: com.example.mybatis.entity.User.name
            return CACHE.get(declaredClass).concat(".").concat(fieldName);
        } catch (RuntimeException | ReflectiveOperationException e) {
            throw new RuntimeException(e.getCause());
        }
    }
    
    
}
