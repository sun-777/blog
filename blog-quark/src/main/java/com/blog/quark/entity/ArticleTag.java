package com.blog.quark.entity;

import com.blog.quark.annotation.MappingTable;


/**
 * Article 和 Tag 多对多关联实体类
 * 
 * @author Sun Xiaodong
 *
 */
@MappingTable(table = "t_article_tag")
public class ArticleTag implements Entity {

    private static final long serialVersionUID = 1L;
    // 文章ID
    @SuppressWarnings("unused")
    private Long articleId;
    // 标签ID
    @SuppressWarnings("unused")
    private Long tagId;
    
    
    @SuppressWarnings("unused")
    private ArticleTag() {}
    
    
    public ArticleTag(Long articleId, Long tagId) {
        this.articleId = articleId;
        this.tagId = tagId;
    }


    public ArticleTag setArticleId(Long articleId) {
        this.articleId = articleId;
        return this;
    }


    public ArticleTag setTagId(Long tagId) {
        this.tagId = tagId;
        return this;
    }
    
    
}
