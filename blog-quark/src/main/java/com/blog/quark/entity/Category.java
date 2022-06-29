package com.blog.quark.entity;

import java.util.Objects;

import com.blog.quark.annotation.MappingTable;
import com.fasterxml.jackson.annotation.JsonFormat;



@MappingTable(table = "t_category")
public class Category implements Entity {

    private static final long serialVersionUID = -1180946197241275856L;

    // 分类ID
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long categoryId;
    // 分类名称
    private String categoryName;
    
    
    
    public Category() {}
    
    
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
    
    
    public Category(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
    
    
    
    public Long getCategoryId() {
        return this.categoryId;
    }

    public Category setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public Category setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }
    
    

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, categoryName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Category)) {
            return false;
        }
        Category other = (Category) obj;
        return Objects.equals(categoryId, other.categoryId) 
                && Objects.equals(categoryName, other.categoryName);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName())
                .append(" [categoryId=").append(categoryId)
                .append(", categoryName=").append(categoryName)
                .append("]");
        return builder.toString();
    }


}
