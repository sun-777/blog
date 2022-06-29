package com.blog.quark.vo;

import java.time.LocalDateTime;
import java.util.List;

import com.blog.quark.entity.Article;
import com.fasterxml.jackson.annotation.JsonFormat;


public class BlogListVo {
    
    private Pagination pagination;
    private List<BlogOutline> list;
    
    public BlogListVo() {}
    
    public BlogListVo(Pagination pagination, List<BlogOutline> list) {
        this.pagination = pagination;
        this.list = list;
    }
    
    public Pagination getPagination() {
        return this.pagination;
    }

    public BlogListVo setPagination(Pagination pagination) {
        this.pagination = pagination;
        return this;
    }

    public List<BlogOutline> getList() {
        return this.list;
    }

    public BlogListVo setList(List<BlogOutline> list) {
        this.list = list;
        return this;
    }




    public static class BlogOutline {
        // 文章ID
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long articleId;
        // 标题
        private String title;
        //
        private String authorName;
        // 文章描述
        private String description;
        // 创建时间
        private LocalDateTime createTime;
        
        public BlogOutline() {}
        
        public BlogOutline(Article article, String authorName) {
            this.articleId = article.getArticleId();
            this.title = article.getTitle();
            this.authorName = authorName;
            this.description = article.getDescription();
            this.createTime = article.getCreateTime();
        }
        
        public BlogOutline(Long articleId, String title, String authorName, String description, LocalDateTime createTime) {
            this.articleId = articleId;
            this.title = title;
            this.authorName = authorName;
            this.description = description;
            this.createTime = createTime;
        }
        
        public Long getArticleId() {
            return this.articleId;
        }
        public BlogOutline setArticleId(Long articleId) {
            this.articleId = articleId;
            return this;
        }
        public String getTitle() {
            return this.title;
        }
        public BlogOutline setTitle(String title) {
            this.title = title;
            return this;
        }
        public String getAuthorName() {
            return this.authorName;
        }
        public BlogOutline setAuthorName(String authorName) {
            this.authorName = authorName;
            return this;
        }
        public String getDescription() {
            return this.description;
        }
        public BlogOutline setDescription(String description) {
            this.description = description;
            return this;
        }
        public LocalDateTime getCreateTime() {
            return this.createTime;
        }
        public BlogOutline setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }
    }
    
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public static class Pagination {
        private Long currentPage;
        private Long pageSize;
        private Long total;
        
        public Pagination() {}
        
        public Pagination(Long currentPage, Long pageSize) {
            this.currentPage = currentPage;
            this.pageSize = pageSize;
        }

        public Long getCurrentPage() {
            return this.currentPage;
        }

        public Pagination setCurrentPage(Long currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public Long getPageSize() {
            return this.pageSize;
        }

        public Pagination setPageSize(Long pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Long getTotal() {
            return this.total;
        }

        public Pagination setTotal(Long total) {
            this.total = total;
            return this;
        }
        
    }
}
