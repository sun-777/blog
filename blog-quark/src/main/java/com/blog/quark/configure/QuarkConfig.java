package com.blog.quark.configure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.blog.quark.configure.properties.QuarkProperties;

@Configuration
@EnableConfigurationProperties(QuarkProperties.class)
public class QuarkConfig {

}
