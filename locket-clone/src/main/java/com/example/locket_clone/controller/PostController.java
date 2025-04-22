package com.example.locket_clone.controller;

import com.example.locket_clone.config.CurrentUser;
import com.example.locket_clone.config.security.CustomUserDetail;
import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.Reaction;
import com.example.locket_clone.entities.UnreadPost;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddPostRequest;
import com.example.locket_clone.entities.request.AddReactionPost;
import com.example.locket_clone.entities.request.AddUnreadPostRequest;
import com.example.locket_clone.entities.request.DeletePostRequest;
import com.example.locket_clone.entities.request.GetPostsRequest;
import com.example.locket_clone.entities.request.HidePostRequest;
import com.example.locket_clone.entities.request.ObjectRequest;
import com.example.locket_clone.entities.request.ReportPostRequest;
import com.example.locket_clone.entities.response.GetPostResponse;
import com.example.locket_clone.entities.response.GetReactionResponse;
import com.example.locket_clone.entities.response.GetUnreadPostResponse;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.runner.EventPostRunner;
import com.example.locket_clone.runner.EventUserRunner;
import com.example.locket_clone.service.PostService;
import com.example.locket_clone.service.ReactionService;
import com.example.locket_clone.service.ReportPostService;
import com.example.locket_clone.service.UnreadPostService;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.Constant;
import com.example.locket_clone.utils.Constant.ResponseCode;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import com.example.locket_clone.utils.fileUtils.FileUtils;
import com.example.locket_clone.utils.s3Utils.S3Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
        EventUserRunner.requests.add(pushMessageToFCMRequest);
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
}
