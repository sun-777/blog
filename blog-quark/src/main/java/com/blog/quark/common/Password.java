package com.blog.quark.common;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import com.blog.quark.common.util.StringUtil;

public class Password implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private static final Password NONE = new Password(new char[] {});
    private final char[] thePassword;
    
    private Password(final char[] thePassword) {
        this.thePassword = thePassword;
    }
    
    private Password(final String thePassword) {
        this.thePassword = thePassword.toCharArray();
    }
    
    public static Password of(String passwordAsString) {
        return Optional.ofNullable(passwordAsString)
                .filter(StringUtil::hasText)
                .map(it -> new Password(it.toCharArray()))
                .orElseGet(Password::none);
    }
    
    
    public static Password of(final char[] passwordAsChars) {
        return Optional.ofNullable(passwordAsChars)
                .filter(it -> { return !(null == it || 0 == Array.getLength(it)); })
                .map(it -> new Password(Arrays.copyOf(it, it.length)))
                .orElseGet(Password::none);
    }
    
    
    public static Password none() {
        return NONE;
    }
    
    
    public boolean isPresent() {
        return !(null == thePassword || 0 == Array.getLength(thePassword));
    }
    
    
    public char[] get() throws NoSuchElementException {
        if (isPresent()) {
            return Arrays.copyOf(thePassword, thePassword.length);
        }
        throw new NoSuchElementException("No password present.");
    }
    
    public <R> Optional<R> map(Function<char[], R> mapper) {
        if (null == mapper) {
            throw new IllegalArgumentException("Mapper function must not be null!");
        }
        
        return toOptional().map(mapper);
    }
    
    
    public Optional<char[]> toOptional() {
        return Optional.ofNullable(isPresent() ? null : get());
    }
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(thePassword);
        return result;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Password))
            return false;
        Password other = (Password) obj;
        return isEqual(thePassword, other.thePassword);
    }
    
    
    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), isPresent() ? "*****" : "<none>");
    }
    
    
    private boolean isEqual(char[] aChars, char[] bChars) {
        if (aChars.length != bChars.length) {
            return false;
        }
        // 防止计时攻击
        int equal = 0;
        for (int i = 0, length = bChars.length; i < length; i++) {
            equal |= aChars[i] ^ bChars[i];
        }
        return equal == 0;
    }
}
