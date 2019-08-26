package com.websystique.springmvc.service;

import com.websystique.springmvc.model.UserProfilePictures;

import java.util.List;
import java.util.Map;

public interface UserProfilePictureService {

    UserProfilePictures findById(int id);

    Map<Integer,String> findAll();

    void save(UserProfilePictures user);

    void deleteByUserId(String userId);

    void updateUserProfilePicture(UserProfilePictures profilePictures);
}
