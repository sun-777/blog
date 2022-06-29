package com.blog.quark.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

import com.blog.quark.annotation.MappingTable;
import com.blog.quark.common.Password;
import com.blog.quark.common.enumerate.GenderEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@MappingTable(table = "t_user")
@JsonIgnoreProperties(value = {"handler"})
public class User implements Entity {

    private static final long serialVersionUID = -3134881933905056898L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    private String email;
    private Password password;
    private String nickname;
    private GenderEnum gender;
    private LocalDate birth;
    private Integer age;
    private String introduction;
    private LocalDateTime createTime;
    private Long profileId;
    private Profile profile;

    
    
    public User() {}
    
    public User(String email, Password password, String nickname, GenderEnum gender, LocalDate birth, String introduction, LocalDateTime createTime, Long profileId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.birth = birth;
        this.introduction = introduction;
        this.createTime = createTime;
        this.profileId = profileId;
    }
    
    public User(Long userId, String email, Password password, String nickname, GenderEnum gender, LocalDate birth, String introduction, LocalDateTime createTime, Long profileId) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.birth = birth;
        this.introduction = introduction;
        this.createTime = createTime;
        this.profileId = profileId;
    }
    

    public Long getUserId() {
        return userId;
    }

    public User setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Password getPassword() {
        return password;
    }

    public User setPassword(Password password) {
        this.password = password;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public User setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public User setGender(GenderEnum gender) {
        this.gender = gender;
        return this;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public User setBirth(LocalDate birth) {
        this.birth = birth;
        // 自动生成年龄
        setAge(getAge(this.birth, LocalDate.now()));
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getIntroduction() {
        return introduction;
    }

    public User setIntroduction(String introduction) {
        this.introduction = introduction;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public User setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }
    

    public Long getProfileId() {
        return profileId;
    }

    public User setProfileId(Long profileId) {
        this.profileId = profileId;
        return this;
    }

    public Profile getProfile() {
        return profile;
    }

    public User setProfile(Profile profile) {
        this.profile = profile;
        return this;
    }

    public Integer getAge() {
        return age;
    }
    
    private void setAge(Integer age) {
        this.age = age;
    }
    
    private Integer getAge(LocalDate begin, LocalDate end) {
        // 自动生成年龄
        final Period period = begin.until(end);
        return (0 == period.getMonths() && 0 == period.getDays()) ? period.getYears() : period.getYears() + 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, birth, createTime, email, gender, introduction, nickname, password, profileId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof User))
            return false;
        User other = (User) obj;
        return Objects.equals(birth, other.birth) 
                && Objects.equals(createTime, other.createTime) 
                && Objects.equals(email, other.email) 
                && gender == other.gender
                && Objects.equals(introduction, other.introduction) 
                && Objects.equals(nickname, other.nickname) 
                && Objects.equals(password, other.password) 
                && Objects.equals(userId, other.userId) 
                && Objects.equals(profileId, other.profileId);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName())
            .append(" [userId=").append(userId)
            .append(", password=").append(password)
            .append(", nickname=").append(nickname)
            .append(", gender=").append(gender)
            .append(", birth=").append(birth)
            .append(", email=").append(email)
            .append(", introduction=").append(introduction)
            .append(", createTime=").append(createTime)
            .append(", profileId=").append(profileId).append("]");
        return builder.toString();
    }


}
