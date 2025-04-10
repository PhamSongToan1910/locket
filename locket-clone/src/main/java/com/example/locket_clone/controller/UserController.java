package com.example.locket_clone.controller;

import com.example.locket_clone.config.CurrentUser;
import com.example.locket_clone.config.security.CustomUserDetail;
import com.example.locket_clone.entities.SendRequestFriend;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.UserFriends;
import com.example.locket_clone.entities.request.AddFriendRequest;
import com.example.locket_clone.entities.request.UpdateUserInfoRequest;
import com.example.locket_clone.entities.request.UpdateUserInforV2Request;
import com.example.locket_clone.entities.response.FindUserByUserNameResponse;
import com.example.locket_clone.entities.response.GetFriendResponse;
import com.example.locket_clone.entities.response.GetUserInfoResponse;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.service.SendRequestFriendService;
import com.example.locket_clone.service.UserFriendsService;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.Constant;
import com.example.locket_clone.utils.Constant.ResponseCode;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import com.example.locket_clone.utils.s3Utils.S3Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/locket-clone/user")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

    UserService userService;
    UserFriendsService userFriendsService;
    SendRequestFriendService sendRequestFriendService;
    S3Service s3Service;

    @PostMapping("/update-user-infor")
    public ResponseData<String> updateUserInfor(@CurrentUser CustomUserDetail customUserDetail, @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
        userService.updateUser(updateUserInfoRequest, customUserDetail.getId());
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    @GetMapping("/find-by-username")
    public ResponseData<FindUserByUserNameResponse> findByUsername(@RequestParam("username") String username) {
        if(!StringUtils.hasLength(username)) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Wrong request format");
        }
        User user = userService.findUserByUsername(username);
        if(Objects.nonNull(user)) {
            FindUserByUserNameResponse findUserByUserNameResponse = new FindUserByUserNameResponse(true);
            return new ResponseData<>(findUserByUserNameResponse);
        }
        return new ResponseData<>(new FindUserByUserNameResponse(false));
    }

    @GetMapping("/find-by-email/{email}")
    public ResponseData<User> findByEmail(@PathVariable("email") String email) {
        User user = userService.findUserByEmail(email);
        if(Objects.nonNull(user)) {
            return new ResponseData<>(ResponseCode.SUCCESS, user.getEmail());
        }
        return new ResponseData<>(404, "not found");
    }

    @PostMapping("/send-request-add-friend")
    public ResponseData<UserFriends> addFriend(@CurrentUser CustomUserDetail customUserDetail, @RequestBody AddFriendRequest addFriendRequest) {
        String userId = customUserDetail.getId();
        String friendId = addFriendRequest.getFriendId();
        userFriendsService.addFriend(userId, friendId);
        boolean result = sendRequestFriendService.sendRequestFriend(new SendRequestFriend(userId, friendId));
        if(!result) {
            return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "unknown error");
        }
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    @PostMapping("/accept-request-add-friend")
    public ResponseData<UserFriends> acceptFriend(@CurrentUser CustomUserDetail customUserDetail, @RequestBody AddFriendRequest addFriendRequest) {
        String userId = customUserDetail.getId();
        String friendId = addFriendRequest.getFriendId();
        boolean result = sendRequestFriendService.acceptRequestFriend(userId, friendId);
        boolean acceptFriend = userFriendsService.acceptFriend(userId, friendId);
        if(!result) {
            return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "Cant find send request");
        }
        if(!acceptFriend) {
            return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "Cant accept request");
        }
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    @PostMapping("/reject-request-add-friend")
    public ResponseData<UserFriends> declineFriend(@CurrentUser CustomUserDetail customUserDetail, @RequestBody AddFriendRequest addFriendRequest) {
        String userId = customUserDetail.getId();
        String friendId = addFriendRequest.getFriendId();
        boolean result = sendRequestFriendService.declineRequestFriend(userId, friendId);
        if(!result) {
            return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "unknown error");
        }
        userFriendsService.removeSendRequestFriend(userId, friendId);
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    @GetMapping("/get-number-friends")
    public ResponseData<Integer> getNumberFriends(@CurrentUser CustomUserDetail customUserDetail) {
        String userId = customUserDetail.getId();
        Integer number = userFriendsService.getNumberFriends(userId);
        return new ResponseData<>(ResponseCode.SUCCESS, "success", number);
    }

    @PutMapping("/update-user-v2")
    public ResponseData<String> updateUserInforV2(@CurrentUser CustomUserDetail customUserDetail, @RequestBody UpdateUserInforV2Request updateUserInfoRequest) {
        userService.updateUserV2(updateUserInfoRequest, customUserDetail.getId());
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    @PostMapping(value = "/update-avt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseData<String> updateAvt(@CurrentUser CustomUserDetail customUserDetail, @RequestParam("file") MultipartFile file) throws IOException {
        String userId = customUserDetail.getId();
        String avtPath = s3Service.uploadFile(file);
        userService.updateAvt(userId, avtPath);
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    @GetMapping("/get-all-friends")
    public ResponseData<List<GetFriendResponse>> getAllFriends(@CurrentUser CustomUserDetail customUserDetail) {
        List<GetFriendResponse> response = userFriendsService.getAllFriends(customUserDetail.getId());
        return new ResponseData<>(ResponseCode.SUCCESS, "success", response);
    }

    @GetMapping("/get-send-request-friends")
    public ResponseData<List<GetFriendResponse>> getSendRequestFriends(@CurrentUser CustomUserDetail customUserDetail) {
        Set<String> sendRequestFriend = sendRequestFriendService.getFriendsRequestByUserId(customUserDetail.getId());
        List<GetFriendResponse> response = sendRequestFriend.stream()
                .map(userService::findUserById)
                .filter(Objects::nonNull)
                .map(user -> {
                    GetFriendResponse responseObj = new GetFriendResponse();
                    ModelMapperUtils.toObject(user, responseObj);
                    return responseObj;
                })
                .toList();
        return new ResponseData<>(ResponseCode.SUCCESS, "success", response);
    }

    @DeleteMapping("/cancel-request-friend")
    public ResponseData<?> cancelRequestFriend(@CurrentUser CustomUserDetail customUserDetail, @RequestParam("friend_id") String friendId) {
        String userId = customUserDetail.getId();
        boolean result = sendRequestFriendService.cancelRequestFriend(userId, friendId);
        if(!result) {
            return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "Cant find send request");
        }
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    @GetMapping("/get-user-info")
    public ResponseData<GetUserInfoResponse> getUserInfo(@CurrentUser CustomUserDetail customUserDetail) {
        User user = userService.findUserById(customUserDetail.getId());
        if(Objects.isNull(user)) {
            return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "Cant find user");
        }
        GetUserInfoResponse response = new GetUserInfoResponse();
        ModelMapperUtils.toObject(user, response);
        if(!StringUtils.hasLength(user.getFullName()) || !StringUtils.hasLength(user.getUsername())) {
            response.setComplete(false);
        } else {
            response.setComplete(true);
        }
        if(user.getIsDeleted()) {
            response.setStatus(Constant.TYPE_USER.DELETED);
        } else {
            response.setStatus(Constant.TYPE_USER.IS_NOT_DELETED);
        }

        return new ResponseData<>(ResponseCode.SUCCESS, "success", response);
    }
}
