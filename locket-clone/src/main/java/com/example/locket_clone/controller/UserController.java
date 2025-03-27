package com.example.locket_clone.controller;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddUserRequest;
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

    @PostMapping("/register")
    public ResponseData<String> register(@RequestBody AddUserRequest userRequest) {
        userService.insertUser(userRequest);
        return new ResponseData<>(200, "success");
    }

    @GetMapping("/find-by-username/{username}")
    public ResponseData<User> findByUsername(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        if(Objects.nonNull(user)) {
            return new ResponseData<>(200, user.getUsername());
        }
        return new ResponseData<>(404, "not found");
    }

    @GetMapping("/find-by-email/{email}")
    public ResponseData<User> findByEmail(@PathVariable String email) {
        User user = userService.findUserByEmail(email);
        if(Objects.nonNull(user)) {
            return new ResponseData<>(200, user.getEmail());
        }
        return new ResponseData<>(404, "not found");
    }
}
