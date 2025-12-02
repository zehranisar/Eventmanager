package com.example.eventmanager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.eventmanager.api.ApiConfig;
import com.example.eventmanager.api.ApiService;
import com.example.eventmanager.api.response.AdminDashboardResponse;
import com.example.eventmanager.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private ApiService apiService;
    private ProgressDialog progressDialog;
    
    // Statistics views
    private TextView tvTotalUsers, tvTotalStudents, tvTotalEvents, tvActiveEvents;
    private TextView tvTotalRegistrations, tvRecentRegistrations;
    
    // Containers
    private LinearLayout llEventsContainer, llUsersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize API
        apiService = ApiConfig.getApiService(this);
        
        // Check if user is admin
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getCurrentUser() == null || !sessionManager.getCurrentUser().isAdmin()) {
            Toast.makeText(this, "Only admins can access this dashboard", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        initViews();
        loadDashboardData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            loadDashboardData();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvTotalEvents = findViewById(R.id.tvTotalEvents);
        tvActiveEvents = findViewById(R.id.tvActiveEvents);
        tvTotalRegistrations = findViewById(R.id.tvTotalRegistrations);
        tvRecentRegistrations = findViewById(R.id.tvRecentRegistrations);
        
        llEventsContainer = findViewById(R.id.llEventsContainer);
        llUsersContainer = findViewById(R.id.llUsersContainer);
    }

    private void loadDashboardData() {
        progressDialog.setMessage("Loading dashboard...");
        progressDialog.show();

        apiService.getAdminDashboard().enqueue(new Callback<AdminDashboardResponse>() {
            @Override
            public void onResponse(Call<AdminDashboardResponse> call, Response<AdminDashboardResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    AdminDashboardResponse dashboardResponse = response.body();
                    
                    if (dashboardResponse.isSuccess()) {
                        displayStatistics(dashboardResponse);
                        displayEventRegistrations(dashboardResponse);
                        displayUserRegistrations(dashboardResponse);
                    } else {
                        Toast.makeText(AdminDashboardActivity.this, 
                            dashboardResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminDashboardActivity.this, 
                        "Failed to load dashboard", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminDashboardResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AdminDashboardActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayStatistics(AdminDashboardResponse response) {
        AdminDashboardResponse.DashboardStats stats = response.getStats();
        
        if (stats != null) {
            tvTotalUsers.setText(String.valueOf(stats.getTotalUsers()));
            tvTotalStudents.setText(String.valueOf(stats.getTotalStudents()));
            tvTotalEvents.setText(String.valueOf(stats.getTotalEvents()));
            tvActiveEvents.setText(String.valueOf(stats.getActiveEvents()));
            tvTotalRegistrations.setText(String.valueOf(stats.getTotalRegistrations()));
            tvRecentRegistrations.setText(String.valueOf(stats.getRecentRegistrations()));
        }
    }

    private void displayEventRegistrations(AdminDashboardResponse response) {
        llEventsContainer.removeAllViews();
        
        if (response.getEventRegistrationDetails() == null || response.getEventRegistrationDetails().isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No events found");
            emptyView.setPadding(16, 16, 16, 16);
            emptyView.setTextColor(0xFF999999);
            llEventsContainer.addView(emptyView);
            return;
        }

        for (AdminDashboardResponse.EventRegistrationDetail eventDetail : response.getEventRegistrationDetails()) {
            View eventView = LayoutInflater.from(this).inflate(R.layout.item_event_registration, llEventsContainer, false);
            
            TextView tvEventTitle = eventView.findViewById(R.id.tvEventTitle);
            TextView tvRegistrationCount = eventView.findViewById(R.id.tvRegistrationCount);
            TextView tvEventDetails = eventView.findViewById(R.id.tvEventDetails);
            LinearLayout llRegistrationsContainer = eventView.findViewById(R.id.llRegistrationsContainer);
            Button btnToggle = eventView.findViewById(R.id.btnToggleRegistrations);
            
            // Set event title
            tvEventTitle.setText(eventDetail.getEventTitle());
            
            // Set registration count
            int count = eventDetail.getRegistrationCount();
            tvRegistrationCount.setText(String.valueOf(count));
            
            // Change color based on count
            if (eventDetail.isFull()) {
                tvRegistrationCount.setBackgroundColor(0xFFdc3545); // Red for full
            } else if (count < 5) {
                tvRegistrationCount.setBackgroundColor(0xFFffc107); // Yellow for low
            } else {
                tvRegistrationCount.setBackgroundColor(0xFF417690); // Blue for normal
            }
            
            // Set event details
            String dateStr = formatDate(eventDetail.getEventDate());
            String timeStr = formatTime(eventDetail.getEventTime());
            String details = "📍 " + eventDetail.getEventLocation() + " | " +
                           "📅 " + dateStr + " at " + timeStr;
            tvEventDetails.setText(details);
            
            // Add registrations
            if (eventDetail.getRegistrations() != null && !eventDetail.getRegistrations().isEmpty()) {
                for (AdminDashboardResponse.RegistrationData registration : eventDetail.getRegistrations()) {
                    View regView = LayoutInflater.from(this).inflate(R.layout.item_registration_detail, llRegistrationsContainer, false);
                    
                    TextView tvUserName = regView.findViewById(R.id.tvUserName);
                    TextView tvUserEmail = regView.findViewById(R.id.tvUserEmail);
                    TextView tvRegistrationName = regView.findViewById(R.id.tvRegistrationName);
                    TextView tvRegistrationEmail = regView.findViewById(R.id.tvRegistrationEmail);
                    TextView tvRegistrationPhone = regView.findViewById(R.id.tvRegistrationPhone);
                    TextView tvStudentId = regView.findViewById(R.id.tvStudentId);
                    TextView tvRegisteredAt = regView.findViewById(R.id.tvRegisteredAt);
                    LinearLayout llDetails = regView.findViewById(R.id.llRegistrationDetails);
                    
                    tvUserName.setText(registration.getUserName());
                    tvUserEmail.setText(registration.getUserEmail());
                    
                    // Show registration details if available
                    boolean hasDetails = false;
                    if (!TextUtils.isEmpty(registration.getName())) {
                        tvRegistrationName.setText("Name: " + registration.getName());
                        tvRegistrationName.setVisibility(View.VISIBLE);
                        hasDetails = true;
                    }
                    if (!TextUtils.isEmpty(registration.getEmail())) {
                        tvRegistrationEmail.setText("Email: " + registration.getEmail());
                        tvRegistrationEmail.setVisibility(View.VISIBLE);
                        hasDetails = true;
                    }
                    if (!TextUtils.isEmpty(registration.getPhone())) {
                        tvRegistrationPhone.setText("Phone: " + registration.getPhone());
                        tvRegistrationPhone.setVisibility(View.VISIBLE);
                        hasDetails = true;
                    }
                    if (!TextUtils.isEmpty(registration.getStudentId())) {
                        tvStudentId.setText("Student ID: " + registration.getStudentId());
                        tvStudentId.setVisibility(View.VISIBLE);
                        hasDetails = true;
                    }
                    
                    if (hasDetails) {
                        llDetails.setVisibility(View.VISIBLE);
                    }
                    
                    if (!TextUtils.isEmpty(registration.getRegisteredAt())) {
                        tvRegisteredAt.setText("Registered: " + formatDateTime(registration.getRegisteredAt()));
                    }
                    
                    llRegistrationsContainer.addView(regView);
                }
            }
            
            // Toggle button
            btnToggle.setOnClickListener(v -> {
                if (llRegistrationsContainer.getVisibility() == View.GONE) {
                    llRegistrationsContainer.setVisibility(View.VISIBLE);
                    btnToggle.setText("Hide Registrations");
                } else {
                    llRegistrationsContainer.setVisibility(View.GONE);
                    btnToggle.setText("Show Registrations (" + count + ")");
                }
            });
            
            btnToggle.setText("Show Registrations (" + count + ")");
            
            llEventsContainer.addView(eventView);
        }
    }

    private void displayUserRegistrations(AdminDashboardResponse response) {
        llUsersContainer.removeAllViews();
        
        if (response.getUserRegistrationDetails() == null || response.getUserRegistrationDetails().isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No user registrations found");
            emptyView.setPadding(16, 16, 16, 16);
            emptyView.setTextColor(0xFF999999);
            llUsersContainer.addView(emptyView);
            return;
        }

        for (AdminDashboardResponse.UserRegistrationDetail userDetail : response.getUserRegistrationDetails()) {
            View userView = LayoutInflater.from(this).inflate(R.layout.item_user_registration, llUsersContainer, false);
            
            TextView tvUserName = userView.findViewById(R.id.tvUserName);
            TextView tvUserEmail = userView.findViewById(R.id.tvUserEmail);
            TextView tvRegistrationCount = userView.findViewById(R.id.tvRegistrationCount);
            LinearLayout llRegistrationsContainer = userView.findViewById(R.id.llRegistrationsContainer);
            Button btnToggle = userView.findViewById(R.id.btnToggleRegistrations);
            
            tvUserName.setText(userDetail.getUserName());
            tvUserEmail.setText(userDetail.getUserEmail());
            
            int count = userDetail.getRegistrationCount();
            tvRegistrationCount.setText(String.valueOf(count));
            
            // Add event registrations
            if (userDetail.getRegistrations() != null && !userDetail.getRegistrations().isEmpty()) {
                for (AdminDashboardResponse.RegistrationData registration : userDetail.getRegistrations()) {
                    View regView = LayoutInflater.from(this).inflate(R.layout.item_registration_detail, llRegistrationsContainer, false);
                    
                    TextView tvEventName = regView.findViewById(R.id.tvUserName);
                    TextView tvEventDetails = regView.findViewById(R.id.tvUserEmail);
                    TextView tvRegistrationName = regView.findViewById(R.id.tvRegistrationName);
                    TextView tvRegistrationEmail = regView.findViewById(R.id.tvRegistrationEmail);
                    TextView tvRegistrationPhone = regView.findViewById(R.id.tvRegistrationPhone);
                    TextView tvStudentId = regView.findViewById(R.id.tvStudentId);
                    TextView tvRegisteredAt = regView.findViewById(R.id.tvRegisteredAt);
                    LinearLayout llDetails = regView.findViewById(R.id.llRegistrationDetails);
                    
                    tvEventName.setText("🎯 " + registration.getEventTitle());
                    String dateStr = formatDate(registration.getEventDate());
                    String timeStr = formatTime(registration.getEventTime());
                    tvEventDetails.setText("📍 " + registration.getEventLocation() + " | " +
                                          "📅 " + dateStr + " at " + timeStr);
                    
                    // Show registration details if available
                    boolean hasDetails = false;
                    if (!TextUtils.isEmpty(registration.getName())) {
                        tvRegistrationName.setText("Name: " + registration.getName());
                        tvRegistrationName.setVisibility(View.VISIBLE);
                        hasDetails = true;
                    }
                    if (!TextUtils.isEmpty(registration.getEmail())) {
                        tvRegistrationEmail.setText("Email: " + registration.getEmail());
                        tvRegistrationEmail.setVisibility(View.VISIBLE);
                        hasDetails = true;
                    }
                    if (!TextUtils.isEmpty(registration.getPhone())) {
                        tvRegistrationPhone.setText("Phone: " + registration.getPhone());
                        tvRegistrationPhone.setVisibility(View.VISIBLE);
                        hasDetails = true;
                    }
                    if (!TextUtils.isEmpty(registration.getStudentId())) {
                        tvStudentId.setText("Student ID: " + registration.getStudentId());
                        tvStudentId.setVisibility(View.VISIBLE);
                        hasDetails = true;
                    }
                    
                    if (hasDetails) {
                        llDetails.setVisibility(View.VISIBLE);
                    }
                    
                    if (!TextUtils.isEmpty(registration.getRegisteredAt())) {
                        tvRegisteredAt.setText("Registered: " + formatDateTime(registration.getRegisteredAt()));
                    }
                    
                    llRegistrationsContainer.addView(regView);
                }
            }
            
            // Toggle button
            btnToggle.setOnClickListener(v -> {
                if (llRegistrationsContainer.getVisibility() == View.GONE) {
                    llRegistrationsContainer.setVisibility(View.VISIBLE);
                    btnToggle.setText("Hide Events");
                } else {
                    llRegistrationsContainer.setVisibility(View.GONE);
                    btnToggle.setText("Show Events (" + count + ")");
                }
            });
            
            btnToggle.setText("Show Events (" + count + ")");
            
            llUsersContainer.addView(userView);
        }
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return date != null ? outputFormat.format(date) : dateStr;
        } catch (ParseException e) {
            return dateStr;
        }
    }

    private String formatTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            Date time = inputFormat.parse(timeStr);
            return time != null ? outputFormat.format(time) : timeStr;
        } catch (ParseException e) {
            return timeStr;
        }
    }

    private String formatDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
            Date dateTime = inputFormat.parse(dateTimeStr);
            return dateTime != null ? outputFormat.format(dateTime) : dateTimeStr;
        } catch (ParseException e) {
            return dateTimeStr;
        }
    }
}

