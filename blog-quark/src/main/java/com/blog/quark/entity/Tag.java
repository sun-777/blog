package com.blog.quark.entity;

import java.util.Objects;

import com.blog.quark.annotation.MappingTable;
import com.fasterxml.jackson.annotation.JsonFormat;



@MappingTable(table = "t_tag")
public class Tag implements Entity {

    private static final long serialVersionUID = -2529948070212442987L;

    // 标签ID
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long tagId;
    // 标签名称
    private String tagName;
    
    
    
    public Tag() {}
    
    public Tag(String tagName) {
        this.tagName = tagName;
    }
    
    public Tag(Long tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    
    
    public Long getTagId() {
        return this.tagId;
    }

    public Tag setTagId(Long tagId) {
        this.tagId = tagId;
        return this;
    }

    public String getTagName() {
        return this.tagName;
    }

    public Tag setTagName(String tagName) {
        this.tagName = tagName;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId, tagName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) obj;
        return Objects.equals(tagId, other.tagId) && Objects.equals(tagName, other.tagName);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName())
                .append(" [tagId=").append(tagId)
                .append(", tagName=").append(tagName)
                .append("]");
        return builder.toString();
    }
    
    
}
