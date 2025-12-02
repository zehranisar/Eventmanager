package com.example.eventmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.api.ApiConfig;
import com.example.eventmanager.api.ApiService;
import com.example.eventmanager.api.request.SetReminderRequest;
import com.example.eventmanager.api.response.BaseResponse;
import com.example.eventmanager.api.response.EventData;
import com.example.eventmanager.api.response.EventDetailResponse;
import com.example.eventmanager.api.response.RegistrationResponse;
import com.example.eventmanager.api.response.ReminderResponse;
import com.example.eventmanager.api.response.UserData;
import com.example.eventmanager.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDescription, tvDate, tvTime, tvLocation, tvCategory;
    private Button btnRegister, btnSetReminder, btnUpdateEvent, btnDeleteEvent;
    private int eventId;
    private ApiService apiService;
    private SessionManager sessionManager;
    private UserData currentUser;
    private ProgressDialog progressDialog;
    
    // Event data from intent
    private String eventTitle, eventDescription, eventDate, eventTime, eventLocation, eventCategory;
    private boolean isRegistered, hasReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Initialize API and session
        apiService = ApiConfig.getApiService(this);
        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getCurrentUser();

        if (currentUser == null) {
            finish();
            return;
        }

        // Get event data from intent
        eventId = getIntent().getIntExtra("event_id", -1);
        eventTitle = getIntent().getStringExtra("event_title");
        eventDescription = getIntent().getStringExtra("event_description");
        eventDate = getIntent().getStringExtra("event_date");
        eventTime = getIntent().getStringExtra("event_time");
        eventLocation = getIntent().getStringExtra("event_location");
        eventCategory = getIntent().getStringExtra("event_category");
        isRegistered = getIntent().getBooleanExtra("event_is_registered", false);
        hasReminder = getIntent().getBooleanExtra("event_has_reminder", false);

        if (eventId == -1) {
            finish();
            return;
        }

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        initViews();
        displayEventDetails();
        setupButtons();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvLocation = findViewById(R.id.tvLocation);
        tvCategory = findViewById(R.id.tvCategory);
        btnRegister = findViewById(R.id.btnRegister);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        btnUpdateEvent = findViewById(R.id.btnUpdateEvent);
        btnDeleteEvent = findViewById(R.id.btnDeleteEvent);
    }

    private void displayEventDetails() {
        tvTitle.setText(eventTitle != null ? eventTitle : "");
        tvDescription.setText(eventDescription != null ? eventDescription : "");
        tvDate.setText("Date: " + formatDate(eventDate));
        tvTime.setText("Time: " + formatTime(eventTime));
        tvLocation.setText("Location: " + (eventLocation != null ? eventLocation : ""));
        tvCategory.setText("Category: " + capitalizeFirst(eventCategory));
    }

    private void setupButtons() {
        if (currentUser.isAdmin()) {
            // Admin view: Show update and delete buttons, hide register/reminder buttons
            btnRegister.setVisibility(View.GONE);
            btnSetReminder.setVisibility(View.GONE);
            
            if (btnUpdateEvent != null) {
                btnUpdateEvent.setVisibility(View.VISIBLE);
                btnUpdateEvent.setOnClickListener(v -> updateEvent());
            }
            
            if (btnDeleteEvent != null) {
                btnDeleteEvent.setVisibility(View.VISIBLE);
                btnDeleteEvent.setOnClickListener(v -> confirmDeleteEvent());
            }
        } else {
            // Student view: Show register/reminder buttons, hide update/delete buttons
            if (btnUpdateEvent != null) {
                btnUpdateEvent.setVisibility(View.GONE);
            }
            if (btnDeleteEvent != null) {
                btnDeleteEvent.setVisibility(View.GONE);
            }
            
            // Setup register button
            updateRegisterButton();
            
            // Setup reminder button
            updateReminderButton();
        }
    }

    private void updateRegisterButton() {
        if (isRegistered) {
            btnRegister.setText("✓ Registered");
            btnRegister.setEnabled(false);
            btnRegister.setAlpha(0.6f);
        } else {
            btnRegister.setText("Register for Event");
            btnRegister.setEnabled(true);
            btnRegister.setAlpha(1.0f);
            btnRegister.setOnClickListener(v -> registerForEvent());
        }
    }

    private void updateReminderButton() {
        if (hasReminder) {
            btnSetReminder.setText("✓ Reminder Set");
            btnSetReminder.setEnabled(false);
            btnSetReminder.setAlpha(0.6f);
        } else {
            btnSetReminder.setText("Set Reminder");
            btnSetReminder.setEnabled(true);
            btnSetReminder.setAlpha(1.0f);
            btnSetReminder.setOnClickListener(v -> setReminder());
        }
    }

    private static final int REQUEST_REGISTRATION = 100;

    private void registerForEvent() {
        // Open registration form
        Intent intent = new Intent(EventDetailActivity.this, RegisterEventActivity.class);
        intent.putExtra("event_id", eventId);
        intent.putExtra("event_title", eventTitle);
        startActivityForResult(intent, REQUEST_REGISTRATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_REGISTRATION && resultCode == RESULT_OK) {
            // Registration was successful
            isRegistered = true;
            updateRegisterButton();
        }
    }

    private void setReminder() {
        // Show dialog for reminder timing selection
        showReminderTimingDialog();
    }
    
    private void showReminderTimingDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_reminder, null);
        
        RadioGroup rgTiming = dialogView.findViewById(R.id.rgReminderTiming);
        RadioButton rb1Day = dialogView.findViewById(R.id.rb1Day);
        RadioButton rb12Hours = dialogView.findViewById(R.id.rb12Hours);
        RadioButton rb6Hours = dialogView.findViewById(R.id.rb6Hours);
        RadioButton rb3Hours = dialogView.findViewById(R.id.rb3Hours);
        RadioButton rb1Hour = dialogView.findViewById(R.id.rb1Hour);
        RadioButton rb30Minutes = dialogView.findViewById(R.id.rb30Minutes);
        RadioButton rb15Minutes = dialogView.findViewById(R.id.rb15Minutes);
        
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSetReminder = dialogView.findViewById(R.id.btnSetReminder);
        
        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        
        // Cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        // Set reminder button
        btnSetReminder.setOnClickListener(v -> {
            // Get selected timing
            String timing = "1_day"; // default
            int selectedId = rgTiming.getCheckedRadioButtonId();
            
            if (selectedId == R.id.rb1Day) {
                timing = "1_day";
            } else if (selectedId == R.id.rb12Hours) {
                timing = "12_hours";
            } else if (selectedId == R.id.rb6Hours) {
                timing = "6_hours";
            } else if (selectedId == R.id.rb3Hours) {
                timing = "3_hours";
            } else if (selectedId == R.id.rb1Hour) {
                timing = "1_hour";
            } else if (selectedId == R.id.rb30Minutes) {
                timing = "30_minutes";
            } else if (selectedId == R.id.rb15Minutes) {
                timing = "15_minutes";
            }
            
            dialog.dismiss();
            sendReminderRequest(timing);
        });
        
        dialog.show();
    }
    
    private void sendReminderRequest(String timing) {
        progressDialog.setMessage("Setting reminder...");
        progressDialog.show();

        SetReminderRequest request = new SetReminderRequest(timing);
        
        apiService.setReminder(eventId, request).enqueue(new Callback<ReminderResponse>() {
            @Override
            public void onResponse(Call<ReminderResponse> call, Response<ReminderResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    ReminderResponse reminderResponse = response.body();
                    
                    if (reminderResponse.isSuccess()) {
                        hasReminder = true;
                        updateReminderButton();
                        Toast.makeText(EventDetailActivity.this, 
                            reminderResponse.getMessage() != null ? 
                                reminderResponse.getMessage() : "Reminder set successfully!", 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EventDetailActivity.this, 
                            reminderResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EventDetailActivity.this, 
                        "Failed to set reminder", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReminderResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EventDetailActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEvent() {
        Intent intent = new Intent(EventDetailActivity.this, AddEventActivity.class);
        intent.putExtra("event_id", eventId);
        intent.putExtra("event_title", eventTitle);
        intent.putExtra("event_description", eventDescription);
        intent.putExtra("event_date", eventDate);
        intent.putExtra("event_time", eventTime);
        intent.putExtra("event_location", eventLocation);
        intent.putExtra("event_category", eventCategory);
        startActivity(intent);
    }

    private void confirmDeleteEvent() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteEvent())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEvent() {
        progressDialog.setMessage("Deleting event...");
        progressDialog.show();

        apiService.deleteEvent(eventId).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse deleteResponse = response.body();
                    
                    if (deleteResponse.isSuccess()) {
                        Toast.makeText(EventDetailActivity.this, 
                            "Event deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EventDetailActivity.this, 
                            deleteResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EventDetailActivity.this, 
                        "Failed to delete event", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EventDetailActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Format date from "2025-12-15" to "Dec 15, 2025"
    private String formatDate(String date) {
        if (date == null || date.isEmpty()) return "";
        try {
            String[] parts = date.split("-");
            if (parts.length == 3) {
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                   "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                int monthIndex = Integer.parseInt(parts[1]) - 1;
                return months[monthIndex] + " " + Integer.parseInt(parts[2]) + ", " + parts[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    // Format time from "14:00:00" to "2:00 PM"
    private String formatTime(String time) {
        if (time == null || time.isEmpty()) return "";
        try {
            String[] parts = time.split(":");
            if (parts.length >= 2) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                String amPm = hour >= 12 ? "PM" : "AM";
                if (hour > 12) hour -= 12;
                if (hour == 0) hour = 12;
                return hour + ":" + String.format("%02d", minute) + " " + amPm;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    // Capitalize first letter
    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
