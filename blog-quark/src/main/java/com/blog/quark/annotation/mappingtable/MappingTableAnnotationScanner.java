package com.blog.quark.annotation.mappingtable;

import com.blog.quark.annotation.MappingTable;
import com.blog.quark.common.util.StringUtil;
import com.blog.quark.configure.GlobalConfig;
import com.blog.quark.entity.Entity;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Objects;
import java.util.Set;

/**
 * 自定义的包扫描器，过滤所有含有自定义注解@MappingTable的类
 * 
 * @author Sun Xiaodong
 *
 */
public class MappingTableAnnotationScanner extends ClassPathBeanDefinitionScanner {
    private final ClassLoader classLoader;
    public MappingTableAnnotationScanner(BeanDefinitionRegistry registry, ClassLoader classLoader) {
        super(registry, false);
        this.classLoader = classLoader;
        registerFilter();
    }

    
    private void registerFilter() {
        //添加过滤条件：有@MappingTable注解的类才会被扫描
        addIncludeFilter(new AnnotationTypeFilter(MappingTable.class));
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        final Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        // 将扫描的指定的注解@MappingTable信息写入GlobalConfig
        beanDefinitionHolders.forEach(it -> {
            BeanDefinition beanDefinition = it.getBeanDefinition();
            Class<?> beanClazz = null;
            try {
                beanClazz = ClassUtils.forName(beanDefinition.getBeanClassName(), classLoader);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (!Entity.class.isAssignableFrom(beanClazz)) {
                // clazz必须是Entity的子类或子接口类，否则抛出异常
                throw new IllegalArgumentException(String.format("\"@MappintTable\" annotation must be used for only a implementation class of \"%s\"", Entity.class.getName()));
            }

            final MappingTable annotation = beanClazz.getAnnotation(MappingTable.class);
            if (Objects.nonNull(annotation)) {
                final String table = annotation.table();
                if (StringUtil.isEmptyOrWhitespaceOnly(table)) {
                    throw new IllegalArgumentException("\"table\" property of @MappintTable annotation must not be empty.");
                }
                GlobalConfig.addMappingTableName((Class<? extends Entity>) beanClazz, table);
            } else {
                throw new IllegalArgumentException("Not found @MappintTable annotation.");
            }
        });
        
        return beanDefinitionHolders;
    }


    /**
     * 重写候选判断逻辑，选出带有注解的接口
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        final AnnotationMetadata metadata = beanDefinition.getMetadata();
        // AnnotationMetadata::isConcrete：是否允许创建（不是接口且不是抽象类）
        if (metadata.isConcrete() && metadata.isIndependent()) {
            try {
                Class<?> target = ClassUtils.forName(metadata.getClassName(), classLoader);
                return !target.isAnnotation();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
