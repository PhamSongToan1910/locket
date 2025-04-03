package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.Role;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddUserRequest;
import com.example.locket_clone.entities.request.UpdateUserInfoRequest;
import com.example.locket_clone.entities.request.UpdateUserInforV2Request;
import com.example.locket_clone.repository.InterfacePackage.RoleRepository;
import com.example.locket_clone.repository.InterfacePackage.UserRepository;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.Constant;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;

    @Override
    public User insertUser(AddUserRequest user) {
        User userInsert = ModelMapperUtils.toObject(user, User.class);
        Role role = roleRepository.findByName(Constant.ROLE.USER_ROLE);
        userInsert.getAuthorities().add(role.getId().toString());
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
        User userInsert = ModelMapperUtils.toObject(user, User.class);
        userInsert.setId(new ObjectId(userId));
        userRepository.save(userInsert);
        return true;
    }

    @Override
    public Boolean updateUserV2(UpdateUserInforV2Request updateUserInforV2, String userId) {
        User updateUser = ModelMapperUtils.toObject(updateUserInforV2, User.class);
        updateUser.setId(new ObjectId(userId));
        userRepository.save(updateUser);
        return true;
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
}
