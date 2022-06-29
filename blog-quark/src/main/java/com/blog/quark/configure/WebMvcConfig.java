package com.blog.quark.configure;

import static com.blog.quark.common.util.Constant.RELOGIN;
import static com.blog.quark.common.util.Constant.TOKEN_JWT;
import static com.blog.quark.common.util.Constant.TOKEN_JWT_PREFIX;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.blog.quark.common.util.JwtUtil;
import com.blog.quark.common.util.JwtUtil.TokenStatus;


/**
 *  <p>全局定制化Spring Boot 的 MVC 特性。</p>
 *  
 *  <p>在Springboot2.0.0之前继承WebMvcConfigurerAdapter类，重写addInterceptors方法；
 *  在springboot2.0.0之后实现WebMvcConfigurer接口，重写addInterceptors方法</p>
 * 
 *  <p>不建议使用继承WebMvcConfigurationSupport类去注册自定义的拦截器，
 *  因为WebMvc自动配置类WebMvcAutoConfiguration头部有注解：@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)</p>
 * 
 *  <p>Springboot已经实例化了WebMvcConfigurationSupport，所以自定义的WebMvcConfigurer实现类不需要添加@EnableWebMvc；
 *  如果添加注解@EnableWebMvc，则默认的WebMvcConfigurationSupport配置类不生效，而是以用户定义的类为主，一般不建议覆盖默认的WebMvcConfigurationSupport类。</p>
 *  <p>当WebMvcAutoConfiguration不生效时，会有以下问题：</p>
 *  <p>1、WebMvcProperties和ResourceProperties失效。</p>
 *  <p>2、类路径上的HttpMessageConverter失效。</p>
 * 
 * @author Sun Xiaodong
 *
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private List<String> excludeUrls(){
        List<String> excludeUrlList = new ArrayList<>();
        excludeUrlList.add("/js/**");
        excludeUrlList.add("/css/**");
        excludeUrlList.add("/img/**");
        excludeUrlList.add("/fonts/**");
        excludeUrlList.add("/static/**");
        excludeUrlList.add("/redirect/**");
        excludeUrlList.add("/login");
        excludeUrlList.add("/index");
        excludeUrlList.add("/quark");
        excludeUrlList.add("/register");
        excludeUrlList.add("/logout");
        excludeUrlList.add("/error");
        excludeUrlList.add("/upload/image");
        excludeUrlList.add("/index.html");

        return excludeUrlList;
    }
    
    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
    
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor())
                // 拦截其他的所有请求
                .addPathPatterns("/**")
                // 排除拦截的请求
                .excludePathPatterns(excludeUrls());
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 增加访问静态资源路径。即：访问http://localhost:8089/static/** 都会跳转到 classpath:/static/
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }


    // 跨域支持
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // "Access-Control-Allow-Headers"
                .allowedHeaders("x-requested-with,content-type")
                // "Access-Control-Allow-Origin"
                //.allowedOrigins("*")
                .allowedOriginPatterns("*")
                // "Access-Control-Allow-Credentials"
                .allowCredentials(true)
                // "Access-Control-Allow-Methods"
                .allowedMethods("GET", "POST", "HEAD", "DELETE", "PUT", "PATCH", "OPTIONS")
                .maxAge(3600 * 24);
    }



    static class AuthenticationInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            if ("OPTIONS".equals(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                String token = request.getHeader(TOKEN_JWT);
                if (null == token || token.isBlank() || !token.startsWith(TOKEN_JWT_PREFIX)) {
                    // 无token或非法token，告诉前端重新登录
                    response.addHeader("Access-Control-Expose-Headers", RELOGIN);
                    response.setHeader(RELOGIN, "/login");
                    return false;
                }
                token = token.replace(TOKEN_JWT_PREFIX, "").strip();
                if (TokenStatus.VALID != JwtUtil.verifyToken(token)) {
                    // 无效token，告诉前端重新登录
                    response.addHeader("Access-Control-Expose-Headers", RELOGIN);
                    response.setHeader(RELOGIN, "/login");
                    return false;
                }
                // 校验成功，则重新刷新token，并写回responseHeader中
                response.addHeader("Access-Control-Expose-Headers", TOKEN_JWT);
                response.setHeader(TOKEN_JWT, JwtUtil.refreshToken(token));
            }
            return true;
        }
    }
    
    
}
