package com.blog.quark.entity;

import java.util.Objects;

import com.blog.quark.annotation.MappingTable;
import com.blog.quark.common.enumerate.PreferenceEnum;
import com.fasterxml.jackson.annotation.JsonFormat;


@MappingTable(table = "t_preference")
public class Preference implements Entity {

    private static final long serialVersionUID = 8505015871902393660L;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long preferenceId;
    private String preferenceKey;
    private String preferenceValue;
    
    public Preference() {}
    
    public Preference(PreferenceEnum preferenceEnum) {
        this.preferenceId = preferenceEnum.code();
        this.preferenceKey = preferenceEnum.key();
    }
    
    
    @SuppressWarnings("unused")
    private Preference(Long preferenceId, String preferenceKey, String preferenceValue) {
        this.preferenceId = preferenceId;
        this.preferenceKey = preferenceKey;
        this.preferenceValue = preferenceValue;
    }
    
    
    public Preference(PreferenceEnum preferenceEnum, String preferenceValue) {
        this.preferenceId = preferenceEnum.code();
        this.preferenceKey = preferenceEnum.key();
        this.preferenceValue = preferenceValue;
    }

    public Long getPreferenceId() {
        return preferenceId;
    }

    private Preference setPreferenceId(Long preferenceId) {
        this.preferenceId = preferenceId;
        return this;
    }
    
    public Preference setPreferenceId(PreferenceEnum preferenceEnum) {
        setPreferenceId(preferenceEnum.code());
        return this;
    }

    public String getPreferenceKey() {
        return preferenceKey;
    }

    private Preference setPreferenceKey(String preferenceKey) {
        this.preferenceKey = preferenceKey;
        return this;
    }
    
    
    public Preference setPreferenceKey(PreferenceEnum preferenceEnum) {
        setPreferenceKey(preferenceEnum.key());
        return this;
    }

    public String getPreferenceValue() {
        return preferenceValue;
    }

    public Preference setPreferenceValue(String preferenceValue) {
        this.preferenceValue = preferenceValue;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(preferenceId, preferenceKey, preferenceValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Preference))
            return false;
        Preference other = (Preference) obj;
        return Objects.equals(preferenceId, other.preferenceId) 
                && Objects.equals(preferenceKey, other.preferenceKey) 
                && Objects.equals(preferenceValue, other.preferenceValue);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName())
            .append(" [preferenceId=").append(preferenceId)
            .append(", preferenceKey=").append(preferenceKey)
            .append(", preferenceValue=").append(preferenceValue).append("]");
        return builder.toString();
    }

    
}
