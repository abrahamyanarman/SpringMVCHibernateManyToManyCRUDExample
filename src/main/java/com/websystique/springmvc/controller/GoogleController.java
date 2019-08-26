package com.websystique.springmvc.controller;

import com.websystique.springmvc.model.User;
import com.websystique.springmvc.model.UserProfile;
import com.websystique.springmvc.model.UserProfilePictures;
import com.websystique.springmvc.service.UserProfilePictureService;
import com.websystique.springmvc.service.UserProfileService;
import com.websystique.springmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.userinfo.GoogleUserInfo;
import org.springframework.social.google.connect.GoogleConnectionFactory;
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
/*TODO Add this Controller in your app for Google singin/singup*/
@Controller
@PropertySource("classpath:/social-cfg.properties")
public class GoogleController {




    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    UserProfilePictureService profilePictureService;


    private static final ResourceBundle resource = ResourceBundle.getBundle("social-cfg");

    @Autowired
    UserService userService;

    private GoogleConnectionFactory googleConnectionFactory = new GoogleConnectionFactory(resource.getString("google.client.id"), resource.getString("google.client.secret"));

    @RequestMapping("/loginWithGoogle")
    public String singinWithGoogle(){
        OAuth2Operations operations = googleConnectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
            /*TODO register this redirect uri in google api*/
        params.setRedirectUri("http://localhost:8080/signin/google");
        params.setScope(resource.getString("google.scope"));


        String url = operations.buildAuthorizeUrl(params);

        return "redirect:"+url;
    }


    @RequestMapping("/signin/google")
    public String signInWithGoogle(@RequestParam("code") String authorizationCode, Model model) {

        OAuth2Operations operations = googleConnectionFactory.getOAuthOperations();

        /*TODO you should to set  here redirect uri */
        AccessGrant accessGrant = operations.exchangeForAccess(authorizationCode,"http://localhost:8080/signin/google",null);

        Connection<Google> connection =  googleConnectionFactory.createConnection(accessGrant);


        Google google = (Google) connection.getApi();

        String [] fields = {"email", "first_name", "last_name" };

        GoogleUserInfo userProfile = google.userOperations().getUserInfo();

        String ssid = String.valueOf(userProfile.getEmail());

        User user = userService.findBySSO(ssid);
        UserProfilePictures profilePictures = new UserProfilePictures();


        if (user==null){
            user = new User();
            user.setEmail(userProfile.getEmail());
            user.setFirstName(userProfile.getFirstName());
            user.setLastName(userProfile.getLastName());
            user.setSsoId(userProfile.getEmail());
            String randomPassword = "1234"+userProfile.getId()+"4321";
            String encrytedPassword = passwordEncoder.encode(randomPassword);
            user.setPassword(encrytedPassword);
            String src = userProfile.getProfilePictureUrl();


            Set<UserProfile> userProfiles = new HashSet<>();
            userProfiles.add(userProfileService.findByType("ADMIN"));
            user.setUserProfiles(userProfiles);
            userService.saveUser(user);

            profilePictures.setUserId(user.getId());
            profilePictures.setPicSrc(src);
            profilePictureService.save(profilePictures);
        }


        model.addAttribute("user",user);


        Authentication auth = new UsernamePasswordAuthenticationToken(user.getSsoId(),user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);



        return "redirect:/list";



    }

}
