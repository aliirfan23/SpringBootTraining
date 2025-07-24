//package com.redmath.security;
//
//import com.redmath.user.Users;
//import com.redmath.user.UsersRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
//import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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
//public class CustomOidcUserService extends OidcUserService {
//
//    private final UsersRepository usersRepository;
//
//    @Override
//    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
//        OidcUser oidcUser = super.loadUser(userRequest);
//        Map<String, Object> attributes = oidcUser.getAttributes();
//
//        String email = (String) attributes.get("email");
//        log.info("CustomOidcUserService invoked for email: {}", email);
//
//        // Find or create user
//        Users user = usersRepository.findByUsername(email).orElseGet(() -> {
//            Users newUser = new Users();
//            newUser.setUserId(System.currentTimeMillis());
//            newUser.setUsername(email);
//            newUser.setPassword("N/A");
//            newUser.setRoles("USER");
//            newUser.setCreatedAt(LocalDateTime.now());
//            return usersRepository.save(newUser);
//        });
//
//        // Extract roles from database
//        String role = "ROLE_" + user.getRoles().toUpperCase();
//        List<GrantedAuthority> authorities = Collections.singletonList(
//                new SimpleGrantedAuthority(role)
//        );
//
//        return new DefaultOidcUser(
//                authorities,
//                oidcUser.getIdToken(),
//                oidcUser.getUserInfo(),
//                "email"
//        );
//    }
//}