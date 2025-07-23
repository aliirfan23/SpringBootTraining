package com.redmath.security;

import com.redmath.user.Users;
import com.redmath.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsersRepository usersRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");

        // Check if user exists
        usersRepository.findByUsername(email).orElseGet(() -> {
            Users user = new Users();
            user.setUserId(System.currentTimeMillis()); // or use UUIDs
            user.setUsername(email);
            user.setPassword(""); // You can use a placeholder like "N/A"
            user.setRoles("USER");
            user.setCreatedAt(LocalDateTime.now());
            return usersRepository.save(user);
        });

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"
        );
    }
}
