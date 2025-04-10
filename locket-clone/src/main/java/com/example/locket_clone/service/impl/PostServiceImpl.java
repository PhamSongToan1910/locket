package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.request.AddPostRequest;
import com.example.locket_clone.entities.response.GetFriendResponse;
import com.example.locket_clone.repository.InterfacePackage.PostRepository;
import com.example.locket_clone.service.PostService;
import com.example.locket_clone.service.UserFriendsService;
import com.example.locket_clone.utils.Constant.Constant;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImpl implements PostService {

    PostRepository postRepository;
    UserFriendsService userFriendsService;

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
}
