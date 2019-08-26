package com.websystique.springmvc.model;

import javax.persistence.*;

@Entity
@Table(name="user_profile_picture")
public class UserProfilePictures {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name="pic_src", unique=true, nullable=false)
    private String picSrc;

    @Column(name="user_id", unique=true, nullable=false)
    private Integer userId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPicSrc() {
        return picSrc;
    }

    public void setPicSrc(String picSrc) {
        this.picSrc = picSrc;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
