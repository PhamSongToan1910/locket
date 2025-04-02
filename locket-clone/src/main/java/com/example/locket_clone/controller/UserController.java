package com.example.locket_clone.controller;

import com.example.locket_clone.config.CurrentUser;
import com.example.locket_clone.config.security.CustomUserDetail;
import com.example.locket_clone.config.security.TokenProvider;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddUserRequest;
import com.example.locket_clone.entities.request.UpdateUserInfoRequest;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/locket-clone/user")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

    UserService userService;

    @PostMapping("/update-user-infor")
    public ResponseData<String> updateUserInfor(@CurrentUser CustomUserDetail customUserDetail, @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        userService.updateUser(updateUserInfoRequest, customUserDetail.getId());
        return new ResponseData<>(200, "success");
    }

    @GetMapping("/find-by-username/{username}")
    public ResponseData<User> findByUsername(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        if(Objects.nonNull(user)) {
            return new ResponseData<>(200, user.getUsername());
        }
        return new ResponseData<>(404, "not found");
    }

    @GetMapping("/find-by-email/{email}")
    public ResponseData<User> findByEmail(@PathVariable("email") String email) {
        User user = userService.findUserByEmail(email);
        if(Objects.nonNull(user)) {
            return new ResponseData<>(200, user.getEmail());
        }
        return new ResponseData<>(404, "not found");
    }
}
