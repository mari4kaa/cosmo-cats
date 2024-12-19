package com.example.cosmocats.config.security;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;


public class AuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {


    public Collection<GrantedAuthority> convert(@NonNull final Jwt jwt) {
        @SuppressWarnings("unchecked")
        final Optional<List<String>> authorities = Optional.ofNullable((List<String>) jwt.getClaims().get("authorities"));
        return authorities.stream().flatMap(List::stream).map(roleName -> "ROLE_" + roleName)
            .map(SimpleGrantedAuthority::new).collect(Collectors.toUnmodifiableList());
    }
}
