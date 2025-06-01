package com.example.locket_clone.controller;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.response.GetAllPostResponse;
import com.example.locket_clone.entities.response.GetReportPosts;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.service.PostService;
import com.example.locket_clone.service.ReportPostService;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.ResponseCode;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import com.mongodb.lang.Nullable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/locket-clone/backend")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BackendController {

    PostService postService;
    ReportPostService reportPostService;
    UserService userService;

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
            User user = userService.findUserById(dto.getUserId());

            if (post != null) {
                dto.setCaption(post.getCaption());
                dto.setImageURL(post.getImageURL());
                dto.setCreatedAt(post.getCreatedAt());
            }

            if (user != null) {
                dto.setOnwerId(user.getId().toString());
            }

            return dto;
        }).toList();


        return new ResponseData<>(ResponseCode.SUCCESS, "success", getReportPostsList);
    }
}
