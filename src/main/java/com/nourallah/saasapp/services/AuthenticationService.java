package com.nourallah.saasapp.services;

import com.nourallah.saasapp.requests.LoginRequest;
import com.nourallah.saasapp.responses.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(final LoginRequest request);
}
