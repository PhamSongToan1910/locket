package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.Role;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddUserRequest;
import com.example.locket_clone.entities.request.SearchUserRequest;
import com.example.locket_clone.entities.request.UpdateUserInfoRequest;
import com.example.locket_clone.entities.request.UpdateUserInforV2Request;
import com.example.locket_clone.entities.response.GetNmberUserOrderByDateResponse;
import com.example.locket_clone.entities.response.GetUserInfoBEResponse;
import com.example.locket_clone.entities.response.SearchFriendByUsernameResponse;
import com.example.locket_clone.repository.InterfacePackage.RoleRepository;
import com.example.locket_clone.repository.InterfacePackage.UserRepository;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.Constant;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;

    @Override
    public User insertUser(AddUserRequest user) {
        User userInsert = new User();
        ModelMapperUtils.toObject(user, userInsert);
        Role role = roleRepository.findByName(Constant.ROLE.USER_ROLE);
        userInsert.getAuthorities().add(role.getId().toString());
        userInsert.getDeviceToken().add(user.getDeviceToken());
        userRepository.save(userInsert);
        return userInsert;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean updateUser(UpdateUserInfoRequest user, String userId) {
        User userInsert = userRepository.findById(userId).orElse(null);
        if(userInsert == null) {
            return false;
        }
        ModelMapperUtils.toObject(user, userInsert);
        userRepository.save(userInsert);
        return true;
    }

    @Override
    public Boolean updateUserV2(UpdateUserInforV2Request updateUserInforV2, String userId) {
        User updateUser = userRepository.findById(userId).orElse(null);
        if(updateUser != null) {
            ModelMapperUtils.toObject(updateUserInforV2, updateUser);
            userRepository.save(updateUser);
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateAvt(String userId, String avtPath) {
        User findUser = userRepository.findById(userId).orElse(null);
        if(findUser != null) {
            findUser.setAvt(avtPath);
            userRepository.save(findUser);
            return true;
        }
        return false;
    }

    @Override
    public SearchFriendByUsernameResponse searchByUsername(String username) {
        User user = this.findUserByUsername(username);
        if(Objects.nonNull(user)) {
            SearchFriendByUsernameResponse searchFriendByUsernameResponse = new SearchFriendByUsernameResponse();
            ModelMapperUtils.toObject(user, searchFriendByUsernameResponse);
            return searchFriendByUsernameResponse;
        }
        return null;
    }

    @Override
    public void updateDeviceToken(User user) {
        userRepository.save(user);
    }

    @Override
    public Set<String> getDeviceTokens(List<String> userIds) {
        Set<String> deviceTokens = new HashSet<>();
        for (String userId : userIds) {
            User user = userRepository.findById(userId).orElse(null);
            if(user != null) {
                deviceTokens.addAll(user.getDeviceToken());
            }
        }
        return deviceTokens;
    }

    @Override
    public Set<String> getDeviceTokenByUserID(String userID) {
        User user = userRepository.findById(userID).orElse(null);
        if(user != null) {
            return user.getDeviceToken();
        }
        return null;
    }

    @Override
    public List<User> getAllUserNormal(Pageable pageable) {
        return userRepository.findUserNormal(pageable);
    }

    @Override
    public void addUserAdmin(String email, String password) {
        User user = new User();
        user.getAuthorities().add("6819716727d55c531dab4db6");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public List<GetNmberUserOrderByDateResponse> getUserOrderByCreateAt() {
        return userRepository.getUserOrderByDay();
    }


    public void changeActive(String userId, boolean isActive) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setIsDeleted(isActive);
            userRepository.save(user);
        }
    }

    public Page<GetUserInfoBEResponse> searchUser(SearchUserRequest request, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (request.getKeyword() != null && !request.getKeyword().isEmpty() &&
                request.getValue() != null && !request.getValue().isEmpty()) {
            criteriaList.add(Criteria.where(request.getKeyword()).regex(request.getValue(), "i"));
        }
        if (request.getStartDate() != null && request.getEndDate() != null) {
            criteriaList.add(Criteria.where("createdAt").gte(request.getStartDate()).lte(request.getEndDate()));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, User.class);
        query.with(pageable);

        List<User> users = mongoTemplate.find(query, User.class);
        // Chuyển sang DTO
        List<GetUserInfoBEResponse> responseList = users.stream().map(user -> {
            GetUserInfoBEResponse response = new GetUserInfoBEResponse();
            ModelMapperUtils.toObject(user, response);
            response.convertCreateAtInstantToString(user.getCreatedAt());
            return response;
        }).toList();

        return new PageImpl<>(responseList, pageable, total);
    }

}
