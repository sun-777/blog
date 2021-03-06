package com.blog.quark.service.impl;

import static com.blog.quark.common.util.Constant.FILE_SEPARATOR;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import com.blog.quark.common.Result;
import com.blog.quark.configure.GlobalConfig;
import com.blog.quark.configure.properties.QuarkProperties.FileServer;
import com.blog.quark.service.FileServerService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;


@Service
public class FileServerServiceImpl implements FileServerService {

    
    @Override
    public Future<Result<?>> download(String src, String dst, CountDownLatch latch){
        return executeTask(downloadTaskWrapper(src, dst, latch));
    }
    
    @Override
    public Future<Result<?>> download(String src, OutputStream dst, CountDownLatch latch) {
        return executeTask(downloadTaskWrapper(src, dst, latch));
    }
    
    @Override
    public Future<Result<?>> upload(String src, String dst, CountDownLatch latch) {
        return executeTask(uploadTaskWrapper(src, dst, latch));
    }
    
    @Override
    public Future<Result<?>> upload(InputStream src, String dst, CountDownLatch latch) {
        return executeTask(uploadTaskWrapper(src, dst, latch));
    }

    @Override
    public Future<Result<?>> move(String src, String dst, CountDownLatch latch) {
        return executeTask(moveTaskWrapper(src, dst, latch));
    }

    @Override
    public Future<Result<?>> delete(String dst, CountDownLatch latch) {
        return executeTask(deleteTaskWrapper(dst, latch));
    }
    
    
    @PreDestroy
    @Override
    public void destroy() {
        ThreadPoolHolder.closeThreadPool();
        // ???????????????????????????????????????SftpChannel
        SftpHolder.closeSftpChannel();
    }
    
    
    
    
    private Future<Result<?>> executeTask(Callable<Result<?>> task) {
        return ThreadPoolHolder.executeTask(task);
    }
    
    
    
    private Callable<Result<?>> deleteTaskWrapper(String dst, CountDownLatch latch){
        return () -> {
            try {
                SftpHolder.getSftpChannel().rm(dst);
                return Result.success();
            } catch (SftpException e) {
                //?????????????????????????????????????????????????????????????????????
                return Result.error(e.getCause().getMessage()).setMessage(dst);
            } finally {
                latch.countDown();
            }
        };
    }
    
    
    private Callable<Result<?>> downloadTaskWrapper(String src, String dst, CountDownLatch latch){
        return () -> {
            try {
                SftpHolder.getSftpChannel().get(src, dst);
                return Result.success();
            } catch (SftpException e) {
                //?????????????????????????????????????????????????????????????????????
                return Result.error(e.getCause().getMessage()).setMessage(src);
            } finally {
                latch.countDown();
            }
        };
    }
    
    
    private Callable<Result<?>> downloadTaskWrapper(String src, OutputStream os, CountDownLatch latch){
        return () -> {
            try {
                SftpHolder.getSftpChannel().get(src, os);
                return Result.success();
            } catch (SftpException e) {
                //?????????????????????????????????????????????????????????????????????
                return Result.error(e.getCause().getMessage()).setMessage(src);
            } finally {
                latch.countDown();
            }
        };
    }
    
    
    private Callable<Result<?>> uploadTaskWrapper(String src, String dst, CountDownLatch latch){
        return () -> {
            try {
                ChannelSftp sftpChannel = SftpHolder.getSftpChannel();
                if (validatePath(sftpChannel, dst)) {
                    sftpChannel.put(src, dst, ChannelSftp.OVERWRITE);
                    return Result.success();
                } else {
                    return Result.error(String.format("Invalid directory path: %s", dst.substring(0, dst.lastIndexOf(FILE_SEPARATOR))));
                }
            } catch (SftpException e) {
                return Result.error(e.getCause().getMessage());
            } finally {
                latch.countDown();
            }
        };
    }
    
    
    private Callable<Result<?>> moveTaskWrapper(String srcFilePath, String dstFilePath, CountDownLatch latch){
        return () -> {
            try {
                ChannelSftp sftpChannel = SftpHolder.getSftpChannel();
                if (validatePath(sftpChannel, dstFilePath)) {
                    sftpChannel.rename(srcFilePath, dstFilePath);
                    return Result.success();
                } else {
                    return Result.error(String.format("Invalid directory path: %s", dstFilePath.substring(0, dstFilePath.lastIndexOf(FILE_SEPARATOR))));
                }
            } catch (SftpException e) {
                return Result.error(e.getCause().getMessage());
            } finally {
                latch.countDown();
            }
        };
    }
    
    
    private Callable<Result<?>> uploadTaskWrapper(InputStream is, String dst, CountDownLatch latch){
        return () -> {
            try {
                ChannelSftp sftpChannel = SftpHolder.getSftpChannel();
                if (validatePath(sftpChannel, dst)) {
                    sftpChannel.put(is, dst, ChannelSftp.OVERWRITE);
                    return Result.success();
                } else {
                    return Result.error(String.format("Invalid directory path: %s", dst.substring(0, dst.lastIndexOf(FILE_SEPARATOR))));
                }
            } catch (SftpException e) {
                return Result.error(e.getCause().getMessage());
            } finally {
                latch.countDown();
            }
        };
    }
    
    
    // ????????????????????????????????????????????????????????????
    private boolean validatePath(ChannelSftp sftpChannel, final String dst) {
        final String dirPath = dst.substring(0, dst.lastIndexOf(FILE_SEPARATOR));
        //???????????????????????????????????????true
        Set<String> pathCache = SftpHolder.getPathCache();
        if (pathCache.contains(dirPath)) {
            return true;
        }
        
        //??????dirPath???????????????dirPath?????????????????????catch????????????????????????
        try {
            sftpChannel.lstat(dirPath);
        } catch (SftpException directoryNotExistException) {
            final String[] folders = dirPath.split(Matcher.quoteReplacement(FILE_SEPARATOR));
            
            // ?????????????????? ??? ????????????????????????????????????
            synchronized (SftpHolder.LOCK) {
                for (String folder : folders) {
                    if (folder.isBlank()) {
                        continue;
                    }
                    try {
                        sftpChannel.cd(folder);
                    } catch (SftpException e) {
                        try {
                            sftpChannel.mkdir(folder);
                            sftpChannel.cd(folder);
                        } catch (SftpException e1) {
                            e1.printStackTrace();
                            return false;
                        }
                    }
                }
                
                try {
                    sftpChannel.cd(FILE_SEPARATOR);
                } catch (SftpException e) {
                    e.printStackTrace();
                }
            }
        }
        return pathCache.add(dirPath);
    }
    
    
    private static final class SftpHolder {
        private static final Object LOCK = new Object();
        private static volatile Session SSH_SESSION;
        private static volatile ChannelSftp SSH_CHANNEL;
        // ??????????????????????????????????????????
        private static volatile Set<String> PATH_CACHE;
        
