package com.example.eventmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.eventmanager.api.ApiConfig;
import com.example.eventmanager.api.ApiService;
import com.example.eventmanager.api.request.LoginRequest;
import com.example.eventmanager.api.response.AuthResponse;
import com.example.eventmanager.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp, tvForgotPassword;
    private MaterialButton btnLoginAsAdmin, btnLoginAsStudent;
    private ApiService apiService;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;
    private String loginType = "student"; // "admin" or "student"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize API service and session manager
        apiService = ApiConfig.getApiService(this);
        sessionManager = new SessionManager(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLoginAsAdmin = findViewById(R.id.btnLoginAsAdmin);
        btnLoginAsStudent = findViewById(R.id.btnLoginAsStudent);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        // Set default selection
        updateLoginTypeButtons();

        // Role selection listeners
        btnLoginAsAdmin.setOnClickListener(v -> {
            loginType = "admin";
            updateLoginTypeButtons();
            // Auto-fill admin credentials for testing
            etEmail.setText("admin@university.edu");
            etPassword.setText("admin123");
        });

        btnLoginAsStudent.setOnClickListener(v -> {
            loginType = "student";
            updateLoginTypeButtons();
            // Clear fields for student login
            etEmail.setText("");
            etPassword.setText("");
        });

        // Button click listeners
        btnLogin.setOnClickListener(v -> loginUser());
        
        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
        
        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }

    private void updateLoginTypeButtons() {
        if (loginType.equals("admin")) {
            btnLoginAsAdmin.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.admin_color));
            btnLoginAsAdmin.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnLoginAsAdmin.setIconTint(ContextCompat.getColorStateList(this, R.color.white));
            btnLoginAsAdmin.setStrokeWidth(0);
            
            btnLoginAsStudent.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
            btnLoginAsStudent.setTextColor(ContextCompat.getColor(this, R.color.primary));
            btnLoginAsStudent.setIconTint(ContextCompat.getColorStateList(this, R.color.primary));
            btnLoginAsStudent.setStrokeColor(ContextCompat.getColorStateList(this, R.color.primary));
            btnLoginAsStudent.setStrokeWidth(2);
        } else {
            btnLoginAsStudent.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary));
            btnLoginAsStudent.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnLoginAsStudent.setIconTint(ContextCompat.getColorStateList(this, R.color.white));
            btnLoginAsStudent.setStrokeWidth(0);
            
            btnLoginAsAdmin.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
            btnLoginAsAdmin.setTextColor(ContextCompat.getColor(this, R.color.admin_color));
            btnLoginAsAdmin.setIconTint(ContextCompat.getColorStateList(this, R.color.admin_color));
            btnLoginAsAdmin.setStrokeColor(ContextCompat.getColorStateList(this, R.color.admin_color));
            btnLoginAsAdmin.setStrokeWidth(2);
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        progressDialog.show();

        // Create login request
        LoginRequest request = new LoginRequest(email, password, loginType);

        // Make API call
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progressDialog.dismiss();
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    if (authResponse.isSuccess()) {
                        // Save session
                        sessionManager.saveLoginSession(
                            authResponse.getTokens().getAccess(),
                            authResponse.getTokens().getRefresh(),
                            authResponse.getUser()
                        );
                        
                        String roleText = authResponse.getUser().isAdmin() ? "Admin" : "Student";
                        Toast.makeText(LoginActivity.this, 
                            "Login successful as " + roleText, Toast.LENGTH_SHORT).show();
                        
                        // Go to main activity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finishAffinity();
                    } else {
                        Toast.makeText(LoginActivity.this, 
                            authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle error response
                    String errorMessage = "Login failed. Please check your credentials.";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = "Invalid email, password or role selection";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
