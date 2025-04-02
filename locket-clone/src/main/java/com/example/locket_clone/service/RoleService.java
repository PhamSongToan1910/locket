package com.example.locket_clone.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public interface RoleService {
    Set<SimpleGrantedAuthority> convertRolesToSimpleGrantedAuthorities(Set<String> roles);
}
