package com.example.eventmanager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.api.ApiConfig;
import com.example.eventmanager.api.ApiService;
import com.example.eventmanager.api.request.EventRegistrationRequest;
import com.example.eventmanager.api.response.RegistrationResponse;
import com.example.eventmanager.api.response.UserData;
import com.example.eventmanager.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterEventActivity extends AppCompatActivity {

    private TextView tvEventTitle;
    private EditText etName, etEmail, etPhone, etStudentId;
    private Button btnSubmitRegistration;
    
    private int eventId;
    private String eventTitle;
    
    private ApiService apiService;
    private SessionManager sessionManager;
    private UserData currentUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);

        // Initialize API and session
        apiService = ApiConfig.getApiService(this);
        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get event data from intent
        eventId = getIntent().getIntExtra("event_id", -1);
        eventTitle = getIntent().getStringExtra("event_title");
        
        if (eventId == -1) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting registration...");
        progressDialog.setCancelable(false);

        initViews();
        setupViews();
        setupButton();
    }

    private void initViews() {
        tvEventTitle = findViewById(R.id.tvEventTitle);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etStudentId = findViewById(R.id.etStudentId);
        btnSubmitRegistration = findViewById(R.id.btnSubmitRegistration);
    }

    private void setupViews() {
        // Set event title
        if (eventTitle != null) {
            tvEventTitle.setText(eventTitle);
        }
        
        // Pre-fill with current user data
        if (currentUser != null) {
            etName.setText(currentUser.getName());
            etEmail.setText(currentUser.getEmail());
        }
    }

    private void setupButton() {
        btnSubmitRegistration.setOnClickListener(v -> submitRegistration());
    }

    private void submitRegistration() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(studentId)) {
            etStudentId.setError("Student ID is required");
            etStudentId.requestFocus();
            return;
        }

        // Show progress dialog
        progressDialog.show();

        // Create registration request
        EventRegistrationRequest request = new EventRegistrationRequest(name, email, phone, studentId);

        // Make API call
        apiService.registerForEvent(eventId, request).enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    RegistrationResponse regResponse = response.body();

                    if (regResponse.isSuccess()) {
                        Toast.makeText(RegisterEventActivity.this, 
                            "Registration successful! You are now registered for this event.", 
                            Toast.LENGTH_LONG).show();
                        
                        // Set result to notify previous activity
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(RegisterEventActivity.this, 
                            regResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Registration failed";
                    if (response.code() == 400) {
                        errorMsg = "You are already registered or event is full";
                    }
                    Toast.makeText(RegisterEventActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterEventActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
