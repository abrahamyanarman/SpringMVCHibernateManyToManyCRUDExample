package com.websystique.springmvc.dao;

import com.websystique.springmvc.model.UserProfilePictures;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("userProfilePictureDao")
public class UserProfilePictureDaoImpl extends AbstractDao<Integer, UserProfilePictures>implements UserProfilePictureDao {
    @SuppressWarnings("unchecked")
    public Map<Integer,String> findAll() {
        Criteria crit = createEntityCriteria();
        crit.addOrder(Order.asc("id"));
        List<UserProfilePictures> userProfilePicturesList = (List<UserProfilePictures>)crit.list();
        Map<Integer, String> userProfilePicturesMap = new HashMap<Integer, String>();
        for (UserProfilePictures o : userProfilePicturesList) {
            userProfilePicturesMap.put((Integer) o.getUserId(), (String) o.getPicSrc());
        }
        return userProfilePicturesMap;
    }


    public UserProfilePictures findById(int id) {
        return getByKey(id);
    }

    @Override
    public void save(UserProfilePictures userProfilePictures) {
        persist(userProfilePictures);
    }

    @Override
    public void deleteByUserId(String userId) {
        Criteria crit = createEntityCriteria();
        crit.add(Restrictions.eq("userId", userId));
        UserProfilePictures profilePictures = (UserProfilePictures) crit.uniqueResult();
        delete(profilePictures);
    }


}
