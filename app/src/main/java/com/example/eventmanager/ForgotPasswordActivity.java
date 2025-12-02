package com.example.eventmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.api.ApiConfig;
import com.example.eventmanager.api.ApiService;
import com.example.eventmanager.api.request.ForgotPasswordRequest;
import com.example.eventmanager.api.request.ResetPasswordRequest;
import com.example.eventmanager.api.request.VerifyOtpRequest;
import com.example.eventmanager.api.response.BaseResponse;
import com.example.eventmanager.api.response.ForgotPasswordResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    // Step 1: Email
    private View step1Email;
    private EditText etEmail;
    private Button btnSendOTP;
    
    // Step 2: OTP
    private View step2OTP;
    private EditText etOTP;
    private Button btnVerifyOTP;
    
    // Step 3: New Password
    private View step3Password;
    private EditText etNewPassword, etConfirmPassword;
    private Button btnResetPassword;
    
    private TextView tvBackToLogin;
    private ApiService apiService;
    private ProgressDialog progressDialog;
    
    private String userEmail;
    private String verifiedOTP;
    private int currentStep = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize API service
        apiService = ApiConfig.getApiService(this);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        initViews();
        setupClickListeners();
        showStep(1);
    }

    private void initViews() {
        // Step 1 views
        step1Email = findViewById(R.id.step1Email);
        etEmail = findViewById(R.id.etEmail);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        
        // Step 2 views
        step2OTP = findViewById(R.id.step2OTP);
        etOTP = findViewById(R.id.etOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        
        // Step 3 views
        step3Password = findViewById(R.id.step3Password);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void setupClickListeners() {
        btnSendOTP.setOnClickListener(v -> sendOTP());
        btnVerifyOTP.setOnClickListener(v -> verifyOTP());
        btnResetPassword.setOnClickListener(v -> resetPassword());
        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void showStep(int step) {
        currentStep = step;
        step1Email.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        step2OTP.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        step3Password.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
        
        // Show back to login only on step 1
        tvBackToLogin.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
    }

    private void sendOTP() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Sending OTP...");
        progressDialog.show();

        userEmail = email;
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        apiService.forgotPassword(request).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                progressDialog.dismiss();
                
                if (response.isSuccessful() && response.body() != null) {
                    ForgotPasswordResponse forgotResponse = response.body();
                    
                    if (forgotResponse.isSuccess()) {
                        Toast.makeText(ForgotPasswordActivity.this, 
                            "OTP has been sent to your email address. Please check your inbox.", 
                            Toast.LENGTH_LONG).show();
                        showStep(2);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, 
                            forgotResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, 
                        "Failed to send OTP. Please check your email address and try again.", 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOTP() {
        String enteredOTP = etOTP.getText().toString().trim();

        if (TextUtils.isEmpty(enteredOTP)) {
            Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredOTP.length() != 6) {
            Toast.makeText(this, "OTP must be 6 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Verifying OTP...");
        progressDialog.show();

        VerifyOtpRequest request = new VerifyOtpRequest(userEmail, enteredOTP);

        apiService.verifyOtp(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                progressDialog.dismiss();
                
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse verifyResponse = response.body();
                    
                    if (verifyResponse.isSuccess()) {
                        verifiedOTP = enteredOTP;
                        Toast.makeText(ForgotPasswordActivity.this, 
                            "OTP verified successfully", Toast.LENGTH_SHORT).show();
                        showStep(3);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, 
                            verifyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, 
                        "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, getString(R.string.password_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Resetting password...");
        progressDialog.show();

        ResetPasswordRequest request = new ResetPasswordRequest(
            userEmail, verifiedOTP, newPassword, confirmPassword
        );

        apiService.resetPassword(request).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                progressDialog.dismiss();
                
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse resetResponse = response.body();
                    
                    if (resetResponse.isSuccess()) {
                        Toast.makeText(ForgotPasswordActivity.this, 
                            "Password reset successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        finishAffinity();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, 
                            resetResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, 
                        "Failed to reset password. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
