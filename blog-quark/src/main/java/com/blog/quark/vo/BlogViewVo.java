package com.blog.quark.vo;

import java.time.LocalDateTime;

import com.blog.quark.entity.Article;
import com.fasterxml.jackson.annotation.JsonFormat;

public class BlogViewVo {
    // 文章ID
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long articleId;
    // 标题
    private String title;
    
    // 文章作者ID
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long authorId;
    // 作者呢称
    private String authorName;
    // 文章描述
    private String description;
    // 创建时间
    private LocalDateTime createTime;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long contentId;
    // 内容
    private String content;
    
    // 最后一次修改时间
    private LocalDateTime contentUpdateTime;
    // 当前登录用户Id与博客创建作者如果是同一个人，则可编辑
    private boolean editable;
    
    
    public BlogViewVo() {}
    
    
    public BlogViewVo(Article article, String authorName, boolean editable) {
        this.articleId = article.getArticleId();
        this.title = article.getTitle();
        this.authorId = article.getAuthor();
        this.authorName = authorName;
        this.description = article.getDescription();
        this.createTime = article.getCreateTime();
        this.contentId = article.getContentId();
        this.content =  article.getContent().getContent();
        this.contentUpdateTime = article.getContent().getUpdateTime();
        this.editable = editable;
    }
    
    public BlogViewVo(Long articleId, 
            String title, 
            Long authorId, 
            String authorName, 
            String description, 
            LocalDateTime createTime, 
            Long contentId, 
            String content, 
            LocalDateTime contentUpdateTime) {
        this.articleId = articleId;
        this.title = title;
        this.authorId = authorId;
        this.authorName = authorName;
        this.description = description;
        this.createTime = createTime;
        this.contentId = contentId;
        this.content =  content;
        this.contentUpdateTime = contentUpdateTime;
    }

    public Long getArticleId() {
        return this.articleId;
    }

    public BlogViewVo setArticleId(Long articleId) {
        this.articleId = articleId;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public BlogViewVo setTitle(String title) {
        this.title = title;
        return this;
    }

    public Long getAuthorId() {
        return this.authorId;
    }

    public BlogViewVo setAuthorId(Long authorId) {
        this.authorId = authorId;
        return this;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public BlogViewVo setAuthorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public BlogViewVo setDescription(String description) {
        this.description = description;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public BlogViewVo setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public Long getContentId() {
        return this.contentId;
    }

    public BlogViewVo setContentId(Long contentId) {
        this.contentId = contentId;
        return this;
    }

    public String getContent() {
        return this.content;
    }

    public BlogViewVo setContent(String content) {
        this.content = content;
        return this;
    }

    public LocalDateTime getContentUpdateTime() {
        return this.contentUpdateTime;
    }

    public BlogViewVo setContentUpdateTime(LocalDateTime contentUpdateTime) {
        this.contentUpdateTime = contentUpdateTime;
        return this;
    }


    public boolean isEditable() {
        return this.editable;
    }


    public BlogViewVo setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }
    
    
    
}
