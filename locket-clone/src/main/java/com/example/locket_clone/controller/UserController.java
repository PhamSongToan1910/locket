package com.example.locket_clone.controller;

import com.example.locket_clone.config.CurrentUser;
import com.example.locket_clone.config.security.CustomUserDetail;
import com.example.locket_clone.config.security.TokenProvider;
import com.example.locket_clone.entities.SendRequestFriend;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.UserFriends;
import com.example.locket_clone.entities.request.AddFriendRequest;
import com.example.locket_clone.entities.request.AddUserRequest;
import com.example.locket_clone.entities.request.UpdateUserInfoRequest;
import com.example.locket_clone.entities.request.UpdateUserInforV2Request;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.service.SendRequestFriendService;
import com.example.locket_clone.service.UserFriendsService;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.Constant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/api/locket-clone/user")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

    UserService userService;
    UserFriendsService userFriendsService;
    SendRequestFriendService sendRequestFriendService;

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

    @PostMapping("/send-request-add-friend")
    public ResponseData<UserFriends> addFriend(@CurrentUser CustomUserDetail customUserDetail, @RequestBody AddFriendRequest addFriendRequest) {
        String userId = customUserDetail.getId();
        String friendId = addFriendRequest.getFriendId();
        userFriendsService.addFriend(userId, friendId);
        sendRequestFriendService.sendRequestFriend(new SendRequestFriend(userId, friendId));
        return new ResponseData<>(200, "success");
    }

    @PostMapping("/accept-request-add-friend")
    public ResponseData<UserFriends> acceptFriend(@CurrentUser CustomUserDetail customUserDetail, @RequestBody AddFriendRequest addFriendRequest) {
        String userId = customUserDetail.getId();
        String friendId = addFriendRequest.getFriendId();
        sendRequestFriendService.acceptRequestFriend(userId, friendId);
        userFriendsService.addFriend(userId, friendId);
        return new ResponseData<>(200, "success");
    }

    @PostMapping("/reject-request-add-friend")
    public ResponseData<UserFriends> declineFriend(@CurrentUser CustomUserDetail customUserDetail, @RequestBody AddFriendRequest addFriendRequest) {
        String userId = customUserDetail.getId();
        String friendId = addFriendRequest.getFriendId();
        sendRequestFriendService.declineRequestFriend(userId, friendId);
        userFriendsService.removeFriend(userId, friendId);
        return new ResponseData<>(200, "success");
    }

    @GetMapping("/get-number-friends")
    public ResponseData<Integer> getNumberFriends(@CurrentUser CustomUserDetail customUserDetail) {
        String userId = customUserDetail.getId();
        Integer number = userFriendsService.getNumberFriends(userId);
        return new ResponseData<>(200, "success", number);
    }

    @PutMapping("/update-user-v2")
    public ResponseData<String> updateUserInforV2(@CurrentUser CustomUserDetail customUserDetail, @RequestBody UpdateUserInforV2Request updateUserInfoRequest) {
        userService.updateUserV2(updateUserInfoRequest, customUserDetail.getId());
        return new ResponseData<>(200, "success");
    }

    @PostMapping(value = "/update-avt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseData<String> updateAvt(@CurrentUser CustomUserDetail customUserDetail, @RequestParam("file") MultipartFile file) {
        String userId = customUserDetail.getId();

    }
}
