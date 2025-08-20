//package com.redmath.security;
//
//import com.redmath.user.Users;
//import com.redmath.user.UsersRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.AuthorizationGrantType;
//import org.springframework.security.oauth2.core.OAuth2AccessToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.time.LocalDateTime;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//class OAuthConfigTest {
//
//    @Mock
//    private UsersRepository usersRepository;
//
//    @Mock
//    private OAuth2UserRequest userRequest;
//
//    @Mock
//    private OAuth2AccessToken accessToken;
//
//    @InjectMocks
//    private OAuthConfig oAuthConfig;
//
//    private static final String TEST_EMAIL = "test@example.com";
//    private ClientRegistration clientRegistration;
//
//    @BeforeEach
//    void setUp() {
//        // Create proper ClientRegistration with all required details
//        clientRegistration = ClientRegistration.withRegistrationId("google")
//                .clientId("client-id")
//                .clientSecret("client-secret")
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
//                .scope("openid", "profile", "email")
//                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
//                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
//                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
//                .userNameAttributeName("sub")
//                .clientName("Google")
//                .build();
//
//        // Setup OAuth2UserRequest mock
//        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
//        when(userRequest.getAccessToken()).thenReturn(accessToken);
//
//        // Setup OAuth2AccessToken mock
//        when(accessToken.getTokenValue()).thenReturn("test-token");
//    }
//
//    @Test
//    void testLoadExistingUser() {
//        Users existingUser = new Users();
//        existingUser.setUsername(TEST_EMAIL);
//        existingUser.setRoles("USER");
//        existingUser.setCreatedAt(LocalDateTime.now());
//
//        when(usersRepository.findByUsername(TEST_EMAIL)).thenReturn(Optional.of(existingUser));
//
//        OAuth2User oauth2User = oAuthConfig.loadUser(userRequest);
//
//        assertNotNull(oauth2User);
//        assertTrue(oauth2User.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
//        verify(usersRepository).findByUsername(TEST_EMAIL);
//        verify(usersRepository, never()).save(any());
//    }
//
//    @Test
//    void testLoadNewUser() {
//        when(usersRepository.findByUsername(TEST_EMAIL)).thenReturn(Optional.empty());
//        when(usersRepository.save(any())).thenAnswer(i -> i.getArgument(0));
//
//        OAuth2User oauth2User = oAuthConfig.loadUser(userRequest);
//
//        assertNotNull(oauth2User);
//        assertTrue(oauth2User.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
//        verify(usersRepository).findByUsername(TEST_EMAIL);
//        verify(usersRepository).save(any());
//    }
//
//    @Test
//    void testLoadUserWithAdminRole() {
//        Users adminUser = new Users();
//        adminUser.setUsername(TEST_EMAIL);
//        adminUser.setRoles("ADMIN");
//        adminUser.setCreatedAt(LocalDateTime.now());
//
//        when(usersRepository.findByUsername(TEST_EMAIL)).thenReturn(Optional.of(adminUser));
//
//        OAuth2User oauth2User = oAuthConfig.loadUser(userRequest);
//
//        assertNotNull(oauth2User);
//        assertTrue(oauth2User.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
//    }
//
//    @Test
//    void testLoadUserWithMultipleRoles() {
//        Users multiRoleUser = new Users();
//        multiRoleUser.setUsername(TEST_EMAIL);
//        multiRoleUser.setRoles("USER,ADMIN");
//        multiRoleUser.setCreatedAt(LocalDateTime.now());
//
//        when(usersRepository.findByUsername(TEST_EMAIL)).thenReturn(Optional.of(multiRoleUser));
//
//        OAuth2User oauth2User = oAuthConfig.loadUser(userRequest);
//
//        assertNotNull(oauth2User);
//        assertTrue(oauth2User.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_USER,ADMIN")));
//    }
//
//    @Test
//    void testLoadUserTimestampCreation() {
//        when(usersRepository.findByUsername(TEST_EMAIL)).thenReturn(Optional.empty());
//        when(usersRepository.save(any())).thenAnswer(i -> {
//            Users savedUser = (Users) i.getArgument(0);
//            assertNotNull(savedUser.getCreatedAt());
//            assertTrue(savedUser.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
//            return savedUser;
//        });
//
//        OAuth2User oauth2User = oAuthConfig.loadUser(userRequest);
//
//        assertNotNull(oauth2User);
//        verify(usersRepository).save(any());
//    }
//}