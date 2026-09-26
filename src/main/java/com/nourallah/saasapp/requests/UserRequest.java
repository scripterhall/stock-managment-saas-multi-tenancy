package com.nourallah.saasapp.requests;

import com.nourallah.saasapp.entities.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    @NotBlank(message = "Username should not be empty")
    private String username;
    @NotBlank(message = "Email should not be empty")
    private String email;
    @NotBlank(message = "Password should not be empty")
    @Size(min = 8 , message = "Password should be at least 8 characters long")
    private String password;
    @NotBlank(message = "First name should be not empty")
    private String firstName;
    @NotBlank(message = "Last name should be not empty")
    private String lastName;
    @NotNull(message = "Role should be not null")
    private UserRole role;
}
