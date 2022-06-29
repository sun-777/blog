package com.blog.quark.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blog.quark.common.Result;
import com.blog.quark.common.enumerate.CodeMsg;
import com.blog.quark.configure.GlobalConfig;
import com.blog.quark.service.FileServerService;
import com.blog.quark.vo.UploadImageVo;
import com.blog.quark.vo.UploadImageVo.UploadImageResult;


@RestController
public class RichTextEditorController implements BaseController {
    @Autowired
    private FileServerService uploadService;
    
    @PostMapping("/upload/image")
    // 富文本编辑器图片上传
    // "file"为wangeditor上传图片配置中指定的名称，需要联动修改。
    public UploadImageVo uploadRichTextImages(@RequestParam("file") MultipartFile[] multipartFiles){
        if (null != multipartFiles) {
            List<UploadImageResult> uploadImageResultList = new ArrayList<>();
            for (int i = 0, size = multipartFiles.length; i < size; ++i) {
                String originalFileName = multipartFiles[i].getOriginalFilename();
                String dstFileName = GlobalConfig.getDefaultUploadFileName(originalFileName);
                
                // 上传图片的完整路径
                // 注意: 上传富文本编辑器图片时，getUploadImageFullPath的第二个参数isConvertPath必须设置为false。
                //      其他任何情况下，默认此参数为true
                String uploadImageFullPath = GlobalConfig.getUploadImageFullPath(dstFileName, false);
                try (InputStream inputStream = multipartFiles[i].getInputStream();){
                    CountDownLatch latch = new CountDownLatch(1);
                    Future<Result<?>> future = uploadService.upload(inputStream, uploadImageFullPath, latch);
                    //等待返回结果
                    latch.await();
                    // 获取上传结果
                    Result<?> result = future.get();
                    if (null != result && result.getCode() == CodeMsg.SUCCESS.code()) {
                        String imageHttpUrl = GlobalConfig.getHttpRefByImageName(dstFileName, false);
                        uploadImageResultList.add(new UploadImageResult(imageHttpUrl));
                    }
                } catch (IOException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return new UploadImageVo().setErrno(0).setData(uploadImageResultList);
        }
        return new UploadImageVo().setErrno(1);
    }
    
}
