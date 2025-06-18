package com.example.locket_clone.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.example.locket_clone.config.CurrentUser;
import com.example.locket_clone.config.security.CustomUserDetail;
import com.example.locket_clone.entities.Conversation;
import com.example.locket_clone.entities.Message;
import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.DeletePostByAdminRequest;
import com.example.locket_clone.entities.request.ObjectRequest;
import com.example.locket_clone.entities.request.UpdateReportPostByAdmin;
import com.example.locket_clone.entities.response.GetAllPostResponse;
import com.example.locket_clone.entities.response.GetReportPosts;
import com.example.locket_clone.entities.response.GetUserInfoBEResponse;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.runner.EventMessageRunner;
import com.example.locket_clone.runner.EventPostRunner;
import com.example.locket_clone.runner.NettySocketIOServerRunner;
import com.example.locket_clone.service.ConversationService;
import com.example.locket_clone.service.PostService;
import com.example.locket_clone.service.ReportPostService;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.Constant;
import com.example.locket_clone.utils.Constant.ResponseCode;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.lang.Nullable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/locket-clone/backend")
@CrossOrigin
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BackendController {

    PostService postService;
    ReportPostService reportPostService;
    UserService userService;
    ConversationService conversationService;
    SocketIOServer server;

    @GetMapping("/get-all-post")
    public ResponseData<List<GetAllPostResponse>> getAllPost(@RequestParam("page") int page,
                                                             @RequestParam("size") int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        List<GetAllPostResponse> responseList = postService.getAllPostsByAdmin(pageRequest);
        return new ResponseData<>(ResponseCode.SUCCESS, "success", responseList);
    }

    @GetMapping("/get-report-post")
    public ResponseData<?> getReportPost(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "2") int kind) {
        Pageable pageable = PageRequest.of(page, size);
        List<GetReportPosts> getReportPostsList = reportPostService.getReportPosts(kind, pageable).stream().map(reportPost -> {
            GetReportPosts dto = new GetReportPosts();
            ModelMapperUtils.toObject(reportPost, dto);

            Post post = postService.findbyId(dto.getPostId());
            User user = userService.findUserById(post.getUserId());

            dto.setCaption(post.getCaption());
            dto.setImageURL(post.getImageURL());
            dto.convertCreateAtInstantToString(post.getCreatedAt());

            if (user != null) {
                dto.setOnwerId(user.getId().toString());
            }

            return dto;
        }).toList();


        return new ResponseData<>(ResponseCode.SUCCESS, "success", getReportPostsList);
    }

    @GetMapping("/get-user-info-by-admin")
    public ResponseData<GetUserInfoBEResponse> getUserInfoByAdmin(@RequestParam String userId) {
        User user = userService.findUserById(userId);
        GetUserInfoBEResponse response = new GetUserInfoBEResponse();
        ModelMapperUtils.toObject(user, response);
        response.convertCreateAtInstantToString(user.getCreatedAt());
        return new ResponseData<>(ResponseCode.SUCCESS, "success", response);
    }

    @PutMapping("/update-report-post-by-admin")
    public ResponseData<?> deletePostByAdmin(@CurrentUser CustomUserDetail customUserDetail, @RequestBody UpdateReportPostByAdmin updateReportPostByAdmin) throws JsonProcessingException {
        String userId = customUserDetail.getId();
        updateReportPostByAdmin.setUserId(userId);
        Post post = postService.findbyId(updateReportPostByAdmin.getPostId());
        if(updateReportPostByAdmin.getStatus() == Constant.STATUS_REPORT_POST.DELETE) {
            sendMessageToOwnerPostDeleted(userId, post);
        }
        ObjectRequest requestDeletePostByAdminV2 = new ObjectRequest(Constant.API.UPDATE_POST_BY_ADMIN, updateReportPostByAdmin);
        ObjectRequest requestDeleteReportPostByAdmin = new ObjectRequest(Constant.API.UPDATE_REPORT_POST_BY_ADMIN, updateReportPostByAdmin);
        EventPostRunner.requests.add(requestDeletePostByAdminV2);
        EventPostRunner.requests.add(requestDeleteReportPostByAdmin);
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    private void sendMessageToOwnerPostDeleted(String userId, Post post) throws JsonProcessingException {
        Conversation conversation = conversationService.getConversationByUserIdAndFriendId(userId, post.getUserId());
        if(Objects.isNull(conversation)) {
            conversation = conversationService.createConversation(userId, post.getUserId());
        }
        Message message = new Message("Your post was deleted by admin for violating community standards.", conversation.getId().toString(), userId, post.getUserId(), false, post.getImageURL());
        Map<String, Object> map = new HashMap<>();
        map.put("conversation_id", message.getConversationId());
        map.put("user_receiver", message.getUserReceiverId());
        map.put("content", message.getContent());
        map.put("post_url", message.getPostURL());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(map);
        EventMessageRunner.eventMessageRequests.add(new ObjectRequest(Constant.API.UPDATE_LAST_MESSAGE, message));
        Set<UUID> usersOnline = NettySocketIOServerRunner.onlineUsers.get(post.getUserId());
        System.out.println("usersOnline: " + usersOnline);
        if(Objects.nonNull(usersOnline)) {
            usersOnline.forEach(uuid -> {
                SocketIOClient socketReceiver = server.getClient(uuid);
                if(socketReceiver != null) {
                    socketReceiver.sendEvent("receiver_message", json);
                }
            });
        }
    }


}
