package com.blog.quark.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.blog.quark.annotation.MappingTable;
import com.fasterxml.jackson.annotation.JsonFormat;

@MappingTable(table = "t_profile")
public class Profile implements Entity {
    
    private static final long serialVersionUID = -2531813088884844093L;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long profileId;
    private String profile;
    private LocalDateTime updateTime;
    
    public Profile() {}
    
    public Profile(String profile, LocalDateTime updateTime) {
        this.profile = profile;
        this.updateTime = updateTime;
    }
    
    public Profile(Long profileId, String profile, LocalDateTime updateTime) {
        this.profileId = profileId;
        this.profile = profile;
        this.updateTime = updateTime;
    }

    public Long getProfileId() {
        return profileId;
    }

    public Profile setProfileId(Long profileId) {
        this.profileId = profileId;
        return this;
    }

    public String getProfile() {
        return profile;
    }

    public Profile setProfile(String profile) {
        this.profile = profile;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public Profile setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile, profileId, updateTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Profile)) {
            return false;
        }
        Profile other = (Profile) obj;
        return Objects.equals(profile, other.profile) 
                && Objects.equals(profileId, other.profileId) 
                && Objects.equals(updateTime, other.updateTime);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName())
            .append(" [profileId=").append(profileId)
            .append(", profile=").append(profile)
            .append(", updateTime=").append(updateTime).append("]");
        return builder.toString();
    }


}
