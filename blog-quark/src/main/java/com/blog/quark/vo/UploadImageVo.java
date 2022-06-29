package com.blog.quark.vo;

import java.util.List;

// wangeditor(version: v4) 富文本编辑器上传图片后，返回的数据格式，
//  详见 https://www.wangeditor.com/doc/pages/07-%E4%B8%8A%E4%BC%A0%E5%9B%BE%E7%89%87/01-%E9%85%8D%E7%BD%AE%E6%9C%8D%E5%8A%A1%E7%AB%AF%E6%8E%A5%E5%8F%A3.html
public class UploadImageVo {
    
    // 0表示成功。非零代表error
    private Integer errno;
    private List<UploadImageResult> data;
    
    public UploadImageVo() {}
    
    
    public Integer getErrno() {
        return this.errno;
    }


    public UploadImageVo setErrno(Integer errno) {
        this.errno = errno;
        return this;
    }


    public List<UploadImageResult> getData() {
        return this.data;
    }

    public UploadImageVo setData(List<UploadImageResult> data) {
        this.data = data;
        return this;
    }




    public static class UploadImageResult {
        private String url;
        private String alt;
        // 此链接应为文件服务器下载文件请求
        private String href;
        
        public UploadImageResult() {
        }
        
        public UploadImageResult(String url) {
            this.url = url;
        }
        
        public UploadImageResult(String url, String alt) {
            this.url = url;
            this.alt = alt;
        }

        public String getUrl() {
            return this.url;
        }

        public UploadImageResult setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getAlt() {
            return this.alt;
        }

        public UploadImageResult setAlt(String alt) {
            this.alt = alt;
            return this;
        }

        public String getHref() {
            return this.href;
        }

        public UploadImageResult setHref(String href) {
            this.href = href;
            return this;
        }
        
    }
}
