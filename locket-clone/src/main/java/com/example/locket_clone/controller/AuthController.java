package com.example.locket_clone.controller;


import com.example.locket_clone.config.CurrentUser;
import com.example.locket_clone.config.security.CustomUserDetail;
import com.example.locket_clone.config.security.TokenProvider;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.*;
import com.example.locket_clone.entities.response.GetNewTokenFromRefreshTokenResponse;
import com.example.locket_clone.entities.response.LoginResponse;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.runner.EventUserRunner;
import com.example.locket_clone.service.RoleService;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.Constant;
import com.example.locket_clone.utils.Constant.ResponseCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
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
    public ResponseData<?> authorize(@RequestBody LoginVM loginVM) {
        if(!loginVM.validateRequest()) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Wrong request format");
        }
        User user = userService.findUserByEmail(loginVM.getEmail());
        if(user != null){
            if(user.getIsDeleted()) {
                return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "User is deleted");
            }
            Set<SimpleGrantedAuthority> authorities = roleService.convertRolesToSimpleGrantedAuthorities(user.getAuthorities());
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginVM.getEmail(),
                    null,
                    authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            String jwt = tokenProvider.createToken(authenticationToken, user.getId().toString());
            String refreshToken = tokenProvider.createRefreshToken(authenticationToken, user.getId().toString());
            if (Objects.isNull(user.getDeviceToken())) {
                user.setDeviceToken(new HashSet<>());
            }
            UpdateDeviceTokenRequest updateDeviceTokenRequest = new UpdateDeviceTokenRequest();
            updateDeviceTokenRequest.setDeviceToken(loginVM.getDeviceToken());
            updateDeviceTokenRequest.setDeviceId(loginVM.getDeviceId());
            updateDeviceTokenRequest.setUserId(user.getId().toString());
            ObjectRequest request = new ObjectRequest(Constant.API.UPDATE_DEVICE_TOKEN, updateDeviceTokenRequest);
            EventUserRunner.eventUserRequests.add(request);
            if(!StringUtils.hasText(user.getFullName())) {
                return new ResponseData<>(new LoginResponse(jwt, refreshToken, false));
            }
            return new ResponseData<>(new LoginResponse(jwt, refreshToken, true));

        } else {
            User userInsert = userService.insertUser(new AddUserRequest(loginVM.getEmail(), loginVM.getAvt(), loginVM.getDeviceToken()));
            Set<SimpleGrantedAuthority> authorities = roleService.convertRolesToSimpleGrantedAuthorities(userInsert.getAuthorities());
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginVM.getEmail(),
                    null,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            String jwt = tokenProvider.createToken(authenticationToken, userInsert.getId().toString());
            String refreshToken = tokenProvider.createRefreshToken(authenticationToken, userInsert.getId().toString());
            return new ResponseData<>(new LoginResponse(jwt, refreshToken, false));
        }
    }

    @PostMapping("/logout")
    public ResponseData<String> logout(@CurrentUser CustomUserDetail customUserDetail, @RequestBody LogoutRequest logoutRequest) {
        if (customUserDetail == null) {
            return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "user is null");
        }
        ObjectRequest objectRequest = new ObjectRequest(Constant.API.LOGOUT, new RemoveDeviceRequest(logoutRequest.getDeviceId(), logoutRequest.getDeviceToken(), customUserDetail.getId()));
        EventUserRunner.eventUserRequests.add(objectRequest);
        SecurityContextHolder.clearContext();
        return new ResponseData<>(ResponseCode.SUCCESS, "Logout success!!!");
    }

    @PostMapping("/get-new-token")
    public ResponseEntity<?> getNewToken(@RequestBody GetNewTokenFromRefreshToken getNewTokenFromRefreshToken) {
        if(!tokenProvider.validateToken(getNewTokenFromRefreshToken.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseData<>(ResponseCode.UN_AUTHORIZED, "Refresh token is expired"));
        }
        String refreshToken = getNewTokenFromRefreshToken.getRefreshToken();
        Authentication authenticationToken = tokenProvider.getAuthentication(refreshToken);
        String userId = tokenProvider.getUserIdByToken(refreshToken);
        String token = tokenProvider.createToken(authenticationToken, userId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>(ResponseCode.SUCCESS, "success", new GetNewTokenFromRefreshTokenResponse(token)));
    }

    @PostMapping("/login-admin")
    public ResponseData<?> loginAdmin(@RequestBody LoginAdminRequest loginAdminRequest) {
        if(!loginAdminRequest.validateRequest()) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Wrong request format");
        }
        User user = userService.findUserByEmail(loginAdminRequest.getEmail());
        Set<SimpleGrantedAuthority> authorities = roleService.convertRolesToSimpleGrantedAuthorities(user.getAuthorities());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginAdminRequest.getEmail(),
                loginAdminRequest.getPassword(),
                authorities);
        String jwt = tokenProvider.createToken(usernamePasswordAuthenticationToken, user.getId().toString());
        return new ResponseData<>(ResponseCode.SUCCESS, "success", jwt);
    }
}