        private SftpHolder() {}
        
        
        private static Set<String> getPathCache() {
            return Objects.requireNonNull(PATH_CACHE);
        }
        
        
        private static ChannelSftp getSftpChannel() {
            if (null == SSH_CHANNEL) {
                initSftpChannel();
            }
            return SSH_CHANNEL;
        }
        
        
        
        // ???????????????????????????SSH_CHANNEL????????????????????????"/"??? SFTP?????????????????????????????????"/"?????????
        // JSch???????????????????????????????????????????????????????????????????????????"/"?????????????????????????????????working directory???????????????????????????
        // ????????????JSch??????SFTP?????????????????????????????????????????????????????????????????????"/"
        // SFTP????????? Linix??????Unix??????????????????: "/opt/blog/upload/img"
        //           windows???????????????: "/D:/blog/upload/img"
        private static void initSftpChannel() {
            if (null == SSH_SESSION) {
                synchronized (LOCK) {
                    if (null == SSH_SESSION) {
                        final FileServer fileServer = GlobalConfig.getFileServer();
                        if (null != fileServer) {
                            try {
                                JSch jsch = new JSch();
                                SSH_SESSION = jsch.getSession(fileServer.getUsername(), fileServer.getHostname(), fileServer.getPort());
                                // ???????????????????????????SSH
                                if (fileServer.getPassword().isPresent()) {
                                    SSH_SESSION.setPassword(String.copyValueOf(fileServer.getPassword().get()));
                                }
                                
                                // ????????????ECDSA key fingerprint????????????HostKeyChecking
                                SSH_SESSION.setConfig("StrictHostKeyChecking", "no");
                                //??????????????????????????????
                                SSH_SESSION.setTimeout(0);
                                
                                SSH_SESSION.connect();
                                
                                //??????SFTP??????
                                SSH_CHANNEL = (ChannelSftp) SSH_SESSION.openChannel("sftp");
                                SSH_CHANNEL.connect();
                                
                                //??????????????????????????????"/"
                                try {
                                    SSH_CHANNEL.cd(FILE_SEPARATOR);
                                } catch (SftpException e) {
                                    e.printStackTrace();
                                }
                                
                                // ??????CopyOnWriteArraySet?????????????????????
                                PATH_CACHE = new CopyOnWriteArraySet<>();
                            } catch (JSchException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        
        
        
        private static void closeSftpChannel() {
            synchronized (LOCK) {
                if (null != SSH_CHANNEL) {
                    SSH_CHANNEL.disconnect();
                    SSH_CHANNEL = null;
                }
                
                if (null != SSH_SESSION) {
                    SSH_SESSION.disconnect();
                    SSH_SESSION = null;
                }
            }
        }
    }
    
    
    private static final class ThreadPoolHolder {
        private static final String THREAD_NAME_PREFIX = "upload_file_thread_";
        private static final int IO_EVENT_THREAD_NUM = Math.max(1, Runtime.getRuntime().availableProcessors() + 1);
        private static final int MAX_TASK_NUM = 1024;
        private static volatile ExecutorService EXECUTORS;
        
        private ThreadPoolHolder() {}
        
        private static void initThreadPool() {
            if (null == EXECUTORS) {
                synchronized (ThreadPoolHolder.class) {
                    if (null == EXECUTORS) {
                        EXECUTORS = new ThreadPoolExecutor(1, IO_EVENT_THREAD_NUM, 30L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(MAX_TASK_NUM), new ThreadFactory() {
                            private final AtomicInteger seqNo = new AtomicInteger(1);
                            @Override
                            public Thread newThread(Runnable r) {
                                return new Thread(r, THREAD_NAME_PREFIX + seqNo.getAndIncrement());
                            }
                        });
                    }
                }
            }
        }
        
        
        private static Future<Result<?>> executeTask(Callable<Result<?>> task) {
            if (null == EXECUTORS) {
                initThreadPool();
            }
            return EXECUTORS.submit(task);
        }
        
        
        private static void closeThreadPool() {
            if (null != EXECUTORS) {
                synchronized (ThreadPoolHolder.class) {
                    EXECUTORS.shutdown();
                    try {
                        // awaitTermination()??????????????????????????????????????????????????????????????????
                        if (!EXECUTORS.awaitTermination(3000, TimeUnit.MILLISECONDS)) {
                            EXECUTORS.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        EXECUTORS.shutdownNow();
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
    }

}
