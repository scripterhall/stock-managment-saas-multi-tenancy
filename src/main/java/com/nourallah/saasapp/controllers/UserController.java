package com.nourallah.saasapp.controllers;


import com.nourallah.saasapp.common.PageResponse;
import com.nourallah.saasapp.requests.UserRequest;
import com.nourallah.saasapp.responses.UserResponse;
import com.nourallah.saasapp.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('COMPANY_ADMIN')")
    public ResponseEntity<Void> createUser(
            @Valid
            @RequestBody
            final UserRequest userRequest
    ){
        this.userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_ADMIN' , 'ADMINISTRATOR','PLATFORM_ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(name = "page" , defaultValue = "0")
            final int page,
            @RequestParam(name = "size" , defaultValue = "10")
            final int size
    ){
        return ResponseEntity.ok(userService.getUsers(page, size));
    }

    @GetMapping("/{user-id}")
    @PreAuthorize("hasAnyRole('COMPANY_ADMIN' ,'ADMINISTRATOR')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable("user-id")
            final String id
    ){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{user-id}")
    @PreAuthorize("hasRole('COMPANY_ADMIN')")
    public ResponseEntity<Void>  updateUser(
            @PathVariable("user-id")
            final String id,
            @Valid
            @RequestBody
            final UserRequest userRequest
    ){
        this.userService.updateUser(id, userRequest);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


    @DeleteMapping("/{user-id}")
    @PreAuthorize("hasRole('COMPANY_ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("user-id")
            final String id
    ){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{user-id}/enable")
    @PreAuthorize("hasRole('COMPANY_ADMIN')")
    public ResponseEntity<Void> enableUser(
            @PathVariable("user-id")
            final String id
    ){
        userService.enableUser(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


    @PatchMapping("/{user-id}/disable")
    @PreAuthorize("hasRole('COMPANY_ADMIN')")
    @Operation(summary = "Disable user" , description = "Disable a user account (prevents login) .")
    public ResponseEntity<Void> disableUser(
            @PathVariable("user-id")
            final String id
    ){
        userService.disableUser(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }



}
