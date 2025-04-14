package com.example.locket_clone.controller;

import com.example.locket_clone.config.CurrentUser;
import com.example.locket_clone.config.security.CustomUserDetail;
import com.example.locket_clone.entities.request.AddPostRequest;
import com.example.locket_clone.entities.request.AddReactionPost;
import com.example.locket_clone.entities.response.GetPostResponse;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.service.PostService;
import com.example.locket_clone.service.ReactionService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locket-clone/post")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PostController {

    PostService postService;
    S3Service s3Service;
    ReactionService reactionService;

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

    @GetMapping("/get-all-posts")
    public ResponseData<List<GetPostResponse>> getAllPosts(@CurrentUser CustomUserDetail customUserDetail,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<GetPostResponse> responseList = postService.getPosts(customUserDetail.getId(), pageable);
        return new ResponseData<>(ResponseCode.SUCCESS, "success", responseList);
    }

    @PostMapping("/react-post")
    public ResponseData<?> addReactionPost(@CurrentUser CustomUserDetail customUserDetail, @RequestBody AddReactionPost addReactionPost) {
        addReactionPost.setUserId(customUserDetail.getId());
        String reactionId = reactionService.addReaction(addReactionPost);
        boolean result = postService.addReactionToPost(addReactionPost.getPostId(), reactionId);
        if(result) {
            return new ResponseData<>(ResponseCode.SUCCESS, "success");
        }
        return new ResponseData<>(ResponseCode.UNKNOWN_ERROR, "Cant find post");
    }
}
