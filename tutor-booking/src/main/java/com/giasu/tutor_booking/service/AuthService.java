package com.giasu.tutor_booking.service;

import com.giasu.tutor_booking.dto.auth.AuthResponse;
import com.giasu.tutor_booking.dto.auth.LoginRequest;
import com.giasu.tutor_booking.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}