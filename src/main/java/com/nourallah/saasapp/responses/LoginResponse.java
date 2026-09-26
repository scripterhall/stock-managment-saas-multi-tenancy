package com.nourallah.saasapp.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String access_token;
    private String tokenType;
}
