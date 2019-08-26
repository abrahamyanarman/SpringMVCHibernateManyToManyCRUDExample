package com.websystique.springmvc.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/*TODO Add this Controller in your app for Twitter singin/singup*/
@Controller
@PropertySource("classpath:/social-cfg.properties")
public class TwitterController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    UserProfilePictureService userProfilePictureService;

     /*set config file for social*/
    private static final ResourceBundle resource = ResourceBundle.getBundle("social-cfg");

    @Autowired
    UserService userService;

    private TwitterConnectionFactory twitterConnectionFactory = new TwitterConnectionFactory(resource.getString("twitter.consumer.key"), resource.getString("twitter.consumer.secret"));

    @RequestMapping("/loginWithTwitter")
    public String singinWithTwitter(HttpServletRequest request){
        /*TODO set TLSv1.2 */
        System.setProperty("https.protocols", "TLSv1.2");



        OAuth1Operations oauthOperations = twitterConnectionFactory.getOAuthOperations();
        /*TODO register this redirect uri in Twitter api*/
        OAuthToken requestToken = oauthOperations.fetchRequestToken( "http://localhost:8080/signin/twitter", null );
        request.getServletContext().setAttribute("token", requestToken);

        OAuth1Parameters params = new OAuth1Parameters();
        params.set("include_email", "true");
        String url = oauthOperations.buildAuthorizeUrl( requestToken.getValue(),params);

        return "redirect:"+url;
    }



    @RequestMapping(value = "/signin/twitter",method = RequestMethod.GET)
    public String signInWithTwitter(HttpServletRequest request, @RequestParam(name = "oauth_token") String oauthToken, @RequestParam(name = "oauth_verifier") String oauthVerifier, Model model) throws IOException {





        OAuth1Operations oauthOperations = twitterConnectionFactory.getOAuthOperations();
        Connection<Twitter> twitterConnection = getAccessTokenToConnection(request, oauthVerifier);


        TwitterTemplate twitterTemplate = new TwitterTemplate(resource.getString("twitter.consumer.key"), resource.getString("twitter.consumer.secret"),resource.getString("twitter.access.token"),resource.getString("twitter.access.token.secret"));



        RestTemplate restTemplate = twitterTemplate.getRestTemplate();
        /*TODO DON"T set  here redirect uri */
        String response = restTemplate.getForObject("https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true", String.class);
        System.out.println("Profile Info with Email: "+ response);
        /*UserProfile userProfile =  twitterConnection.fetchUserProfile();*/
        ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> mp = mapper.readValue(response,new TypeReference<Map<String, Object>>() {});
            String email = mp.get("email").toString();
            String firstName = mp.get("name").toString();
            String fullName = mp.get("screen_name").toString();
            String userProfileId = mp.get("id").toString();





        User user = userService.findBySSO(email);

        UserProfilePictures pictures = new UserProfilePictures();

        if (user==null){
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(fullName);
            user.setSsoId(email);
            String randomPassword = "1234"+userProfileId+"4321";
            String encrytedPassword = passwordEncoder.encode(randomPassword);
            user.setPassword(encrytedPassword);



            Set<UserProfile> userProfiles = new HashSet<>();
            userProfiles.add(userProfileService.findByType("ADMIN"));
            user.setUserProfiles(userProfiles);
            userService.saveUser(user);


            pictures.setUserId(user.getId());
            pictures.setPicSrc(mp.get("profile_image_url").toString());


            userProfilePictureService.save(pictures);
        }

        pictures = userProfilePictureService.findById(user.getId());


        model.addAttribute("user",user);
        model.addAttribute("picture",pictures);



        Authentication auth = new UsernamePasswordAuthenticationToken(user.getSsoId(),user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);



        return "redirect:/list";



    }
    private Connection<Twitter> getAccessTokenToConnection(HttpServletRequest request, @RequestParam(name = "oauth_verifier") String oauthVerifier) {
        //TwitterConnectionFactory twitterConnectionFactory = new TwitterConnectionFactory(clientId, clientSecret);
        OAuth1Operations operations = twitterConnectionFactory.getOAuthOperations();
        OAuthToken requestToken = (OAuthToken)request.getServletContext().getAttribute("token");
        request.getServletContext().removeAttribute("token");
        OAuthToken accessToken = operations.exchangeForAccessToken(new AuthorizedRequestToken(requestToken, oauthVerifier), null);
        return twitterConnectionFactory.createConnection(accessToken);
    }
}


