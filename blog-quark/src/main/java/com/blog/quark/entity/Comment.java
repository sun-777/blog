package com.blog.quark.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.blog.quark.annotation.MappingTable;
import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * 对文章留言的留言表
 * 
 * @author Sun Xiaodong
 *
 */
@MappingTable(table = "t_comment")
public class Comment implements Entity {

    private static final long serialVersionUID = -8673702935919031647L;

    
    // 留言ID
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long commentId;
    // 文章ID
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long articalId;
    // 留言的用户ID
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    // 留言内容
    private String content;
    // 上级留言ID （当对文章留言时，为空；当对留言评论时，为留言ID）
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long toCommentId;
    // IP地址
    private String ip;
    // 设备
    private String device;
    // 地址
    private String address;
    // 留言时间
    private LocalDateTime createTime;
    
    
    public Comment() {}
    
    public Comment(Long articalId, Long userId, String content, Long toCommentId, String ip, String device, String address, LocalDateTime createTime) {
        this.articalId = articalId;
        this.userId = userId;
        this.content = content;
        this.toCommentId = toCommentId;
        this.ip = ip;
        this.device = device;
        this.address = address;
        this.createTime = createTime;
    }
    
    
    public Comment(Long commentId, Long articalId, Long userId, String content, Long toCommentId, String ip, String device, String address, LocalDateTime createTime) {
        this.commentId = commentId;
        this.articalId = articalId;
        this.userId = userId;
        this.content = content;
        this.toCommentId = toCommentId;
        this.ip = ip;
        this.device = device;
        this.address = address;
        this.createTime = createTime;
    }

    public Long getCommentId() {
        return this.commentId;
    }

    public Comment setCommentId(Long commentId) {
        this.commentId = commentId;
        return this;
    }

    public Long getArticalId() {
        return this.articalId;
    }

    public Comment setArticalId(Long articalId) {
        this.articalId = articalId;
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Comment setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getContent() {
        return this.content;
    }

    public Comment setContent(String content) {
        this.content = content;
        return this;
    }

    public Long getToCommentId() {
        return this.toCommentId;
    }

    public Comment setToCommentId(Long toCommentId) {
        this.toCommentId = toCommentId;
        return this;
    }

    public String getIp() {
        return this.ip;
    }

    public Comment setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getDevice() {
        return this.device;
    }

    public Comment setDevice(String device) {
        this.device = device;
        return this;
    }

    public String getAddress() {
        return this.address;
    }

    public Comment setAddress(String address) {
        this.address = address;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public Comment setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, articalId, commentId, content, createTime, device, ip, toCommentId, userId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Comment))
            return false;
        Comment other = (Comment) obj;
        return Objects.equals(address, other.address) 
                && Objects.equals(articalId, other.articalId)
                && Objects.equals(commentId, other.commentId) 
                && Objects.equals(content, other.content)
                && Objects.equals(createTime, other.createTime) 
                && Objects.equals(device, other.device)
                && Objects.equals(ip, other.ip) 
                && Objects.equals(toCommentId, other.toCommentId)
                && Objects.equals(userId, other.userId);
    }

    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName())
                .append(" [commentId=").append(commentId)
                .append(", articalId=").append(articalId)
                .append(", userId=").append(userId)
                .append(", content=").append(content)
                .append(", toCommentId=").append(toCommentId)
                .append(", ip=").append(ip)
                .append(", device=").append(device)
                .append(", address=").append(address)
                .append(", createTime=").append(createTime)
                .append("]");
        return builder.toString();
    }
    
    
}
