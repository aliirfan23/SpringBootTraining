package com.redmath.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.redmath.user.UsersService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

//@Slf4j
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        log.info("Configuring security filter chain");
//        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()));
//        return http
//                .authorizeHttpRequests(auth -> auth
//
//                        .requestMatchers("/h2-console/**").hasRole("ADMIN")
//                        .requestMatchers("/api/v1/news/**").hasRole("REPORTER")
//                        .requestMatchers(  "/api/v1/{newsId}").hasRole("ADMIN")
//
//                        .anyRequest().authenticated()
//                )
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults())
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers(PathRequest.toH2Console()) //Disable CSRF for H2 Console
//                )
//                .headers(headers -> headers
//                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) //Allow H2 in frames
//                )
////                .headers(headers -> headers.disable())
//                .build();
//
//    }
//}
@OpenAPIDefinition(info = @Info(title = "News API", version = "v1"), security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UsersService userService, JwtEncoder jwtEncoder)
            throws Exception {
        http.formLogin(config -> config.successHandler((request, response, auth) -> {
            long expirySeconds = 3600;
            JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .subject(auth.getName())
                    .expiresAt(Instant.now().plusSeconds(expirySeconds))
                    .claim("roles", auth.getAuthorities().stream()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .toList())
                    .build();
            Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet));
            String tokenResponse = "{\"token_type\":\"Bearer\",\"access_token\":\"" + jwt.getTokenValue()
                    + "\",\"expires_in\":" + expirySeconds + "}";
            response.getWriter().print(tokenResponse.formatted());
        }));
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");
            return roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toList());
        });
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
//        http.oauth2ResourceServer(config -> config.jwt(jwtConfig -> jwtConfig.jwtAuthenticationConverter(jwt -> {
//            UserDetails user = userService.loadUserByUsername(jwt.getSubject());
//            return new JwtAuthenticationToken(jwt, user.getAuthorities());
//        })));

        http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(config -> config
                .requestMatchers("/error", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/news").hasRole("ADMIN")
                .requestMatchers("/api/v1/**").hasRole("ADMIN")
                .requestMatchers("/h2-console/**","/login").permitAll()
                .anyRequest().authenticated());
        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(PathRequest.toH2Console()))  //Disable CSRF for H2 Console
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/login")); //Disable CSRF for login endpoint
        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)); //Allow H2 in frames
        return http.build();
    }

    @Bean
    public JwtEncoder jwtEncoder(@Value("${jwt.signing.key}") byte[] signingKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(new SecretKeySpec(signingKey, "RSA")));
    }
    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.signing.key}") byte[] signingKey) {
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(signingKey, "RSA")).build();
    }
}
