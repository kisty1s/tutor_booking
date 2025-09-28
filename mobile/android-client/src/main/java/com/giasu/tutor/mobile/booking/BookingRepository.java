package com.giasu.tutor.mobile.booking;

import com.giasu.tutor.mobile.model.BookingModels.BookingCreateRequest;
import com.giasu.tutor.mobile.model.BookingModels.BookingResponse;
import com.giasu.tutor.mobile.retrofit.RetrofitProvider;
import com.giasu.tutor.mobile.retrofit.TokenProvider;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import retrofit2.Call;
import retrofit2.Response;

public class BookingRepository {

    private final BookingApi bookingApi;

    public BookingRepository(TokenProvider tokenProvider) {
        this.bookingApi = RetrofitProvider.getInstance(tokenProvider).create(BookingApi.class);
    }

    public CompletableFuture<BookingResponse> create(BookingCreateRequest request) {
        return CompletableFuture.supplyAsync(() -> execute(bookingApi.create(request)));
    }

    public CompletableFuture<List<BookingResponse>> listTutor(String tutorProfileId) {
        return CompletableFuture.supplyAsync(() -> executeList(bookingApi.listByTutor(tutorProfileId)));
    }

    public CompletableFuture<List<BookingResponse>> listStudent(String studentProfileId) {
        return CompletableFuture.supplyAsync(() -> executeList(bookingApi.listByStudent(studentProfileId)));
    }

    public CompletableFuture<BookingResponse> confirm(String bookingId) {
        return CompletableFuture.supplyAsync(() -> execute(bookingApi.confirm(bookingId)));
    }

    public CompletableFuture<BookingResponse> cancel(String bookingId) {
        return CompletableFuture.supplyAsync(() -> execute(bookingApi.cancel(bookingId)));
    }

    public CompletableFuture<BookingResponse> complete(String bookingId) {
        return CompletableFuture.supplyAsync(() -> execute(bookingApi.complete(bookingId)));
    }

    private BookingResponse execute(Call<BookingResponse> call) {
        try {
            Response<BookingResponse> response = call.execute();
            if (!response.isSuccessful() || response.body() == null) {
                throw new IllegalStateException("Booking request failed with status " + response.code());
            }
            return response.body();
        } catch (IOException e) {
            throw new IllegalStateException("Network error", e);
        }
    }

    private List<BookingResponse> executeList(Call<List<BookingResponse>> call) {
        try {
            Response<List<BookingResponse>> response = call.execute();
            if (!response.isSuccessful() || response.body() == null) {
                throw new IllegalStateException("Booking request failed with status " + response.code());
            }
            return response.body();
        } catch (IOException e) {
            throw new IllegalStateException("Network error", e);
        }
    }
}