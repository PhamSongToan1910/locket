package com.example.locket_clone.controller;

import com.example.locket_clone.config.CurrentUser;
import com.example.locket_clone.config.security.CustomUserDetail;
import com.example.locket_clone.entities.request.AddPostRequest;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.service.PostService;
import com.example.locket_clone.utils.Constant.Constant;
import com.example.locket_clone.utils.Constant.ResponseCode;
import com.example.locket_clone.utils.s3Utils.S3Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locket-clone/post")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PostController {

    PostService postService;
    S3Service s3Service;

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseData<String> addPost(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        if(multipartFile.getOriginalFilename() == null || multipartFile.getOriginalFilename().isEmpty()){
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
}
