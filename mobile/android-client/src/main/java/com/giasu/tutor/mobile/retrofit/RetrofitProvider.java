package com.giasu.tutor.mobile.retrofit;

import com.giasu.tutor.mobile.ApiConfig;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitProvider {

    private RetrofitProvider() {
    }

    private static Retrofit retrofit;

    public static synchronized Retrofit getInstance(TokenProvider tokenProvider) {
        if (retrofit == null) {
            retrofit = buildRetrofit(tokenProvider);
        }
        return retrofit;
    }

    private static Retrofit buildRetrofit(TokenProvider tokenProvider) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(new AuthorizationInterceptor(tokenProvider))
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(ApiConfig.getBaseUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static class AuthorizationInterceptor implements Interceptor {

        private final TokenProvider tokenProvider;

        AuthorizationInterceptor(TokenProvider tokenProvider) {
            this.tokenProvider = Objects.requireNonNull(tokenProvider);
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            String token = tokenProvider.getToken();
            if (token == null || token.isBlank()) {
                return chain.proceed(original);
            }
            Request request = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(request);
        }
    }
}