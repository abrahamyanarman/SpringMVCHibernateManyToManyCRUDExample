package com.websystique.springmvc.dao;

import com.websystique.springmvc.model.UserProfilePictures;

import java.util.List;
import java.util.Map;

public interface UserProfilePictureDao {
    Map<Integer,String> findAll();

    UserProfilePictures findById(int id);

    void save(UserProfilePictures user);

    void deleteByUserId(String userId);

}
