package com.giasu.tutor.mobile.booking;

import com.giasu.tutor.mobile.model.BookingModels.BookingCreateRequest;
import com.giasu.tutor.mobile.model.BookingModels.BookingResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookingApi {

    @POST("/api/v1/bookings")
    Call<BookingResponse> create(@Body BookingCreateRequest request);

    @GET("/api/v1/bookings/tutor/{tutorProfileId}")
    Call<List<BookingResponse>> listByTutor(@Path("tutorProfileId") String tutorProfileId);

    @GET("/api/v1/bookings/student/{studentProfileId}")
    Call<List<BookingResponse>> listByStudent(@Path("studentProfileId") String studentProfileId);

    @POST("/api/v1/bookings/{bookingId}/confirm")
    Call<BookingResponse> confirm(@Path("bookingId") String bookingId);

    @POST("/api/v1/bookings/{bookingId}/cancel")
    Call<BookingResponse> cancel(@Path("bookingId") String bookingId);

    @POST("/api/v1/bookings/{bookingId}/complete")
    Call<BookingResponse> complete(@Path("bookingId") String bookingId);
}