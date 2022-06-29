package com.blog.quark.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.blog.quark.annotation.MappingTable;
import com.fasterxml.jackson.annotation.JsonFormat;


@MappingTable(table = "t_content")
public class Content implements Entity {

    private static final long serialVersionUID = 3930322714510205229L;

    // 文章内容ID
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long contentId;
    // 文章内容（html格式，其中图片文件单独存放在文件服务器中）
    private String content;
    // 修改时间
    private LocalDateTime updateTime;
    
    
    
    public Content() {}
    
    
    public Content(String content, LocalDateTime updateTime) {
        this.content = content;
        this.updateTime = updateTime;
    }
    
    public Content(Long contentId, String content, LocalDateTime updateTime) {
        this.contentId = contentId;
        this.content = content;
        this.updateTime = updateTime;
    }
    
    
    public Long getContentId() {
        return this.contentId;
    }

    public Content setContentId(Long contentId) {
        this.contentId = contentId;
        return this;
    }

    public String getContent() {
        return this.content;
    }

    public Content setContent(String content) {
        this.content = content;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    public Content setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }
    
    
    @Override
    public int hashCode() {
        return Objects.hash(content, contentId, updateTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Content))
            return false;
        Content other = (Content) obj;
        return Objects.equals(content, other.content) 
                && Objects.equals(contentId, other.contentId)
                && Objects.equals(updateTime, other.updateTime);
    }
    

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName())
                .append(" [contentId=").append(contentId)
                .append(", content=").append(content)
                .append(", updateTime=").append(updateTime).append("]");
        return builder.toString();
    }


}
