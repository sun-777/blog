package com.blog.quark.annotation.mappingtable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import com.blog.quark.annotation.MappingTableScan;
import com.blog.quark.common.util.StringUtil;



public class MappingTableAnnotationScanerRegister implements ImportBeanDefinitionRegistrar, BeanClassLoaderAware {

    private ClassLoader classLoader;
    
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(MappingTableScan.class.getName()));
        
        if (Objects.nonNull(attributes)) {
            Set<String> packages = getScanningPackages(attributes);
            if (packages.isEmpty()) {
                packages.add(getDefaultBasePackage(annotationMetadata));
            }
            
            // 自定义的包扫描器
            MappingTableAnnotationScanner scanner = new MappingTableAnnotationScanner(registry, this.classLoader);
            scanner.setBeanNameGenerator(AnnotationBeanNameGenerator.INSTANCE);
            scanner.scan(packages.toArray(String[]::new));
        }

    }
    

    private Set<String> getScanningPackages(AnnotationAttributes attributes) {
        Set<String> packages = new HashSet<>();
        if (null != attributes) {
            addPackages(packages, Arrays.stream(attributes.getStringArray("value")).filter(StringUtil::hasText).collect(Collectors.toSet()));
            addPackages(packages, Arrays.stream(attributes.getStringArray("basePackages")).filter(StringUtil::hasText).collect(Collectors.toSet()));
            addClasses(packages, Arrays.stream(attributes.getClassArray("basePackageClasses")).map(ClassUtils::getPackageName).collect(Collectors.toSet()));
        }
        return packages;
    }
    
    
    private static void addPackages(Set<String> packages, Set<String> values) {
        if (Objects.nonNull(packages) && Objects.nonNull(values) && !values.isEmpty()) {
            Collections.addAll(packages, values.toArray(new String[0]));
        }
    }

    private static void addClasses(Set<String> packages, Set<String> values) {
        if (Objects.nonNull(packages) && Objects.nonNull(values) && !values.isEmpty()) {
            values.forEach(val -> packages.add(ClassUtils.getPackageName(val)));
        }
    }

    private static String getDefaultBasePackage(AnnotationMetadata annotationMetadata) {
        return ClassUtils.getPackageName(annotationMetadata.getClassName());
    }

}
