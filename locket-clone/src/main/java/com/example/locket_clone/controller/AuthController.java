package com.example.locket_clone.controller;


import com.ctc.wstx.util.StringUtil;
import com.example.locket_clone.config.security.TokenProvider;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddUserRequest;
import com.example.locket_clone.entities.request.LoginVM;
import com.example.locket_clone.entities.response.LoginResponse;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.service.RoleService;
import com.example.locket_clone.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/locket-clone/auth")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthController {
    TokenProvider tokenProvider;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    UserService userService;
    RoleService roleService;

    @PostMapping("/login")
    public ResponseData<LoginResponse> authorize(@RequestBody LoginVM loginVM) {
        User user = userService.findUserByEmail(loginVM.getUsername());
        if(user != null){
            Set<SimpleGrantedAuthority> authorities = roleService.convertRolesToSimpleGrantedAuthorities(user.getAuthorities());
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginVM.getUsername(),
                    null,
                    authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            String jwt = tokenProvider.createToken(authenticationToken, user.getId().toString());
            String refreshToken = tokenProvider.createRefreshToken(authenticationToken, user.getId().toString());
            if(StringUtils.isEmpty(user.getFullName()) || Objects.isNull(user.getFullName())) {
                return new ResponseData<>(new LoginResponse(jwt, refreshToken, false));
            }
            return new ResponseData<>(new LoginResponse(jwt, refreshToken, true));

        } else {
            User userInsert = userService.insertUser(new AddUserRequest(loginVM.getUsername(), loginVM.getAvt()));
            Set<SimpleGrantedAuthority> authorities = roleService.convertRolesToSimpleGrantedAuthorities(userInsert.getAuthorities());
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginVM.getUsername(),
                    null,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            String jwt = tokenProvider.createToken(authenticationToken, userInsert.getId().toString());
            String refreshToken = tokenProvider.createRefreshToken(authenticationToken, userInsert.getId().toString());
            return new ResponseData<>(new LoginResponse(jwt, refreshToken, false));
        }
    }

    @PostMapping("/logout")
    public ResponseData<String> logout() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            SecurityContextHolder.clearContext();
        } else {
            // Trong trường hợp người dùng chưa đăng nhập, `principal` sẽ là chuỗi "anonymousUser"
            String username = principal.toString();
            System.out.println("Username: " + username);
        }
        return new ResponseData<>(200, "Logout success!!!");
    }

}
