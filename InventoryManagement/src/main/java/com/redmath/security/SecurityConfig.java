package com.redmath.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@OpenAPIDefinition(info = @Info(title = "Inventory API", version = "v1"), security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@EnableMethodSecurity
@Configuration
public class SecurityConfig {
    private final OAuthConfig oAuthConfig;

    public SecurityConfig(OAuthConfig oAuthConfig) {
        this.oAuthConfig = oAuthConfig;
    }

    @Bean
    @Profile("!test") // Apply this configuration only when the "test" profile is not active
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtEncoder jwtEncoder) throws Exception {

        http.formLogin(config -> config.successHandler((request,
                                                        response, auth) -> {
            generateJwtTokenResponse(response, auth, jwtEncoder);
        }));

        // Configure OAuth2 login with custom user service
        http.oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(this.oAuthConfig)
                        )
                        .successHandler((request, response, authentication) -> {
                            generateJwtTokenResponse(response, authentication, jwtEncoder);
                        })
                )
                .logout(LogoutConfigurer::deleteCookies);


        // JWT conversion setup
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");
            return roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toList());
        });

        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));

        // Session and endpoint configuration
        http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(config -> config
                .requestMatchers(
                        "/error",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/login/oauth2/code/**",  // Allow OAuth2 callback
                        "/oauth2/authorization/google",  // Allow OAuth2 initiation
                        "/login",  // Allow login endpoint
                        "/login/oauth2/code/google", // Google callback
                        "/favicon.ico", // Browser requests
                        "/actuator/**",
                        "/h2-console/**"
                ).permitAll()
                .anyRequest().authenticated()
        );
        http.cors();
        // CSRF configuration
        http.csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers(PathRequest.toH2Console())
                .ignoringRequestMatchers("/login/oauth2/code/**","/login","/oauth2/authorization/google")
                .ignoringRequestMatchers("/login","/login/oauth2/code/google", "/favicon.ico")
        );

        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );

        return http.build();
    }
    private void generateJwtTokenResponse(HttpServletResponse response, Authentication authentication, JwtEncoder jwtEncoder) {
        try {
            long expirySeconds = 3600;
            JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

            List<String> roles = authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .toList();

            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .subject(authentication.getName())
                    .expiresAt(Instant.now().plusSeconds(expirySeconds))
                    .claim("roles", roles)
                    .build();

            Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet));

            String tokenResponse = String.format(
                    "{\"token_type\":\"Bearer\",\"access_token\":\"%s\",\"expires_in\":%d}",
                    jwt.getTokenValue(),
                    expirySeconds
            );

            response.setContentType("application/json");
            response.getWriter().print(tokenResponse);
        } catch (Exception e) {
            // Handle exception appropriately
            throw new RuntimeException("Failed to generate JWT token response", e);
        }
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // your React dev server
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // important for cookies/session-based login

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    @Profile("test")
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http,JwtEncoder jwtEncoder) throws Exception {
        http.formLogin(config -> config.successHandler((request,
                                                        response, auth) -> {
            generateJwtTokenResponse(response, auth, jwtEncoder);
        }));
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                );
        http.csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers(PathRequest.toH2Console())
                .ignoringRequestMatchers("/login", "/login/oauth2/code/**")
        );
        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );
        return http.build();
    }

    @Bean
    public JwtEncoder jwtEncoder(@Value("${jwt.signing.key}") byte[] signingKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(new SecretKeySpec(signingKey, "HMACSHA256")));
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.signing.key}") byte[] signingKey) {
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(signingKey, "HMACSHA256")).build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}