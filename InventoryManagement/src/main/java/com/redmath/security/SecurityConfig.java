//package com.redmath.security;
//
//import com.nimbusds.jose.jwk.source.ImmutableSecret;
//import com.redmath.user.UsersService;
//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
//import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
//import io.swagger.v3.oas.annotations.info.Info;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.security.SecurityScheme;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.context.annotation.Profile;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
//import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
//import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
//import org.springframework.security.oauth2.jwt.*;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
//
//import javax.crypto.spec.SecretKeySpec;
//import java.time.Instant;
//import java.util.List;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@OpenAPIDefinition(info = @Info(title = "Inventory API", version = "v1"), security = @SecurityRequirement(name = "bearerAuth"))
//@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
//@EnableMethodSecurity
//@Configuration
//public class SecurityConfig {
//    private final OAuthConfig oAuthConfig;
//    private final @Lazy UsersService usersService;
//    private final PasswordEncoder passwordEncoder;
//
//    public SecurityConfig(OAuthConfig oAuthConfig,@Lazy UsersService usersService, PasswordEncoder passwordEncoder) {
//        this.oAuthConfig = oAuthConfig;
//        this.usersService = usersService;
//        this.passwordEncoder = passwordEncoder;
//    }
//    @Bean
//    @Profile("!test")
//    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtEncoder jwtEncoder) throws Exception {
//        // Common token generator function
//        Function<Authentication, String> tokenGenerator = authentication -> {
//            long expirySeconds = 3600;
//            JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
//
//            List<String> roles = authentication.getAuthorities().stream()
//                    .map(a -> a.getAuthority().replace("ROLE_", ""))
//                    .toList();
//
//            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
//                    .subject(authentication.getName())
//                    .expiresAt(Instant.now().plusSeconds(expirySeconds))
//                    .claim("roles", roles)
//                    .build();
//
//            Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet));
//
//            return String.format(
//                    "{\"token_type\":\"Bearer\",\"access_token\":\"%s\",\"expires_in\":%d}",
//                    jwt.getTokenValue(),
//                    expirySeconds
//            );
//        };
//
//        // Configure form login
//        http.formLogin(form -> form
//                .loginProcessingUrl("/login") // form login endpoint
//                .successHandler((request, response, authentication) -> {
//                    response.setContentType("application/json");
//                    response.getWriter().print(tokenGenerator.apply(authentication));
//                })
//                .failureHandler((request, response, exception) -> {
//                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                    response.setContentType("application/json");
//                    response.getWriter().print("{\"error\":\"Authentication failed: " + exception.getMessage() + "\"}");
//                })
//        );
//
//        // Configure OAuth2 login
//        http.oauth2Login(oauth2 -> oauth2
//                        .loginPage(OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/google")
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(this.oAuthConfig)
//                        )
//                        .successHandler((request, response, authentication) -> {
//                            response.setContentType("application/json");
//                            response.getWriter().print(tokenGenerator.apply(authentication));
//                        })
//                )
//                .logout(LogoutConfigurer::deleteCookies);
//
//        // JWT conversion setup
//        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
//            List<String> roles = jwt.getClaimAsStringList("roles");
//            return roles.stream()
//                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
//                    .collect(Collectors.toList());
//        });
//
//        http.oauth2ResourceServer(oauth2 -> oauth2
//                .jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
//
//        // Session and endpoint configuration
//        http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        http.authorizeHttpRequests(config -> config
//                .requestMatchers(
//                        "/error",
//                        "/swagger-ui/**",
//                        "/v3/api-docs/**",
//                        "/login/oauth2/code/**",  // Allow OAuth2 callback
//                        "/oauth2/authorization/google",  // Allow OAuth2 initiation
//                        "/login",  // Allow form login endpoint
//                        "/favicon.ico" // Browser requests
//                ).permitAll()
//                .requestMatchers("/actuator/**").permitAll()
//                .requestMatchers("/h2-console/**").permitAll()
//                .anyRequest().authenticated()
//        );
//
//        // CSRF configuration
//        http.csrf(csrf -> csrf
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
//                .ignoringRequestMatchers(PathRequest.toH2Console())
//                .ignoringRequestMatchers("/login", "/login/oauth2/code/**")
//        );
//
//        http.headers(headers -> headers
//                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
//        );
//
//        // Configure authentication manager
//        http.authenticationManager(authenticationManager(http));
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
//        daoProvider.setUserDetailsService(usersService);
//        daoProvider.setPasswordEncoder(passwordEncoder);
//
//        return new ProviderManager(daoProvider);
//    }
//
//    @Bean
//    @Profile("test") // Apply this configuration only when the "test" profile is active
//    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().permitAll() // Permit all requests for testing
//                ); // Often disabled for unit tests for simplicity
//        return http.build();
//    }
//
//    @Bean
//    public JwtEncoder jwtEncoder(@Value("${jwt.signing.key}") byte[] signingKey) {
//        return new NimbusJwtEncoder(new ImmutableSecret<>(new SecretKeySpec(signingKey, "HMACSHA256")));
//    }
//
//    @Bean
//    public JwtDecoder jwtDecoder(@Value("${jwt.signing.key}") byte[] signingKey) {
//        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(signingKey, "HMACSHA256")).build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}