package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddPostRequest;
import com.example.locket_clone.entities.response.GetFriendResponse;
import com.example.locket_clone.entities.response.GetPostResponse;
import com.example.locket_clone.repository.InterfacePackage.PostRepository;
import com.example.locket_clone.service.PostService;
import com.example.locket_clone.service.UserFriendsService;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.Constant;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImpl implements PostService {

    PostRepository postRepository;
    UserFriendsService userFriendsService;
    UserService userService;

    @Override
    public boolean addPost(AddPostRequest addPostRequest, String userId) {
        try{
            Post post = new Post();
            post.setUserId(userId);
            if(addPostRequest.getType() == Constant.TYPE_ADD_POST.PUBLIC) {
                List<GetFriendResponse> getFriendResponseList = userFriendsService.getAllFriends(userId);
                addPostRequest.getFriendIds().addAll(getFriendResponseList.stream().map(GetFriendResponse::getId).toList());
            }
            addPostRequest.getFriendIds().add(userId);
            ModelMapperUtils.toObject(addPostRequest, post);
            postRepository.save(post);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<GetPostResponse> getPosts(String userId, Pageable pageable) {
        List<Post> getAllPosts = postRepository.GetAllPosts(userId, pageable);
        List<GetPostResponse> responseList = new ArrayList<>();
        for (Post post : getAllPosts) {
            GetPostResponse getPostResponse = new GetPostResponse();
            ModelMapperUtils.toObject(post, getPostResponse);
            if(post.getUserId().equals(userId)) {
                getPostResponse.setFriendPost(false);
                if(CollectionUtils.isEmpty(post.getReactionIds())) {
                    getPostResponse.setFriendReaction(false);
                } else {
                    getPostResponse.setFriendReaction(true);
                }
            } else {
                getPostResponse.setFriendPost(true);
                User ownerPost = userService.findUserById(post.getUserId());
                if(ownerPost != null) {
                    getPostResponse.setFriendAvt(ownerPost.getAvt());
                }
            }
            responseList.add(getPostResponse);
        }
        return responseList;
    }

    @Override
    public boolean addReactionToPost(String postId, String reactionId) {
        Post post = postRepository.findById(postId).orElse(null);
        if(Objects.isNull(post)) {
            return false;
        }
        post.getReactionIds().add(reactionId);
        postRepository.save(post);
        return true;
    }
}
