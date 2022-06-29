package com.blog.quark.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * 配合vue前端页面，修改请求的url。
 * 当请求的url为/api/a/b?s=q&m=t时，修改为/a/b?s=q&m=t; 即只去掉"/api"，参数全部跟随。
 * @author Sun Xiaodong
 *
 */
@WebFilter
public class UrlFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponseWrapper httpResponse = new HttpServletResponseWrapper((HttpServletResponse) response);
        String path=httpRequest.getRequestURI();
        // VUE.js前端分离使用动态路由功能。
        // 当将VUE.js前端整合到SpringBoot中后，需要单独对路由进行处理，否则将会找不到URL资源
        if(path.indexOf("/api/") >= 0) {
            path = path.replace("/api/", "/");
            httpRequest.getRequestDispatcher(path).forward(request,response);
        } else {
            chain.doFilter(httpRequest, httpResponse);
        }
    }
    
    
    /**
     * tomcat9.0.x 支持 servlet4.0 版本，tomcat8.5.x 支持 servlet3.1版本。
     * 相应的Filter变化是4.0使用default默认方法重写了init与destory方法，而3.1需要实现这两个方法，否则会导致tomcat8启动失败。
     * 
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
