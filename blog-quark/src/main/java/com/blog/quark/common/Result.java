package com.blog.quark.common;

import java.io.Serializable;

import com.blog.quark.common.enumerate.CodeMsg;

public class Result<T> implements Serializable{

    private static final long serialVersionUID = 1133213058406185006L;
    
    private int code;
    private String message;
    // 返回给浏览器的数据
    private T data;
    
    public Result() {}
    
    private Result(T data) {
        this.code = CodeMsg.SUCCESS.code();
        this.message = CodeMsg.SUCCESS.key();
        this.data = data;
    }
    
    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    private Result(CodeMsg codeMsg) {
        if(null != codeMsg) {
            this.code = codeMsg.code();
            this.message = codeMsg.key();
        }
    }
    
    public int getCode() {
        return code;
    }
    
    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public T getData() {
        return data;
    }
    
    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }
    
    public boolean isSuccess() {
        return this.code == CodeMsg.SUCCESS.code();
    }
    
    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }
    
    public static <T> Result<T> error(){
        return new Result<T>(CodeMsg.ERROR);
    }
    
    public static <T> Result<T> error(T data){
        return new Result<T>(CodeMsg.ERROR).setData(data);
    }
    
    
    public static <T> Result<T> success(){
        return new Result<T>(CodeMsg.SUCCESS);
    }
    
    
    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName())
        .append(" [code=").append(code)
        .append(", message=").append(message)
        .append(", data=").append(null == data ? "" : data.toString())
        .append("]");
        
        return sb.toString();
    }
}
