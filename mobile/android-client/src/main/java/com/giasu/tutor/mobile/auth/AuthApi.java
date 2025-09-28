package com.giasu.tutor.mobile.auth;

import com.giasu.tutor.mobile.model.AuthModels.AuthResponse;
import com.giasu.tutor.mobile.model.AuthModels.LoginRequest;
import com.giasu.tutor.mobile.model.AuthModels.RegisterRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("/api/v1/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("/api/v1/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);
}