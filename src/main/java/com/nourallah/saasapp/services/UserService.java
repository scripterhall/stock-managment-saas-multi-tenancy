package com.nourallah.saasapp.services;

import com.nourallah.saasapp.common.PageResponse;
import com.nourallah.saasapp.requests.UserRequest;
import com.nourallah.saasapp.responses.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {


    void createUser(final UserRequest request);
    void updateUser(final String userId, final UserRequest request);

    void deleteUser(final String userId);

    UserResponse getUserById(final String userId);

    PageResponse<UserResponse> getUsers(final int page, final int size);

    void enableUser(final String userId);
    void disableUser(final String userId);
}
