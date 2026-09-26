package com.nourallah.saasapp.responses;


import com.nourallah.saasapp.entities.UserRole;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class UserResponse {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
}
