package com.example.locket_clone.config.security;

import com.example.locket_clone.entities.Role;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.repository.InterfacePackage.RoleRepository;
import com.example.locket_clone.repository.InterfacePackage.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component("userDetailsService")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DomainUserDetailService implements UserDetailsService {

    UserRepository userRepository;
    RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByEmail(username);
        if(userEntity != null) {
            return getUserDetails(userEntity);
        }
        throw new UsernameNotFoundException("username: "+ username + " not found!!!");
    }

    private UserDetails getUserDetails(User userEntity) {
        Set<SimpleGrantedAuthority> authorities = userEntity.getAuthorities()
                .stream()
                .map((id) -> {
                    try {
                        return stringToRole(id);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                authorities
        );
    }

    private String stringToRole(String id) throws Exception {
        return roleRepository.findById(id, Role.class).getName();
    }
}
