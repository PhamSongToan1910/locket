package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddPostRequest;
import com.example.locket_clone.entities.request.GetPostsRequest;
import com.example.locket_clone.entities.response.GetFriendResponse;
import com.example.locket_clone.entities.response.GetPostResponse;
import com.example.locket_clone.repository.InterfacePackage.PostRepository;
import com.example.locket_clone.service.PostService;
import com.example.locket_clone.service.UserFriendsService;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.Constant;
import com.example.locket_clone.utils.DateTimeConvertUtils;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImpl implements PostService {

    PostRepository postRepository;
    UserFriendsService userFriendsService;
    UserService userService;

    int TIME_OF_9_DAYS_AGO = 777600000;

    @Override
    public Post addPost(AddPostRequest addPostRequest, String userId) {
        try{
            Post post = new Post();
            post.setUserId(userId);
            if(addPostRequest.getType() == Constant.TYPE_ADD_POST.PUBLIC) {
                List<GetFriendResponse> getFriendResponseList = userFriendsService.getAllFriends(userId);
                addPostRequest.getFriendIds().addAll(getFriendResponseList.stream().map(GetFriendResponse::getId).toList());
            }
            addPostRequest.getFriendIds().add(userId);
            ModelMapperUtils.toObject(addPostRequest, post);
            return postRepository.save(post);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<GetPostResponse> getPosts(String userId, GetPostsRequest getPostsRequest, Pageable pageable) {
        switch (getPostsRequest.getType()) {
            case Constant.TYPE_GET_POST.FRIEND_DETAIL -> {
                return getPostByUserId(userId, getPostsRequest, pageable);
            }
            case Constant.TYPE_GET_POST.ME -> {
                return getMyPost(userId, pageable);
            }
            default -> {
                return getAllPosts(userId, getPostsRequest, pageable);
            }
        }
    }

    @Override
    public boolean addReactionToPost(Post post, String reactionId) {
        if(Objects.isNull(post)) {
            return false;
        }
        post.getReactionIds().add(reactionId);
        postRepository.save(post);
        return true;
    }

    @Override
    public Post findbyId(String postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public boolean hidePost(Post post, String userId) {
        if(Objects.isNull(post) || !StringUtils.hasLength(userId)) {
            return false;
        }
        post.getFriendIds().remove(userId);
        postRepository.save(post);
        return true;
    }

    @Override
    public void deletePost(String postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public List<Post> getPostsByIds(Set<String> postIds) {
        return postIds.stream().map(id -> postRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Post getNewestPostByUserId(String userId) {
        return postRepository.getNewestPostByUserId(userId);
    }

    private List<GetPostResponse> getAllPosts(String userId, GetPostsRequest getPostsRequest, Pageable pageable) {
        List<Post> getAllPosts = postRepository.GetAllPosts(userId, pageable);
        List<GetPostResponse> responseList = new ArrayList<>();
        for (Post post : getAllPosts) {
            GetPostResponse getPostResponse = new GetPostResponse();
            ModelMapperUtils.toObject(post, getPostResponse);
            Date createAt = Date.from(post.getCreatedAt());
            if(new Date().getTime() -  createAt.getTime() > TIME_OF_9_DAYS_AGO){
                getPostResponse.setCreateAt(DateTimeConvertUtils.convertDateToString(createAt));
            } else {
                getPostResponse.setCreateAt((new Date().getTime() - createAt.getTime())/(TIME_OF_9_DAYS_AGO/9) + "d");
            }
            User ownerPost = userService.findUserById(post.getUserId());
            getPostResponse.setFirstname(ownerPost.getFirstName());
            if(post.getUserId().equals(userId)) {
                getPostResponse.setFriendPost(false);
                if(CollectionUtils.isEmpty(post.getReactionIds())) {
                    getPostResponse.setFriendReaction(false);
                } else {
                    getPostResponse.setFriendReaction(true);
                }
            } else {
                getPostResponse.setFriendPost(true);
            }
            getPostResponse.setFriendAvt(ownerPost.getAvt());
            responseList.add(getPostResponse);
        }
        return responseList;
    }

    private List<GetPostResponse> getPostByUserId(String userId, GetPostsRequest getPostsRequest, Pageable pageable) {
        List<Post> listPostsFriendid = postRepository.getPostByFriendId(userId, getPostsRequest.getFriendId(), pageable);
        List<GetPostResponse> responseList = new ArrayList<>();
        User ownerPost = userService.findUserById(getPostsRequest.getFriendId());
        for (Post post : listPostsFriendid) {
            GetPostResponse getPostResponse = new GetPostResponse();
            ModelMapperUtils.toObject(post, getPostResponse);
            Date createAt = Date.from(post.getCreatedAt());
            if(new Date().getTime() -  createAt.getTime() > TIME_OF_9_DAYS_AGO){
                getPostResponse.setCreateAt(DateTimeConvertUtils.convertDateToString(createAt));
            } else {
                getPostResponse.setCreateAt((new Date().getTime() - createAt.getTime())/(TIME_OF_9_DAYS_AGO/9) + "d");
            }
            getPostResponse.setFriendPost(true);
            if(ownerPost != null) {
                getPostResponse.setFriendAvt(ownerPost.getAvt());
                getPostResponse.setFirstname(ownerPost.getFirstName());
            }
            responseList.add(getPostResponse);
        }
        return responseList;
    }

    private List<GetPostResponse> getMyPost(String userId, Pageable pageable) {
        List<Post> listMyPosts = postRepository.getMyPosts(userId, pageable);
        List<GetPostResponse> responseList = new ArrayList<>();
        User ownerPost = userService.findUserById(userId);
        for (Post post : listMyPosts) {
            GetPostResponse getPostResponse = new GetPostResponse();
            ModelMapperUtils.toObject(post, getPostResponse);
            Date createAt = Date.from(post.getCreatedAt());
            if(new Date().getTime() -  createAt.getTime() > TIME_OF_9_DAYS_AGO){
                getPostResponse.setCreateAt(DateTimeConvertUtils.convertDateToString(createAt));
            } else {
                getPostResponse.setCreateAt((new Date().getTime() - createAt.getTime())/(TIME_OF_9_DAYS_AGO/9) + "d");
            }
            getPostResponse.setFriendPost(false);
            if(CollectionUtils.isEmpty(post.getReactionIds())) {
                getPostResponse.setFriendReaction(false);
            } else {
                getPostResponse.setFriendReaction(true);
            }
            getPostResponse.setFriendAvt(ownerPost.getAvt());
            responseList.add(getPostResponse);
        }
        return responseList;
    }
}
