package com.websystique.springmvc.service;

import com.websystique.springmvc.dao.UserProfilePictureDao;
import com.websystique.springmvc.model.UserProfilePictures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("userProfilePictureService")
@Transactional
public class UserProfilePictureServiceImpl implements UserProfilePictureService {

    @Autowired
    UserProfilePictureDao userProfilePictureDao;

    @Override
    public UserProfilePictures findById(int id) {
        return userProfilePictureDao.findById(id);
    }

    @Override
    public Map<Integer,String> findAll() {
        return userProfilePictureDao.findAll();
    }

    @Override
    public void save(UserProfilePictures profilePictures) {
        userProfilePictureDao.save(profilePictures);
    }

    @Override
    public void deleteByUserId(String userId) {
    userProfilePictureDao.deleteByUserId(userId);
    }

    @Override
    public void updateUserProfilePicture(UserProfilePictures profilePictures) {
        UserProfilePictures entity = userProfilePictureDao.findById(profilePictures.getUserId());
        if (entity!=null){
            entity.setPicSrc(profilePictures.getPicSrc());
            userProfilePictureDao.save(entity);
        }
    }
}
