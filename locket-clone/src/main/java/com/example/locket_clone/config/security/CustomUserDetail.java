package com.example.locket_clone.config.security;

import com.example.locket_clone.entities.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Getter
public class CustomUserDetail implements UserDetails {

    private final String id;
    private final Collection<SimpleGrantedAuthority> authorities;

    public CustomUserDetail(String userId, Set<SimpleGrantedAuthority> authorities) {
        this.id = userId;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

}
