package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserCustomRepository {
    List<User> findUserNormal(Pageable pageable);

    List<User> getUserOrderByDay();
}
