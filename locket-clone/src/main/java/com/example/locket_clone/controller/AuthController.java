package com.example.locket_clone.controller;


import com.example.locket_clone.config.security.JWTToken;
import com.example.locket_clone.config.security.TokenProvider;
import com.example.locket_clone.entities.request.LoginVM;
import com.example.locket_clone.entities.response.ResponseData;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locket-clone/auth")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthController {
    TokenProvider tokenProvider;
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping("/login")
    public ResponseData<JWTToken> authorize(@RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginVM.getUsername(),
                loginVM.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        System.out.println("authen: " +  authentication.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        return new ResponseData<>(new JWTToken(jwt, refreshToken));
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
