package com.example.locket_clone.controller;

import com.example.locket_clone.config.CurrentUser;
import com.example.locket_clone.config.security.CustomUserDetail;
import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.request.AddPostRequest;
import com.example.locket_clone.entities.request.AddReactionPost;
import com.example.locket_clone.entities.request.GetPostsRequest;
import com.example.locket_clone.entities.response.GetPostResponse;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.service.PostService;
import com.example.locket_clone.service.ReactionService;
import com.example.locket_clone.service.ReportPostService;
import com.example.locket_clone.utils.Constant.ResponseCode;
import com.example.locket_clone.utils.fileUtils.FileUtils;
import com.example.locket_clone.utils.s3Utils.S3Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locket-clone/post")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PostController {

    PostService postService;
    S3Service s3Service;
    ReactionService reactionService;
    ReportPostService reportPostService;

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseData<String> addPost(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        if(multipartFile.getOriginalFilename() == null || multipartFile.getOriginalFilename().isEmpty() || FileUtils.validateFile(multipartFile)){
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Wrong request format");
        }
        String imageURL = s3Service.uploadFile(multipartFile);
        return new ResponseData<>(ResponseCode.SUCCESS, "success", imageURL);
    }

    @PostMapping("/add-post")
    public ResponseData<?> addPost(@CurrentUser CustomUserDetail customUserDetail, @RequestBody AddPostRequest addPostRequest) {
        if(!addPostRequest.validateRequest()) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Wrong request format");
        }
        String userId = customUserDetail.getId();
        boolean success = postService.addPost(addPostRequest, userId);
        if(success){
            return new ResponseData<>(ResponseCode.SUCCESS, "success");
        }
        return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "unknown error");
    }

    @GetMapping("/get-posts")
    public ResponseData<List<GetPostResponse>> getAllPosts(@CurrentUser CustomUserDetail customUserDetail,
                                                           @RequestBody GetPostsRequest getPostsRequest) {
        if(!getPostsRequest.validateRequest()) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Wrong request format");
        }
        Pageable pageable = PageRequest.of(getPostsRequest.getPage(), getPostsRequest.getSize());
        List<GetPostResponse> responseList = postService.getPosts(customUserDetail.getId(), getPostsRequest, pageable);
        return new ResponseData<>(ResponseCode.SUCCESS, "success", responseList);
    }

    @PostMapping("/react-post")
    public ResponseData<?> addReactionPost(@CurrentUser CustomUserDetail customUserDetail, @RequestBody AddReactionPost addReactionPost) {
        Post post = postService.findbyId(addReactionPost.getPostId());
        if(Objects.isNull(post)) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Cant find this post");
        }
        if(!post.getFriendIds().contains(customUserDetail.getId())) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "User cant read this post");
        }
        if(post.getUserId().equals(customUserDetail.getId())) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "User cant reaction my own post");
        }
        addReactionPost.setUserId(customUserDetail.getId());
        String reactionId = reactionService.addReaction(addReactionPost);
        boolean result = postService.addReactionToPost(post, reactionId);
        if(result) {
            return new ResponseData<>(ResponseCode.SUCCESS, "success");
        }
        return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "Cant find post");
    }

    @GetMapping("/report-post")
    public ResponseData<?> reportPost(@CurrentUser CustomUserDetail customUserDetail, @RequestParam("post_id") String postId) {
        String userId = customUserDetail.getId();
        Post post = postService.findbyId(postId);
        if(Objects.isNull(post)) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Cant find this post");
        }
        if(!post.getFriendIds().contains(userId)) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "User cant read this post");
        }
        reportPostService.addReportPost(userId, postId);
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    @PutMapping("/hide-post")
    public ResponseData<?> hidePost(@CurrentUser CustomUserDetail customUserDetail, @RequestParam("post_id") String postId) {
        Post post = postService.findbyId(postId);
        String userId = customUserDetail.getId();
        if(Objects.isNull(post)) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "Cant find this post");
        }
        if(!post.getFriendIds().contains(userId)) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "User cant read this post");
        }
        if(post.getUserId().equals(userId)) {
            return new ResponseData<>(ResponseCode.WRONG_DATA_FORMAT, "User cannot hide this post");
        }
        boolean result = postService.hidePost(post, userId);
        if(!result) {
            return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "Hide post failed");
        }
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

}
