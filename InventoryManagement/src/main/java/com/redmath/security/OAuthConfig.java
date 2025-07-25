package com.redmath.security;


import com.redmath.user.Users;
import com.redmath.user.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class OAuthConfig implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UsersRepository usersRepository;

    public OAuthConfig() {
        System.out.println("✅ OAuthConfig constructor initialized");
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("✅ OAuthConfig LOADUSER initialized");

        // Load user from Google
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        System.out.println("Processing OAuth user: " + email);

        Users user = usersRepository.findByUsername(email).orElseGet(() -> {
            Users newUser = new Users();
            newUser.setUsername(email);
            newUser.setPassword("N/A"); // Placeholder for OAuth users
            newUser.setRoles("USER"); // Default role
            newUser.setCreatedAt(LocalDateTime.now());
            return usersRepository.save(newUser);
        });

        // Extract roles from database
        String role = "ROLE_" + user.getRoles().toUpperCase();


        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        return new DefaultOAuth2User(authorities, oauth2User.getAttributes(), "email");
    }
}