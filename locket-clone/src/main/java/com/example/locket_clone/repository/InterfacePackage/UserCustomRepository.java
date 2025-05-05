package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.User;

import java.util.List;

public interface UserCustomRepository {
    List<User> findUserNormal();
}
