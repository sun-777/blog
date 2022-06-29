package com.blog.quark.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.blog.quark.common.enumerate.PreferenceEnum;
import com.blog.quark.configure.GlobalConfig;
import com.blog.quark.configure.properties.QuarkProperties;
import com.blog.quark.context.resultmap.BaseResultMap;
import com.blog.quark.entity.Preference;
import com.blog.quark.id.generator.SnowflakeId;
import com.blog.quark.service.PreferenceService;

/**
 *  当SpringBoot容器初始化完成之后，执行一些必要的初始化
 *  
 * @author Sun Xiaodong
 *
 */
@Component
public class ConfigurationInitializer implements ApplicationListener<ContextRefreshedEvent>{

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        
        // 初始化BaseResultMap
        BaseResultMap.initialize(Objects.requireNonNull(applicationContext.getBean(SqlSessionFactory.class)).getConfiguration());
        
        QuarkProperties properties = Objects.requireNonNull(applicationContext.getBean(QuarkProperties.class));
        // 初始化雪花Id生成器的WorkId
        initializeSnowflakeId(properties.getKeyGenerator());
        
        
        // 初始化文件服务器设置
         GlobalConfig.setFileServer(properties.getFileServer());
        // 初始化加密算法配置
         GlobalConfig.setCrypto(properties.getCrypto());
        // 初始化PreferenceSetting
        PreferenceService preferenceService = Objects.requireNonNull(applicationContext.getBean(PreferenceService.class));
        GlobalConfig.setPreference(initializePreference(preferenceService));
    }
    
    
    private void initializeSnowflakeId(QuarkProperties.KeyGenerator keyGenerator) {
        if (null != keyGenerator) {
            QuarkProperties.Snowflake snowflake = keyGenerator.getSnowflake();
            if (null != snowflake) {
                SnowflakeId.setWorkId(snowflake.getWorkId());
            }
        }
    }
    
    
    
    private Map<PreferenceEnum, Preference> initializePreference(final PreferenceService preferenceService) {
        List<Preference> list = preferenceService.getAll();
        final PreferenceEnum[] enums = PreferenceEnum.values();
        if (null == list || list.isEmpty()) {  // 数据库没有配置信息
            if (null == list) {
                list = new ArrayList<>();
            }
            
            for (PreferenceEnum e : enums) {
                list.add(new Preference(e, e.value()));
            }
            // 将默认的配置信息写入数据库
            try {
                preferenceService.add(list);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } else {  // 数据库有配置信息，则核验
            List<Preference> invalidPreferenceList = new ArrayList<>();
            Iterator<Preference> iter = list.iterator();
            while (iter.hasNext()) {
                Preference preference =iter.next();
                if (enums[0].codeOf(preference.getPreferenceId()).isEmpty()) {
                    invalidPreferenceList.add(preference);
                    iter.remove();
                }
            }
            
            //删除PreferenceEnum中不存在的配置
            if (!invalidPreferenceList.isEmpty()) {
                try {
                    preferenceService.delete(invalidPreferenceList);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
            
            //比对所有的PreferenceEnum枚举对象，如果存在未写入数据库的枚举对象，则写入数据库；并存入List
            Set<Long> preferenceIdSet = list.stream().map(u -> u.getPreferenceId()).collect(Collectors.toSet());
            List<Preference> nonExist = Arrays.asList(enums).stream()
                    .filter(u -> !preferenceIdSet.contains(u.code()))
                    .map(u -> new Preference(u, u.value()))
                    .collect(Collectors.toUnmodifiableList());
            if (!nonExist.isEmpty()) {
                try {
                    preferenceService.add(nonExist);
                    list.addAll(nonExist);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return list.stream()
                .filter(e -> enums[0].codeOf(e.getPreferenceId()).isPresent())
                .collect(Collectors.toMap(e -> enums[0].codeOf(e.getPreferenceId()).get(), Function.identity()));
    }

}
