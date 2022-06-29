package com.blog.quark.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import com.blog.quark.common.Result;


/**
 * 文件服务器服务，提供文件上传、下载、删除等操作
 * 
 * @author SUNXDEN
 *
 */
public interface FileServerService {
    
    Future<Result<?>> upload(String src, String dst, CountDownLatch latch);
    Future<Result<?>> upload(InputStream src, String dst, CountDownLatch latch);
    Future<Result<?>> download(String src, String dst, CountDownLatch latch);
    Future<Result<?>> download(String src, OutputStream dst, CountDownLatch latch);
    Future<Result<?>> move(String src, String dst, CountDownLatch latch);
    Future<Result<?>> delete(String dst, CountDownLatch latch);
    
    void destroy();
}
