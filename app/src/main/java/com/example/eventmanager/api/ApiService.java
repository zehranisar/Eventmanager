package com.example.eventmanager.api;

import com.example.eventmanager.api.request.*;
import com.example.eventmanager.api.response.*;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    
    // ==================== Authentication ====================
    
    @POST("auth/register/")
    Call<AuthResponse> register(@Body RegisterRequest request);
    
    @POST("auth/login/")
    Call<AuthResponse> login(@Body LoginRequest request);
    
    @POST("auth/forgot-password/")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);
    
    @POST("auth/verify-otp/")
    Call<BaseResponse> verifyOtp(@Body VerifyOtpRequest request);
    
    @POST("auth/reset-password/")
    Call<BaseResponse> resetPassword(@Body ResetPasswordRequest request);
    
    @GET("auth/profile/")
    Call<ProfileResponse> getProfile();
    
    @POST("auth/change-password/")
    Call<BaseResponse> changePassword(@Body ChangePasswordRequest request);
    
    // ==================== Events ====================
    
    @GET("events/")
    Call<EventListResponse> getEvents();
    
    @GET("events/{id}/")
    Call<EventDetailResponse> getEventDetail(@Path("id") int eventId);
    
    @POST("events/create/")
    Call<EventDetailResponse> createEvent(@Body CreateEventRequest request);
    
    @PUT("events/{id}/update/")
    Call<EventDetailResponse> updateEvent(@Path("id") int eventId, @Body CreateEventRequest request);
    
    @DELETE("events/{id}/delete/")
    Call<BaseResponse> deleteEvent(@Path("id") int eventId);
    
    // ==================== Registrations ====================
    
    @POST("events/{id}/register/")
    Call<RegistrationResponse> registerForEvent(@Path("id") int eventId, @Body EventRegistrationRequest request);
    
    @DELETE("events/{id}/cancel-registration/")
    Call<BaseResponse> cancelRegistration(@Path("id") int eventId);
    
    @GET("registrations/")
    Call<MyRegistrationsResponse> getMyRegistrations();
    
    // ==================== Reminders ====================
    
    @POST("events/{id}/set-reminder/")
    Call<ReminderResponse> setReminder(@Path("id") int eventId, @Body com.example.eventmanager.api.request.SetReminderRequest request);
    
    @DELETE("events/{id}/cancel-reminder/")
    Call<BaseResponse> cancelReminder(@Path("id") int eventId);
    
    @GET("reminders/")
    Call<MyRemindersResponse> getMyReminders();
    
    // ==================== Dashboard ====================
    
    @GET("dashboard/")
    Call<DashboardResponse> getDashboard();
    
    @GET("admin/dashboard/")
    Call<com.example.eventmanager.api.response.AdminDashboardResponse> getAdminDashboard();
}

