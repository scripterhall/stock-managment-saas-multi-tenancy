package com.nourallah.saasapp.mappers;

import com.nourallah.saasapp.entities.User;
import com.nourallah.saasapp.requests.UserRequest;
import com.nourallah.saasapp.responses.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public User toEntity(UserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .role(request.getRole())
                .build();
    }

    public UserResponse toResponse(User entity) {
        return UserResponse.builder()
                .username(entity.getUsername())
                .email(entity.getEmail())
                .lastName(entity.getLastName())
                .firstName(entity.getFirstName())
                .role(entity.getRole())
                .build();
    }
}
