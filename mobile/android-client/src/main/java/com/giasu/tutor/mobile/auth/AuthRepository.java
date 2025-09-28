package com.giasu.tutor.mobile.auth;

import com.giasu.tutor.mobile.model.AuthModels.AuthResponse;
import com.giasu.tutor.mobile.model.AuthModels.LoginRequest;
import com.giasu.tutor.mobile.model.AuthModels.RegisterRequest;
import com.giasu.tutor.mobile.retrofit.RetrofitProvider;
import com.giasu.tutor.mobile.retrofit.TokenProvider;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import retrofit2.Response;

public class AuthRepository {

    private final AuthApi authApi;

    public AuthRepository(TokenProvider tokenProvider) {
        this.authApi = RetrofitProvider.getInstance(tokenProvider).create(AuthApi.class);
    }

    public CompletableFuture<AuthResponse> login(LoginRequest request) {
        return CompletableFuture.supplyAsync(() -> execute(authApi.login(request)));
    }

    public CompletableFuture<AuthResponse> register(RegisterRequest request) {
        return CompletableFuture.supplyAsync(() -> execute(authApi.register(request)));
    }

    private AuthResponse execute(retrofit2.Call<AuthResponse> call) {
        try {
            Response<AuthResponse> response = call.execute();
            if (!response.isSuccessful() || response.body() == null) {
                throw new IllegalStateException("Auth request failed with status " + response.code());
            }
            return response.body();
        } catch (IOException e) {
            throw new IllegalStateException("Network error", e);
        }
    }
}