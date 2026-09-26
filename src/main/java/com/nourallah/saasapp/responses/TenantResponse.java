package com.nourallah.saasapp.responses;

import com.nourallah.saasapp.entities.TenantStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TenantResponse {
    private String tenantId;
    private String companyName;
    private String companyCode;
    private String email;
    // initial admin credentials
    private String adminFullName;
    private String adminEmail;
    private String adminUsername;
    private String adminPassword;
    private LocalDateTime createdAt;
    private TenantStatus status;
}
