package com.blog.quark.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.quark.context.resultmap.BaseResultMap;
import com.blog.quark.entity.Article;
import com.blog.quark.entity.Content;
import com.blog.quark.entity.column.UpdateColumnNode;
import com.blog.quark.entity.field.EntityField;
import com.blog.quark.mapper.ArticleMapper;
import com.blog.quark.mapper.ContentMapper;
import com.blog.quark.service.BlogService;


@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ContentMapper contentMapper;
    
    @Transactional
    @Override
    public long add(Article article) throws RuntimeException {
        long ret = 0;
        if (null == article || null == article.getContent()) {
            return ret;
        }
        
        if (1 != (ret = contentMapper.insert(article.getContent(), Content.class))) {
            throw new RuntimeException(String.format("写入数据{ %s }失败", article.getContent()));
        }
        
        article.setContentId(article.getContent().getContentId());
        
        if (1 != (ret = articleMapper.insert(article, Article.class))) {
            throw new RuntimeException(String.format("写入数据{ %s }失败", article));
        }
        return ret;
    }

    @Override
    public List<Article> getByAuthorId(Long author) {
        List<Article> list = articleMapper.getByAuthorId(author);
        return list.isEmpty() ? null : list;
    }

    @Override
    public Article get(Long articleId) {
        return articleMapper.get(articleId, Article.class);
    }

    @Transactional
    @Override
    public long update(Article article) throws RuntimeException {
        long ret = 0;
        if (null == article || null == article.getContent()) {
            return ret;
        }
        
        if (1 != (ret = contentMapper.update(article.getContent(), Content.class))) {
            throw new RuntimeException(String.format("更新数据{ %s }失败", article.getContent()));
        }
        
        
        //写入修改的标题信息
        List<UpdateColumnNode<?>> columnList = new ArrayList<UpdateColumnNode<?>>();
        columnList.add(
                new UpdateColumnNode<>(
                        BaseResultMap.getColumnInfo(EntityField.getQualifiedFieldName(Article::getTitle)), 
                        article.getTitle()
                        )
                );
        //写入修改的内容摘要信息
        columnList.add(
                new UpdateColumnNode<>(
                        BaseResultMap.getColumnInfo(EntityField.getQualifiedFieldName(Article::getDescription)), 
                        article.getDescription()
                        )
                );
        
        // 更新上面的两个字段
        if (1 != (ret = articleMapper.updateById(columnList, article.getArticleId(), Article.class))) {
            throw new RuntimeException(String.format("更新数据{ %s }失败", article));
        }
        return ret;
    }

    @Override
    public List<Article> get(Long offset, Long limit, String orderByColumn, boolean isAsc) {
        if (!(null == offset || offset < 0) && !(null == limit || limit <= 0)) {
            List<Article> list = articleMapper.getRange(offset, limit, orderByColumn, false);
            return list.isEmpty() ? null : list;
        }
        return null;
    }

    @Override
    public Long count() {
        return articleMapper.count();
    }

    
}
