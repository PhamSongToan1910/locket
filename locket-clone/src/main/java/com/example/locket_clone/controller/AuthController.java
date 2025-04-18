package com.example.locket_clone.controller;


import com.ctc.wstx.util.StringUtil;
import com.example.locket_clone.config.security.TokenProvider;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddUserRequest;
import com.example.locket_clone.entities.request.GetNewTokenFromRefreshToken;
import com.example.locket_clone.entities.request.LoginVM;
import com.example.locket_clone.entities.response.GetNewTokenFromRefreshTokenResponse;
import com.example.locket_clone.entities.response.LoginResponse;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.service.RoleService;
import com.example.locket_clone.service.UserService;
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
            if(!StringUtils.hasText(user.getFullName())) {
                return new ResponseData<>(new LoginResponse(jwt, refreshToken, false));
            }
            user.getDeviceToken().add(loginVM.getDeviceToken());
            userService.updateDeviceToken(user);
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
    public ResponseData<String> logout() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            SecurityContextHolder.clearContext();
        } else {
            // Trong trường hợp người dùng chưa đăng nhập, `principal` sẽ là chuỗi "anonymousUser"
            String username = principal.toString();
            System.out.println("Username: " + username);
        }
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
}
