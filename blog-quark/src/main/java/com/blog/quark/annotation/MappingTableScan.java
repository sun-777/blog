package com.blog.quark.annotation;

import com.blog.quark.annotation.mappingtable.MappingTableAnnotationScanerRegister;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
//通过类MappingTableAnnotationScanerRegister来扫描@MappingTable注解
@Import(MappingTableAnnotationScanerRegister.class)
public @interface MappingTableScan {
    @AliasFor("basePackages")
    String[] value() default {};
    
    @AliasFor("value")
    String[] basePackages() default {};
    
    Class<?>[] basePackageClasses() default {};
    
    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

}
