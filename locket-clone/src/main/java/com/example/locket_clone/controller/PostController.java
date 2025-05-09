package com.example.locket_clone.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.example.locket_clone.config.CurrentUser;
import com.example.locket_clone.config.security.CustomUserDetail;
import com.example.locket_clone.entities.*;
import com.example.locket_clone.entities.request.*;
import com.example.locket_clone.entities.response.GetPostResponse;
import com.example.locket_clone.entities.response.GetReactionResponse;
import com.example.locket_clone.entities.response.GetReportPosts;
import com.example.locket_clone.entities.response.GetUnreadPostResponse;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.runner.EventMessageRunner;
import com.example.locket_clone.runner.EventPostRunner;
import com.example.locket_clone.runner.EventUserRunner;
import com.example.locket_clone.runner.NettySocketIOServerRunner;
import com.example.locket_clone.service.*;
import com.example.locket_clone.utils.Constant.Constant;
import com.example.locket_clone.utils.Constant.ResponseCode;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import com.example.locket_clone.utils.fileUtils.FileUtils;
import com.example.locket_clone.utils.s3Utils.S3Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Role;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locket-clone/post")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PostController {

    PostService postService;
    S3Service s3Service;
    ReactionService reactionService;
    ReportPostService reportPostService;
    UserService userService;
    UnreadPostService unreadPostService;
    ConversationService conversationService;
    SocketIOServer server;

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseData<String> addPost(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        if (multipartFile.getOriginalFilename() == null || multipartFile.getOriginalFilename().isEmpty() || FileUtils.validateFile(multipartFile)) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Wrong request format");
        }
        String imageURL = s3Service.uploadFile(multipartFile);
        return new ResponseData<>(ResponseCode.SUCCESS, "success", imageURL);
    }

    @PostMapping("/add-post")
    public ResponseData<?> addPost(@CurrentUser CustomUserDetail customUserDetail, @RequestBody AddPostRequest addPostRequest) {
        if (!addPostRequest.validateRequest()) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Wrong request format");
        }
        String userId = customUserDetail.getId();
        Post post = postService.addPost(addPostRequest, userId);
        AddUnreadPostRequest addUnreadPostRequest = new AddUnreadPostRequest(post.getFriendIds(), post.getId().toString());
        ObjectRequest request = new ObjectRequest(Constant.API.ADD_POST_TO_UNREAD_POST, addUnreadPostRequest);
        EventPostRunner.requests.add(request);
        ObjectRequest pushMessageToFCMRequest = new ObjectRequest(Constant.API.ADD_NOTIFICATION_NEW_POST, post);
        EventUserRunner.eventUserRequests.add(pushMessageToFCMRequest);
        if (StringUtils.hasLength(post.getId().toString())) {
            return new ResponseData<>(ResponseCode.SUCCESS, "success");
        }
        return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "unknown error");
    }

    @GetMapping("/get-posts")
    public ResponseData<List<GetPostResponse>> getAllPosts(@CurrentUser CustomUserDetail customUserDetail,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam("type") int type,
                                                           @RequestParam("friend_id") String friendId) {
        GetPostsRequest request = new GetPostsRequest(page, size, type, friendId);
        if (!request.validateRequest()) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Wrong request format");
        }
        Pageable pageable = PageRequest.of(page, size);
        String userId = customUserDetail.getId();
        EventPostRunner.requests.add(new ObjectRequest(Constant.API.CHANGE_UNREAD_POST_STATUS, userId));
        List<GetPostResponse> responseList = postService.getPosts(userId, request, pageable);
        return new ResponseData<>(ResponseCode.SUCCESS, "success", responseList);
    }

    @PostMapping("/react-post")
    public ResponseData<?> addReactionPost(@CurrentUser CustomUserDetail customUserDetail, @RequestBody AddReactionPost addReactionPost) {
        addReactionPost.setUserId(customUserDetail.getId());
        ObjectRequest objectRequest = new ObjectRequest(Constant.API.ADD_REACTION, addReactionPost);
        EventPostRunner.requests.add(objectRequest);
        return new ResponseData<>(ResponseCode.SUCCESS, "success");

    }

    @PostMapping("/report-post")
    public ResponseData<?> reportPost(@CurrentUser CustomUserDetail customUserDetail, @RequestParam("post_id") String postId) {
        ReportPostRequest reportPostRequest = new ReportPostRequest(customUserDetail.getId(), postId);
        ObjectRequest request = new ObjectRequest(Constant.API.REPORT_POST, reportPostRequest);
        EventPostRunner.requests.add(request);
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    @PutMapping("/hide-post")
    public ResponseData<?> hidePost(@CurrentUser CustomUserDetail customUserDetail, @RequestParam("post_id") String postId) {
        HidePostRequest hidePostRequest = new HidePostRequest(customUserDetail.getId(), postId);
        ObjectRequest request = new ObjectRequest(Constant.API.HIDE_POST, hidePostRequest);
        EventPostRunner.requests.add(request);
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    @GetMapping("/list-reaction")
    public ResponseData<List<GetReactionResponse>> listReaction(@CurrentUser CustomUserDetail customUserDetail, @RequestParam("post_id") String postId) {
        Post post = postService.findbyId(postId);
        String userId = customUserDetail.getId();
        if (Objects.isNull(post)) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Cant find post");
        }
        if(!post.getUserId().equals(userId)) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "User is not authorized");
        }
        Set<String> setReactionIds = post.getReactionIds();
        List<Reaction> listReactions = reactionService.getReactions(setReactionIds);
        List<GetReactionResponse> response = listReactions.stream().map(reaction -> {
            User user = userService.findUserById(reaction.getUserId());
            try {
                GetReactionResponse getReactionResponse = new GetReactionResponse();
                getReactionResponse.setFirstname(user.getFirstName());
                getReactionResponse.setAvt(user.getAvt());
                getReactionResponse.setReactions(reaction.getIcons());
                return getReactionResponse;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).toList();
        return new ResponseData<>(ResponseCode.SUCCESS, "success", response);
    }

    @DeleteMapping("/delete-post")
    public ResponseData<?> deletePost(@CurrentUser CustomUserDetail customUserDetail,@RequestParam("post_id") String postId) {
         DeletePostRequest deletePostRequest = new DeletePostRequest(customUserDetail.getId(), postId);
         ObjectRequest request = new ObjectRequest(Constant.API.DELETE_POST, deletePostRequest);
         EventPostRunner.requests.add(request);
         return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    @GetMapping("/get-unread-post")
    public ResponseData<?> getUnreadPost(@CurrentUser CustomUserDetail customUserDetail) {
        String userId = customUserDetail.getId();
        UnreadPost unreadPost = unreadPostService.getUnreadPosts(userId);
        if(!CollectionUtils.isEmpty(unreadPost.getPostIds())) {
            Post unreadPostNewest = postService.findbyId(unreadPost.getPostIds().getLast());
            GetUnreadPostResponse getUnreadPostResponse = new GetUnreadPostResponse();
            ModelMapperUtils.toObject(unreadPostNewest, getUnreadPostResponse);
            User ownerPost = userService.findUserById(unreadPost.getUserId());
            getUnreadPostResponse.setUnreadCount(unreadPost.getPostIds().size());
            getUnreadPostResponse.setFriendAvt(ownerPost.getAvt());
            getUnreadPostResponse.setId(unreadPostNewest.getId().toString());
            return new ResponseData<>(ResponseCode.SUCCESS, "success", getUnreadPostResponse);
        }
        Post newestPost = postService.getNewestPostByUserId(userId);
        GetUnreadPostResponse getLastesPost = new GetUnreadPostResponse();
        ModelMapperUtils.toObject(newestPost, getLastesPost);
        User ownerPost = userService.findUserById(unreadPost.getUserId());
        getLastesPost.setUnreadCount(unreadPost.getPostIds().size());
        getLastesPost.setFriendAvt(ownerPost.getAvt());
        getLastesPost.setId(newestPost.getId().toString());
        return new ResponseData<>(ResponseCode.SUCCESS, "success", getLastesPost);
    }

    @GetMapping("/get-report-post")
    public ResponseData<?> getReportPost(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<GetReportPosts> getReportPostsList = reportPostService.getReportPosts(pageable).stream().map(reportPost -> {
            GetReportPosts dto = new GetReportPosts();
            ModelMapperUtils.toObject(reportPost, dto);

            Post post = postService.findbyId(dto.getPostId());
            User user = userService.findUserById(dto.getUserId());

            if (post != null) {
                dto.setCaption(post.getCaption());
                dto.setImageURL(post.getImageURL());
                dto.setCreatedAt(post.getCreatedAt());
            }

            if (user != null) {
                dto.setUsername(user.getUsername());
            }

            return dto;
        }).toList();


        return new ResponseData<>(ResponseCode.SUCCESS, "success", getReportPostsList);
    }

    @DeleteMapping("/delete-post-by-admin")
    public ResponseData<?> deletePostByAdmin(@CurrentUser CustomUserDetail customUserDetail,
                                             @RequestParam("post_id") String postId) {
        String userId = customUserDetail.getId();
        Post post = postService.findbyId(postId);
        DeletePostRequest deletePostRequest = new DeletePostRequest(customUserDetail.getId(), postId);
        ObjectRequest objectRequest = new ObjectRequest(Constant.API.DELETE_POST_BY_ADMIN, deletePostRequest);
        EventPostRunner.requests.add(objectRequest);
        RemovePostNotificationRequest removePostNotificationRequest = new RemovePostNotificationRequest(post.getUserId(), "...", post.getCaption(), post.getImageURL());
        ObjectRequest addNotiRemovePostByAdmin = new ObjectRequest(Constant.API.DELETE_POST_BY_ADMIN, removePostNotificationRequest);
        EventUserRunner.eventUserRequests.add(addNotiRemovePostByAdmin);
        Notification notification = new Notification(postId, Constant.TYPE_OF_NOTIFICATION.REMOVE_POST_BY_ADMIN, userId);
        EventUserRunner.eventUserRequests.add(new ObjectRequest(Constant.API.SAVE_NOTIFICATION, notification));
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    @PostMapping("/reply-post")
    public ResponseData<?> replyPost(@CurrentUser CustomUserDetail customUserDetail,
                                     @RequestBody ReplyPostRequest replyPostRequest) throws JsonProcessingException {
        String userSender = customUserDetail.getId();
        String userReceiver = replyPostRequest.getUserId();
        Conversation conversation = conversationService.getConversationByUserIdAndFriendId(userReceiver, userSender);
        Message message = new Message(replyPostRequest.getContent(), conversation.getId().toString(), userSender, userReceiver, false, replyPostRequest.getPostURL());
        Map<String, Object> map = new HashMap<>();
        map.put("conversation_id", message.getConversationId());
        map.put("user_receiver", message.getUserReceiverId());
        map.put("content", message.getContent());
        map.put("post_url", message.getPostURL());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(map);
        Set<UUID> usersOnline = NettySocketIOServerRunner.onlineUsers.get(userReceiver);
        if(Objects.nonNull(usersOnline)) {
            usersOnline.forEach(uuid -> {
                SocketIOClient socketReceiver = server.getClient(uuid);
                if(socketReceiver != null) {
                    socketReceiver.sendEvent("receiver_message", json);
                }
            });
        }
        EventMessageRunner.eventMessageRequests.add(new ObjectRequest(Constant.API.UPLOAD_MESSAGE, message));
        EventMessageRunner.eventMessageRequests.add(new ObjectRequest(Constant.API.UPLOAD_LAST_MESSAGE, message));
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }
}
