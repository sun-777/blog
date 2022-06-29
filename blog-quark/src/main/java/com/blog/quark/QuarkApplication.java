package com.blog.quark;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.blog.quark.annotation.MappingTableScan;

@SpringBootApplication
//注解启用 AOP
@EnableAspectJAutoProxy
@MapperScan(basePackages = {"com.blog.quark.mapper"})
//扫描包路径下的自定义注解@MappingTable
@MappingTableScan(basePackages = {"com.blog.quark.entity"})

//使用@ServletComponentScan注解后，
//Servlet、Filter、Listener可以直接通过@WebServlet、@WebFilter、@WebListener注解自动注册，无需其他代码
@ServletComponentScan(basePackages= { "com.blog.quark.filter" })
public class QuarkApplication {

    @PostConstruct
    void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
    
    
    public static void main(String[] args) {
        SpringApplication.run(QuarkApplication.class, args);
    }
}
