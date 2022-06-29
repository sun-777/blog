package com.blog.quark.entity;

import com.blog.quark.annotation.MappingTable;


/**
 * Article 和 Category 多对多关联实体类
 * 
 * @author Sun Xiaodong
 *
 */
@MappingTable(table = "t_article_category")
public class ArticleCategory implements Entity {

    private static final long serialVersionUID = 1L;
    // 文章ID
    @SuppressWarnings("unused")
    private Long articleId;
    // 分类ID
    @SuppressWarnings("unused")
    private Long categoryId;
    
    
    @SuppressWarnings("unused")
    private ArticleCategory() {}
    
    public ArticleCategory(Long articleId, Long categoryId) {
        this.articleId = articleId;
        this.categoryId = categoryId;
    }
    
    
    public ArticleCategory setArticleId(Long articleId) {
        this.articleId = articleId;
        return this;
    }
    

    public ArticleCategory setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }
    
}
