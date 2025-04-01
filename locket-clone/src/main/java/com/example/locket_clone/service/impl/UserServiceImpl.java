package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.Role;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.AddUserRequest;
import com.example.locket_clone.repository.InterfacePackage.RoleRepository;
import com.example.locket_clone.repository.InterfacePackage.UserRepository;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public void insertUser(AddUserRequest user) {
        User userInsert = ModelMapperUtils.toObject(user, User.class);
//        Query query = new Query(Criteria.where(Role.NAME).is("user"));
//        Role role = roleRepository.findOne(query, Role.class);
//        userInsert.getAuthorities().add(role.getId().toString());
        userInsert.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userInsert);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
