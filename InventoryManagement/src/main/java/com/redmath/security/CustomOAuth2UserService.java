//package com.redmath.security;
//
//import com.redmath.user.Users;
//import com.redmath.user.UsersRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//    private final UsersRepository usersRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest)throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = loadUser(userRequest);
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//
//        String email = (String) attributes.get("email");
//        log.info("CustomOAuth2UserService invoked for email: {}", email);
//        System.out.println("CustomOAuth2UserService invoked for email: "+email);
//
//        // Find or create user
//        Users user = usersRepository.findByUsername(email).orElseGet(() -> {
//            Users newUser = new Users();
//            newUser.setUserId(System.currentTimeMillis());
//            newUser.setUsername(email);
//            newUser.setPassword("N/A"); // Placeholder for OAuth users
//            newUser.setRoles("USER"); // Default role
//            newUser.setCreatedAt(LocalDateTime.now());
//            return usersRepository.save(newUser);
//        });
//
//        // Extract roles from database
//        String role = "ROLE_" + user.getRoles().toUpperCase();
//        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
//                new SimpleGrantedAuthority(role)
//        );
//
//        return new DefaultOAuth2User(
//                authorities,
//                attributes,
//                "email"
//        );
//    }
//}
////@Slf4j
////@Service
////@RequiredArgsConstructor
////public class CustomOAuth2UserService extends DefaultOAuth2UserService {
////
////    private final UsersRepository usersRepository;
////
////    @Override
////    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
////        OAuth2User oAuth2User = super.loadUser(userRequest);
////        Map<String, Object> attributes = oAuth2User.getAttributes();
////
////        String email = (String) attributes.get("email");
////        log.info("CustomOAuth2UserService invoked for email: {}", email);
////
////        // Check if user exists
////        usersRepository.findByUsername(email).orElseGet(() -> {
////            Users user = new Users();
////            user.setUserId(System.currentTimeMillis()); // or use UUIDs
////            user.setUsername(email);
////            user.setPassword(""); // You can use a placeholder like "N/A"
////            user.setRoles("USER");
////            user.setCreatedAt(LocalDateTime.now());
////            return usersRepository.save(user);
////        });
////
////        return new DefaultOAuth2User(
////                List.of(new SimpleGrantedAuthority("ROLE_USER")),
////                attributes,
////                "email"
////        );
////    }
////}
