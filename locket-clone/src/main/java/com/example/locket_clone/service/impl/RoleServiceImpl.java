package com.example.locket_clone.service.impl;

import com.example.locket_clone.repository.InterfacePackage.RoleRepository;
import com.example.locket_clone.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;

    @Override
    public Set<SimpleGrantedAuthority> convertRolesToSimpleGrantedAuthorities(Set<String> roles) {
        return roles.stream().map(roleRepository::findByName).filter(Objects::nonNull).map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }
}
