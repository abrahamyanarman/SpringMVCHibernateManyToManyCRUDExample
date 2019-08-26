package com.websystique.springmvc.controller;


import com.websystique.springmvc.model.UserProfile;
import com.websystique.springmvc.model.UserProfilePictures;
import com.websystique.springmvc.service.UserProfilePictureService;
import com.websystique.springmvc.service.UserProfileService;
import com.websystique.springmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.CoverPhoto;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookObject;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/*TODO Add this Controller in your app for Facebook singin/singup*/
@Controller
public class FacebookController {

    private static final ResourceBundle resource = ResourceBundle.getBundle("social-cfg");


    private FacebookConnectionFactory factory = new FacebookConnectionFactory(resource.getString("facebook.app.id"),resource.getString("facebook.app.secret"));


    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserProfilePictureService userProfilePictureService;

    @Autowired
    UserProfileService userProfileService;



    @RequestMapping("/loginWithFacebook")
    public String singinWithFacebook(){
        OAuth2Operations operations = factory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        /*TODO register this redirect uri in Facebook api*/
        params.setRedirectUri("http://localhost:8080/returnFromFacebook");
        params.setScope(resource.getString("facebook.scope"));


        String url = operations.buildAuthorizeUrl(params);

        return "redirect:"+url;
    }

    @RequestMapping("/returnFromFacebook")
    public String getDataFromFacebook(@RequestParam("code") String authorizationCode, Model model){

        OAuth2Operations operations = factory.getOAuthOperations();
        /*TODO you should to set  here redirect uri */
        AccessGrant accessGrant = operations.exchangeForAccess(authorizationCode,"http://localhost:8080/returnFromFacebook",null);

        Connection<Facebook> connection =  factory.createConnection(accessGrant);


        Facebook facebook = (Facebook)connection.getApi();

        String [] fields = {"id", "about", "age_range", "birthday", "context", "cover", "currency", "devices", "education", "email",
                "favorite_athletes", "favorite_teams", "first_name", "gender", "hometown", "inspirational_people", "installed", "install_type",
                "is_verified", "languages", "last_name", "link", "locale", "location", "meeting_for", "middle_name", "name", "name_format",
                "political", "quotes", "payment_pricepoints", "relationship_status", "religion", "security_settings", "significant_other",
                "sports", "test_group", "timezone", "third_party_id", "updated_time", "verified", "video_upload_limits", "viewer_can_send_gift",
                "website", "work"};

        User userProfile =  facebook.fetchObject("me",User.class,fields);

        String firstName = userProfile.getFirstName();
        String lastName = userProfile.getLastName();
        String email =  userProfile.getEmail();
        String profilePictureSrc =  "http://graph.facebook.com/" + userProfile.getId() + "/picture?type=square";
        String userProfileId = userProfile.getId();
        String password = "1234"+userProfileId+"4321";
        String encrytedPassword = passwordEncoder.encode(password);

        com.websystique.springmvc.model.User user  = userService.findBySSO(email);
        UserProfilePictures pictures = new UserProfilePictures();
        if (user==null){
            user = new com.websystique.springmvc.model.User();
            user.setPassword(encrytedPassword);
            user.setSsoId(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);


            Set<UserProfile> userProfiles = new HashSet<>();
            userProfiles.add(userProfileService.findByType("ADMIN"));
            user.setUserProfiles(userProfiles);
            userService.saveUser(user);

            pictures.setUserId(user.getId());
            pictures.setPicSrc(profilePictureSrc);
            userProfilePictureService.save(pictures);
        }




        pictures = userProfilePictureService.findById(user.getId());


        model.addAttribute("user",user);
        model.addAttribute("picture",pictures);

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getSsoId(),user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);



        return "redirect:/list";

    }



}
