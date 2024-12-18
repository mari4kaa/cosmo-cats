package com.example.cosmocats.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import static com.example.cosmocats.util.SecurityUtil.ROLE_CLAIMS_HEADER;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

    private static final String API_V_1_GENERAL = "/api/v1/internal/**";
    private static final String API_V_1_CATS = "/api/v1/admin/**";

    @Bean
    @Order(1)
    public SecurityFilterChain filterChainInternalV1(HttpSecurity http, JwtDecoder decoder) throws Exception {
        http.securityMatcher(API_V_1_GENERAL)
            .cors(withDefaults())
            .csrf(CsrfConfigurer::disable)
            .addFilterBefore(new CosmicKeyFilter(decoder), UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize.requestMatchers(antMatcher(API_V_1_GENERAL)).authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChainCatsV1(HttpSecurity http) throws Exception {

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new AuthorityConverter());


        http.securityMatcher(API_V_1_CATS)
            .cors(withDefaults())
            .csrf(csrf -> csrf.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()).csrfTokenRepository(withHttpOnlyFalse()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize ->
                authorize.requestMatchers(antMatcher(API_V_1_CATS)).authenticated())
            .oauth2ResourceServer(oAuth2 ->
                oAuth2.bearerTokenResolver(new HeaderBearerTokenResolver(ROLE_CLAIMS_HEADER))
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();
    }
}