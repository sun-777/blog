package com.blog.quark.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.blog.quark.annotation.MappingTable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@MappingTable(table = "t_article")
@JsonIgnoreProperties(value = {"handler"})
public class Article implements Entity {

    private static final long serialVersionUID = 536343613581423116L;
    
    // 文章ID
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long articleId;
    // 标题
    private String title;
    // 用户ID （页面显示为用户昵称）
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long author;
    // 文章描述
    private String description;
    // 文章内容ID
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long contentId;
    // 创建时间
    private LocalDateTime createTime;
    
    private Content content;
    
    
    
    public Article() {}
    
    
    public Article(String title, Long author, String description, Long contentId, LocalDateTime createTime) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.contentId = contentId;
        this.createTime = createTime;
    }
    
    
    public Article(Long articleId, String title, Long author, String description, Long contentId, LocalDateTime createTime) {
        this.articleId = articleId;
        this.title = title;
        this.author = author;
        this.description = description;
        this.contentId = contentId;
        this.createTime = createTime;
    }
    
    
    public Long getArticleId() {
        return this.articleId;
    }

    public Article setArticleId(Long articleId) {
        this.articleId = articleId;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public Article setTitle(String title) {
        this.title = title;
        return this;
    }

    public Long getAuthor() {
        return this.author;
    }

    public Article setAuthor(Long author) {
        this.author = author;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public Article setDescription(String description) {
        this.description = description;
        return this;
    }

    public Long getContentId() {
        return this.contentId;
    }

    public Article setContentId(Long contentId) {
        this.contentId = contentId;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public Article setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }
    
    
    public Content getContent() {
        return this.content;
    }

    public Article setContent(Content content) {
        this.content = content;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId, author, contentId, createTime, description, title);
    }

    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Article)) {
            return false;
        }
        Article other = (Article) obj;
        return Objects.equals(articleId, other.articleId) 
                && Objects.equals(author, other.author)
                && Objects.equals(contentId, other.contentId) 
                && Objects.equals(createTime, other.createTime)
                && Objects.equals(description, other.description) 
                && Objects.equals(title, other.title);
    }

    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName())
                .append(" [articleId=").append(articleId)
                .append(", title=").append(title)
                .append(", author=").append(author)
                .append(", description=").append(description)
                .append(", contentId=").append(contentId)
                .append(", createTime=").append(createTime).append("]");
        return builder.toString();
    }


}
