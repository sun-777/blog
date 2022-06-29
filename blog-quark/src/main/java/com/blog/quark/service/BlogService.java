package com.blog.quark.service;

import java.util.List;

import com.blog.quark.entity.Article;

public interface BlogService {
    long add(Article article) throws RuntimeException;
    List<Article> getByAuthorId(Long author);
    List<Article> get(Long offset, Long limited, String orderByColumn, boolean isAsc);
    Long count();
    Article get(Long articleId);
    long update(Article article) throws RuntimeException;
}
