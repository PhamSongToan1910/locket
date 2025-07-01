package com.example.locket_clone.repository.InterfacePackage;

import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.request.FindUserBeRequest;
import com.example.locket_clone.entities.response.GetNmberUserOrderByDateResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserCustomRepository {
    List<User> findUserNormal(Pageable pageable, FindUserBeRequest findUserBeRequest);

    List<GetNmberUserOrderByDateResponse> getUserOrderByDay();
}
